#!/usr/bin/env python3
"""Generate Cathopedia's compact daily-reading index from a USCCB calendar PDF.

The input is the annual "Liturgical Calendar for the Dioceses of the United
States of America" PDF.  Only factual metadata (date, celebration title, and
Scripture citations) is emitted; the copyrighted lectionary text is not copied.
"""

from __future__ import annotations

import argparse
import calendar
import json
import re
from dataclasses import dataclass
from datetime import date
from pathlib import Path

from pypdf import PdfReader


MONTHS = {name.upper(): number for number, name in enumerate(calendar.month_name) if name}
DAY_RE = re.compile(r"^(\d{1,2})\s+(SUN|Sun|Mon|Tue|Wed|Thu|Fri|Sat)\s+(.+)$")
COLOR_RE = re.compile(
    r"\s+(?:(?:green|white|red|violet|rose|black)(?:\s+or\s+)?)+"
    r"(?:/(?:green|white|red|violet|rose|black))*\s*$",
    re.IGNORECASE,
)
LECTIONARY_RE = re.compile(r"\s+\([^()]+\)\s*(?:Pss\s+\S+)?\s*$")
CITATION_START_RE = re.compile(
    r"(?:[123]\s+)?[A-Z][a-z]{0,5}\s+(?:[A-Z]:)?\d",
)
RANKS = {"Feast", "Memorial", "Solemnity"}
SMALL_WORDS = {"a", "an", "and", "in", "of", "or", "the", "to", "within"}
GOSPEL_BOOKS = {"Mt": "Matt", "Mk": "Mark", "Lk": "Luke", "Jn": "John"}
VERSE_TRANSLATION = "Douay-Rheims American Edition (1899)"


@dataclass
class Block:
    month: int
    day: int
    weekday: str
    lines: list[str]


def display_title(raw: str) -> str:
    if not raw.isupper():
        return raw
    words = raw.lower().split()
    result: list[str] = []
    for index, word in enumerate(words):
        if word.rstrip(":") == "usa":
            result.append("USA" + (":" if word.endswith(":") else ""))
            continue
        parts = word.split("-")
        rendered_parts = []
        for part_index, part in enumerate(parts):
            capitalize = index == 0 or part_index > 0 or part not in SMALL_WORDS
            rendered_parts.append(part.capitalize() if capitalize else part)
        result.append("-".join(rendered_parts))
    return " ".join(result)


def title_for(block: Block) -> str:
    title = display_title(COLOR_RE.sub("", block.lines[0]).strip())
    rank = None
    for line in block.lines[1:4]:
        clean = line.strip()
        if clean.startswith(("[", "Any readings")) or CITATION_START_RE.search(clean):
            break
        if any(clean.startswith(candidate) for candidate in RANKS):
            rank = next(candidate for candidate in RANKS if clean.startswith(candidate))
            break
        if clean and not clean.isdigit():
            title += " " + COLOR_RE.sub("", clean).strip()

    title = " ".join(title.split())
    if rank == "Memorial" and title.startswith(("Saint ", "Saints ")):
        title = f"Memorial of {title}"
    return title


def reading_line_for(block: Block) -> str:
    candidates: list[tuple[str, str]] = []
    pending = ""
    pending_label = ""
    for source_line in block.lines[1:]:
        line = " ".join(source_line.split())
        citation_match = CITATION_START_RE.search(line)
        if citation_match:
            pending_label = line[: citation_match.start()].strip()
            pending = line[citation_match.start() :]
        elif pending and not LECTIONARY_RE.search(pending) and line:
            pending += " " + line
        else:
            continue

        if LECTIONARY_RE.search(pending):
            candidates.append((pending_label, pending))
            pending = ""
            pending_label = ""

    if not candidates:
        raise ValueError(f"No reading citations found for {block.month:02}-{block.day:02}: {block.lines}")

    # Christmas and Easter publish several Mass formularies. The daytime Mass
    # is the least time-sensitive default for a compact all-day home card.
    complete = [(label, line) for label, line in candidates if "/" in line]
    daytime = [line for label, line in complete if label == "Day:"]
    return (daytime or [line for _, line in complete])[-1]


def references_for(block: Block) -> list[dict[str, str]]:
    if any("Any readings from" in line for line in block.lines):
        return []
    line = reading_line_for(block)
    line = LECTIONARY_RE.sub("", line).strip()
    line = re.sub(r"\s+\([0-9A-Z-]+\)(?=/|$)", "", line)
    pieces = [piece.strip() for piece in line.split("/")]
    # Palm Sunday begins with a separate procession Gospel before the three
    # Mass readings. The compact Home card intentionally shows the latter.
    if len(pieces) > 3:
        pieces = pieces[-3:]
    if len(pieces) < 2:
        raise ValueError(
            f"Expected at least a first reading and Gospel for "
            f"{block.month:02}-{block.day:02}: {line}"
        )

    labels = ["FIRST_READING", "GOSPEL"] if len(pieces) == 2 else [
        "FIRST_READING",
        *(["SECOND_READING"] * (len(pieces) - 2)),
        "GOSPEL",
    ]
    return [{"kind": kind, "citation": citation} for kind, citation in zip(labels, pieces)]


def extract_blocks(pdf_path: Path, year: int) -> list[Block]:
    blocks: list[Block] = []
    current_month = None
    current = None

    for page in PdfReader(str(pdf_path)).pages:
        for raw_line in (page.extract_text() or "").splitlines():
            line = " ".join(raw_line.split())
            month_match = re.fullmatch(r"([A-Z]+)\s+(\d{4})", line)
            if month_match and int(month_match.group(2)) == year:
                current_month = MONTHS.get(month_match.group(1))
                continue

            day_match = DAY_RE.match(line)
            if day_match and current_month is not None:
                if current is not None:
                    blocks.append(current)
                current = Block(
                    month=current_month,
                    day=int(day_match.group(1)),
                    weekday=day_match.group(2).title(),
                    lines=[day_match.group(3)],
                )
            elif current is not None:
                current.lines.append(line)

    if current is not None:
        blocks.append(current)
    return blocks


def load_gospels(paths: list[Path]) -> dict[str, dict]:
    gospels = {}
    for path in paths:
        book = json.loads(path.read_text(encoding="utf-8"))
        gospels[book["book"]] = book
    missing = set(GOSPEL_BOOKS.values()) - set(gospels)
    if missing:
        raise ValueError(f"Missing Gospel JSON files: {', '.join(sorted(missing))}")
    return gospels


def featured_verse_for(
    readings: list[dict[str, str]],
    gospels: dict[str, dict],
    fallback_citation: str | None = None,
) -> dict[str, str] | None:
    gospel = next((item for item in readings if item["kind"] == "GOSPEL"), None)
    citation = gospel["citation"] if gospel is not None else fallback_citation
    if citation is None:
        return None

    match = re.match(r"(Mt|Mk|Lk|Jn)\s+(\d+):(\d+)", citation)
    if match is None:
        raise ValueError(f"Cannot locate first Gospel verse in {citation}")

    abbreviation, chapter_number, verse_number = match.groups()
    book = gospels[GOSPEL_BOOKS[abbreviation]]
    chapter = next(item for item in book["chapters"] if item["chapter"] == int(chapter_number))
    verse = next(item for item in chapter["verses"] if item["number"] == int(verse_number))
    return {
        "citation": f"{abbreviation} {chapter_number}:{verse_number}",
        "text": verse["text"],
        "translation": VERSE_TRANSLATION,
    }


def generate(pdf_path: Path, year: int, source: str, gospels: dict[str, dict]) -> dict:
    # The US calendar prints two May 14/17 variants because a handful of
    # provinces observe Ascension on Thursday. Keeping the later entry selects
    # the "all other U.S. ecclesiastical provinces" (Sunday transfer) default.
    by_date: dict[str, dict] = {}
    for block in extract_blocks(pdf_path, year):
        day_date = date(year, block.month, block.day)
        if day_date.strftime("%a") != block.weekday:
            raise ValueError(f"Weekday mismatch for {day_date}: PDF says {block.weekday}")
        readings = references_for(block)
        # All Souls permits a broad choice from the Masses for the Dead. USCCB's
        # published representative selection uses John 6:37-40 as the Gospel.
        fallback_verse = "Jn 6:37" if (block.month, block.day) == (11, 2) else None
        by_date[day_date.isoformat()] = {
            "date": day_date.isoformat(),
            "title": title_for(block),
            "readings": readings,
            "featuredVerse": featured_verse_for(readings, gospels, fallback_verse),
        }

    days = [by_date[key] for key in sorted(by_date)]

    expected = 366 if calendar.isleap(year) else 365
    if len(days) != expected:
        raise ValueError(f"Expected {expected} unique days, found {len(days)}")

    return {
        "calendar": "USCCB",
        "year": year,
        "source": source,
        "verseSource": "https://github.com/midvash/bible-data/tree/main/versions/en/dra",
        "verseTranslation": VERSE_TRANSLATION,
        "days": days,
    }


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("pdf", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--year", type=int, required=True)
    parser.add_argument("--source", required=True)
    parser.add_argument(
        "--gospel-json",
        type=Path,
        action="append",
        required=True,
        help="Repeat for the Douay-Rheims Matt, Mark, Luke, and John JSON files.",
    )
    args = parser.parse_args()

    payload = generate(args.pdf, args.year, args.source, load_gospels(args.gospel_json))
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_text(json.dumps(payload, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    print(f"Wrote {len(payload['days'])} days to {args.output}")


if __name__ == "__main__":
    main()
