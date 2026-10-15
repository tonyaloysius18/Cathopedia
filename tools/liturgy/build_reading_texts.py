#!/usr/bin/env python3
"""Bundle the Scripture text of every lectionary reference in the offline Ordo.

Source: the World English Bible, "engwebu" edition (eBible.org, public domain; uses "LORD"
rather than the divine name, which Catholic liturgy does not pronounce). It has every book of
the Catholic canon, with the Greek forms of Esther and Daniel, and RSV-style numbering, the same
as the references in lectionary.json (Dn 3 numbered 1-97 with the Prayer of Azariah at 24-90).

The text is used verbatim, whole verses only: a reference to part of a verse ("34a") brings the
whole verse, and is marked partial. The WEB asks that changed text not be called the WEB, so the
wording is never edited. This is a study text, not an approved liturgical translation.

Output: files/content/ordo/reading_texts_web.json
  {"verses": {"MAT 22:15": "...", ...}, "citations": {"Mt 22:15-21": {"r": [["MAT", 22, 15, 21]], "p": false}}}

Usage:  python3 tools/liturgy/build_reading_texts.py   (run build_ordo.py first)
"""

from __future__ import annotations

import io
import json
import re
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CACHE = ROOT / "build" / "liturgy-cache"
ORDO = ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "content" / "ordo"
SOURCE = "https://ebible.org/Scriptures/engwebu_vpl.zip"

BOOKS = {
    "Gn": "GEN", "Ex": "EXO", "Lv": "LEV", "Nm": "NUM", "Dt": "DEU", "Jos": "JOS", "Jgs": "JDG",
    "Ru": "RUT", "1 Sm": "1SA", "2 Sm": "2SA", "1 Kgs": "1KI", "2 Kgs": "2KI", "1 Chr": "1CH",
    "2 Chr": "2CH", "Ezra": "EZR", "Neh": "NEH", "Tb": "TOB", "Jdt": "JDT", "Est": "ESG",
    "1 Mc": "1MA", "2 Mc": "2MA", "Jb": "JOB", "Ps": "PSA", "Prv": "PRO", "Eccl": "ECC",
    "Sg": "SOL", "Wis": "WIS", "Sir": "SIR", "Is": "ISA", "Jer": "JER", "Lam": "LAM", "Bar": "BAR",
    "Ez": "EZE", "Dn": "DNG", "Hos": "HOS", "Jl": "JOE", "Am": "AMO", "Ob": "OBA", "Jon": "JON",
    "Mi": "MIC", "Na": "NAH", "Hb": "HAB", "Zep": "ZEP", "Hg": "HAG", "Zec": "ZEC", "Mal": "MAL",
    "Mt": "MAT", "Mk": "MAR", "Lk": "LUK", "Jn": "JOH", "Acts": "ACT", "Rom": "ROM", "1 Cor": "1CO",
    "2 Cor": "2CO", "Gal": "GAL", "Eph": "EPH", "Phil": "PHI", "Col": "COL", "1 Thes": "1TH",
    "2 Thes": "2TH", "1 Tm": "1TI", "2 Tm": "2TI", "Ti": "TIT", "Phlm": "PHM", "Heb": "HEB",
    "Jas": "JAM", "1 Pt": "1PE", "2 Pt": "2PE", "1 Jn": "1JO", "2 Jn": "2JO", "3 Jn": "3JO",
    "Jude": "JUD", "Rv": "REV",
}
# One-chapter books are cited by verse alone ("Phlm 9b-10, 12-17").
ONE_CHAPTER = {"OBA", "PHM", "2JO", "3JO", "JUD"}
BOOK_RE = re.compile(r"^(" + "|".join(sorted(map(re.escape, BOOKS), key=len, reverse=True)) + r")\s+(.*)$")


def load_bible() -> dict[tuple[str, int, int], str]:
    cached = CACHE / "engwebu_vpl.zip"
    if not cached.exists():
        cached.parent.mkdir(parents=True, exist_ok=True)
        cached.write_bytes(urllib.request.urlopen(SOURCE).read())
    with zipfile.ZipFile(cached) as z:
        text = z.read("engwebu_vpl.txt").decode("utf-8-sig")
    verses = {}
    for line in text.splitlines():
        m = re.match(r"^(\w{3}) (\d+):(\d+) (.*)$", line)
        if m:
            verses[(m.group(1), int(m.group(2)), int(m.group(3)))] = m.group(4).strip()
    return verses


# The WEB prints the doxology of Romans after 14:23, where some manuscripts have it.
VERSE_ALIASES = {("ROM", 16, 25): ("ROM", 14, 24), ("ROM", 16, 26): ("ROM", 14, 25), ("ROM", 16, 27): ("ROM", 14, 26)}


def parse(citation: str, bible) -> tuple[list[list], bool] | None:
    """'Is 52:13-53:12' -> ([['ISA', 52, 13, 15], ['ISA', 53, 1, 12]], partial?) or None."""
    citation = citation.removeprefix("Cf. ").strip()
    # "Mal 1:14b-2:1-2" is printed that way in the source: 1:14b through 2:2.
    citation = citation.replace("1:14b-2:1-2", "1:14b-2:2")
    ranges: list[list] = []
    partial = False
    book = None
    chapter = None
    for part in [p.strip() for p in citation.split(";") if p.strip()]:
        m = BOOK_RE.match(part)
        if m:
            book, part = BOOKS[m.group(1)], m.group(2)
        if book is None:
            return None
        if book in ONE_CHAPTER and ":" not in part:
            part = f"1:{part}"
        if ":" in part.split(",")[0]:
            ch, _, part = part.partition(":")
            if not ch.strip().isdigit():
                return None
            chapter = int(ch)
        if chapter is None:
            return None
        for item in [i.strip() for i in part.split(",") if i.strip()]:
            if re.fullmatch(r"[a-z]+", item):
                partial = True  # another part of the previous verse ("4a, c")
                continue
            switch = re.match(r"^(\d+):(.*)$", item)
            if switch:  # a new chapter inside a comma list: "Hb 1:2-3, 2:2-4"
                chapter, item = int(switch.group(1)), switch.group(2)
            if book == "ESG" and re.search(r"[a-z]", item):
                return None  # lettered Greek Esther verses (4:17a-z) are numbered differently in the WEB
            item = re.sub(r"^(\d+)([a-z]+)-([a-z]+)$", r"\1\2", item)  # "12b-d" is part of verse 12
            m = re.fullmatch(r"(\d+)([a-z]*)(?:-(?:(\d+):)?(\d+)([a-z]*))?", item)
            if not m:
                return None  # e.g. the lettered Greek Esther verses "17k-m"
            start = int(m.group(1))
            partial |= bool(m.group(2)) or bool(m.group(5))
            end_ch = int(m.group(3)) if m.group(3) else chapter
            end = int(m.group(4)) if m.group(4) else start
            if end_ch == chapter:
                ranges.append([book, chapter, start, end])
            else:
                last = max(v for (b, c, v) in bible if b == book and c == chapter)
                ranges.append([book, chapter, start, last])
                for c in range(chapter + 1, end_ch):
                    ranges.append([book, c, 1, max(v for (b, cc, v) in bible if b == book and cc == c)])
                ranges.append([book, end_ch, 1, end])
                chapter = end_ch
    return ranges, partial


def alias_ranges(ranges: list[list]) -> list[list]:
    """Moves aliased verses (Rom 16:25-27) to where the WEB prints them, keeping each range
    separate so a psalm's stanzas stay distinct."""
    out = []
    for b, c, s, e in ranges:
        piece: list[list] = []
        for v in range(s, e + 1):
            tb, tc, tv = VERSE_ALIASES.get((b, c, v), (b, c, v))
            if piece and piece[-1][:2] == [tb, tc] and piece[-1][3] == tv - 1:
                piece[-1][3] = tv
            else:
                piece.append([tb, tc, tv, tv])
        out.extend(piece)
    return out


def main() -> None:
    bible = load_bible()
    lectionary = json.loads((ORDO / "lectionary.json").read_text())
    citations: set[str] = set()

    def walk(node):
        if isinstance(node, dict):
            if node.get("kind") == "SEQUENCE":
                return
            if "citation" in node:
                citations.add(node["citation"])
            for value in node.values():
                walk(value)
        elif isinstance(node, list):
            for value in node:
                walk(value)

    walk(lectionary)
    out_citations: dict[str, dict] = {}
    used: dict[str, str] = {}
    unresolved: list[str] = []
    for citation in sorted(citations):
        parsed = parse(citation, bible)
        if not parsed:
            unresolved.append(citation)
            continue
        ranges, partial = parsed
        ranges = alias_ranges(ranges)
        missing = [(b, c, v) for b, c, s, e in ranges for v in range(s, e + 1) if (b, c, v) not in bible]
        if missing or not ranges:
            unresolved.append(f"{citation} (missing {missing[:2]})")
            continue
        for b, c, s, e in ranges:
            for v in range(s, e + 1):
                used[f"{b} {c}:{v}"] = bible[(b, c, v)]
        out_citations[citation] = {"r": [f"{b} {c}:{st}-{en}" for b, c, st, en in ranges], "p": partial}

    (ORDO / "reading_texts_web.json").write_text(json.dumps({
        "translation": "World English Bible (public domain)",
        "note": "Study text, not an approved liturgical translation. Whole verses only.",
        "verses": used,
        "citations": out_citations,
    }, ensure_ascii=False, separators=(",", ":")))
    print(f"citations: {len(citations)}, with text: {len(out_citations)}, verses: {len(used)}, unresolved: {len(unresolved)}")
    for u in unresolved:
        print("  unresolved:", u)


if __name__ == "__main__":
    main()
