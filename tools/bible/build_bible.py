#!/usr/bin/env python3
"""Builds the bundled Bible reader text: the 73 books of the Catholic canon, in Catholic order.

Source: the World English Bible, "engwebu" edition (eBible.org, public domain), the same text the
Mass readings use (tools/liturgy/build_reading_texts.py), so the book codes and verse numbers match.
Esther is the Greek Esther (ESG, with the additions) and Daniel the Greek Daniel (DNG: the Song of the
Three, Susanna, Bel), as in Catholic Bibles. Baruch 6 is the Letter of Jeremiah.

The WEB is public domain; "World English Bible" is a trademark of eBible.org, used on condition that
the text is not changed. This script copies each verse exactly and never edits it.

Output, in shared/src/commonMain/composeResources/files/bible/web/:
  index.json      the books: code, name, short name, testament, group, verse counts per chapter
  <CODE>.json     one book: {"chapters": [{"v": [verse numbers], "t": [verse texts]}, ...]}

Run: python3 tools/bible/build_bible.py
"""
from __future__ import annotations

import json
import re
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CACHE = ROOT / "build" / "liturgy-cache" / "engwebu_vpl.zip"
SOURCE = "https://ebible.org/Scriptures/engwebu_vpl.zip"
# The same edition in USFM: used only for where paragraphs (and poetry lines) begin.
USFM_CACHE = ROOT / "build" / "liturgy-cache" / "engwebu_usfm.zip"
USFM_SOURCE = "https://ebible.org/Scriptures/engwebu_usfm.zip"
# VPL code -> USFM book id, where they differ.
USFM_ID = {"SOL": "SNG", "EZE": "EZK", "DNG": "DAG", "JOE": "JOL", "NAH": "NAM", "MAR": "MRK",
           "JOH": "JHN", "PHI": "PHP", "JAM": "JAS", "1JO": "1JN", "2JO": "2JN", "3JO": "3JN"}
# Markers that start a new block of text: paragraphs, poetry lines, list items.
BLOCK = re.compile(r"^\\(p|m|pi\d?|mi|pc|q\d?|qm\d?|li\d?|b|d)$")
OUT = ROOT / "shared" / "src" / "commonMain" / "composeResources" / "files" / "bible" / "web"

# (code in the WEB file, name, short name, testament, group), in the order of Catholic Bibles.
BOOKS = [
    ("GEN", "Genesis", "Gn", "ot", "pentateuch"),
    ("EXO", "Exodus", "Ex", "ot", "pentateuch"),
    ("LEV", "Leviticus", "Lv", "ot", "pentateuch"),
    ("NUM", "Numbers", "Nm", "ot", "pentateuch"),
    ("DEU", "Deuteronomy", "Dt", "ot", "pentateuch"),
    ("JOS", "Joshua", "Jos", "ot", "historical"),
    ("JDG", "Judges", "Jgs", "ot", "historical"),
    ("RUT", "Ruth", "Ru", "ot", "historical"),
    ("1SA", "1 Samuel", "1 Sm", "ot", "historical"),
    ("2SA", "2 Samuel", "2 Sm", "ot", "historical"),
    ("1KI", "1 Kings", "1 Kgs", "ot", "historical"),
    ("2KI", "2 Kings", "2 Kgs", "ot", "historical"),
    ("1CH", "1 Chronicles", "1 Chr", "ot", "historical"),
    ("2CH", "2 Chronicles", "2 Chr", "ot", "historical"),
    ("EZR", "Ezra", "Ezr", "ot", "historical"),
    ("NEH", "Nehemiah", "Neh", "ot", "historical"),
    ("TOB", "Tobit", "Tb", "ot", "historical"),
    ("JDT", "Judith", "Jdt", "ot", "historical"),
    ("ESG", "Esther", "Est", "ot", "historical"),
    ("1MA", "1 Maccabees", "1 Mc", "ot", "historical"),
    ("2MA", "2 Maccabees", "2 Mc", "ot", "historical"),
    ("JOB", "Job", "Jb", "ot", "wisdom"),
    ("PSA", "Psalms", "Ps", "ot", "wisdom"),
    ("PRO", "Proverbs", "Prv", "ot", "wisdom"),
    ("ECC", "Ecclesiastes", "Eccl", "ot", "wisdom"),
    ("SOL", "Song of Songs", "Sg", "ot", "wisdom"),
    ("WIS", "Wisdom", "Wis", "ot", "wisdom"),
    ("SIR", "Sirach", "Sir", "ot", "wisdom"),
    ("ISA", "Isaiah", "Is", "ot", "prophets"),
    ("JER", "Jeremiah", "Jer", "ot", "prophets"),
    ("LAM", "Lamentations", "Lam", "ot", "prophets"),
    ("BAR", "Baruch", "Bar", "ot", "prophets"),
    ("EZE", "Ezekiel", "Ez", "ot", "prophets"),
    ("DNG", "Daniel", "Dn", "ot", "prophets"),
    ("HOS", "Hosea", "Hos", "ot", "prophets"),
    ("JOE", "Joel", "Jl", "ot", "prophets"),
    ("AMO", "Amos", "Am", "ot", "prophets"),
    ("OBA", "Obadiah", "Ob", "ot", "prophets"),
    ("JON", "Jonah", "Jon", "ot", "prophets"),
    ("MIC", "Micah", "Mi", "ot", "prophets"),
    ("NAH", "Nahum", "Na", "ot", "prophets"),
    ("HAB", "Habakkuk", "Hb", "ot", "prophets"),
    ("ZEP", "Zephaniah", "Zep", "ot", "prophets"),
    ("HAG", "Haggai", "Hg", "ot", "prophets"),
    ("ZEC", "Zechariah", "Zec", "ot", "prophets"),
    ("MAL", "Malachi", "Mal", "ot", "prophets"),
    ("MAT", "Matthew", "Mt", "nt", "gospels"),
    ("MAR", "Mark", "Mk", "nt", "gospels"),
    ("LUK", "Luke", "Lk", "nt", "gospels"),
    ("JOH", "John", "Jn", "nt", "gospels"),
    ("ACT", "Acts of the Apostles", "Acts", "nt", "acts"),
    ("ROM", "Romans", "Rom", "nt", "pauline"),
    ("1CO", "1 Corinthians", "1 Cor", "nt", "pauline"),
    ("2CO", "2 Corinthians", "2 Cor", "nt", "pauline"),
    ("GAL", "Galatians", "Gal", "nt", "pauline"),
    ("EPH", "Ephesians", "Eph", "nt", "pauline"),
    ("PHI", "Philippians", "Phil", "nt", "pauline"),
    ("COL", "Colossians", "Col", "nt", "pauline"),
    ("1TH", "1 Thessalonians", "1 Thes", "nt", "pauline"),
    ("2TH", "2 Thessalonians", "2 Thes", "nt", "pauline"),
    ("1TI", "1 Timothy", "1 Tm", "nt", "pauline"),
    ("2TI", "2 Timothy", "2 Tm", "nt", "pauline"),
    ("TIT", "Titus", "Ti", "nt", "pauline"),
    ("PHM", "Philemon", "Phlm", "nt", "pauline"),
    ("HEB", "Hebrews", "Heb", "nt", "pauline"),
    ("JAM", "James", "Jas", "nt", "catholic_letters"),
    ("1PE", "1 Peter", "1 Pt", "nt", "catholic_letters"),
    ("2PE", "2 Peter", "2 Pt", "nt", "catholic_letters"),
    ("1JO", "1 John", "1 Jn", "nt", "catholic_letters"),
    ("2JO", "2 John", "2 Jn", "nt", "catholic_letters"),
    ("3JO", "3 John", "3 Jn", "nt", "catholic_letters"),
    ("JUD", "Jude", "Jude", "nt", "catholic_letters"),
    ("REV", "Revelation", "Rv", "nt", "revelation"),
]


def load_source() -> str:
    if not CACHE.exists():
        CACHE.parent.mkdir(parents=True, exist_ok=True)
        CACHE.write_bytes(urllib.request.urlopen(SOURCE).read())
    with zipfile.ZipFile(CACHE) as z:
        return z.read("engwebu_vpl.txt").decode("utf-8-sig")


def load_paragraphs() -> dict[str, dict[int, set[int]]]:
    """For each USFM book id: chapter -> verse numbers that begin a new paragraph or line."""
    if not USFM_CACHE.exists():
        USFM_CACHE.write_bytes(urllib.request.urlopen(USFM_SOURCE).read())
    out: dict[str, dict[int, set[int]]] = {}
    with zipfile.ZipFile(USFM_CACHE) as z:
        for name in z.namelist():
            if not name.endswith(".usfm"):
                continue
            text = z.read(name).decode("utf-8-sig")
            book_id, chapter, pending, starts = None, 0, False, {}
            for tok in re.finditer(r"\\(\w+)\*?(?:[ \t]+([^\s\\]+))?", text):
                marker, arg = tok.group(1), tok.group(2)
                if marker == "id":
                    book_id = arg
                elif marker == "c":
                    chapter, pending = int(arg), True
                elif marker == "v":
                    if pending and arg and arg.split("-")[0].isdigit():
                        starts.setdefault(chapter, set()).add(int(arg.split("-")[0]))
                    pending = False
                elif BLOCK.match("\\" + marker):
                    pending = True
            if book_id:
                out[book_id] = starts
    return out


def main() -> None:
    assert len(BOOKS) == 73, len(BOOKS)
    wanted = {b[0] for b in BOOKS}
    books: dict[str, dict[int, list[tuple[int, str]]]] = {code: {} for code in wanted}
    for line in load_source().splitlines():
        if not line.strip():
            continue
        m = re.match(r"^(\w{3}) (\d+):(\d+) ?(.*)$", line)
        if not m:
            raise SystemExit(f"unexpected line: {line[:60]}")
        code = m.group(1)
        # The WEB keeps a numbered but empty line for verses absent from the oldest manuscripts
        # (Acts 8:37, Luke 17:36, many in Sirach); the reader skips the number, like printed Bibles.
        if code in wanted and m.group(4).strip():
            books[code].setdefault(int(m.group(2)), []).append((int(m.group(3)), m.group(4).strip()))

    OUT.mkdir(parents=True, exist_ok=True)
    for old in OUT.glob("*.json"):
        old.unlink()
    paragraphs = load_paragraphs()
    index = []
    total = 0
    for code, name, short, testament, group in BOOKS:
        chapters = books[code]
        numbers = sorted(chapters)
        if numbers != list(range(1, len(numbers) + 1)):
            raise SystemExit(f"{code}: chapters not 1..n: {numbers}")
        out_chapters = []
        for c in numbers:
            verses = sorted(chapters[c])
            if any(t == "" for _, t in verses):
                raise SystemExit(f"{code} {c}: empty verse")
            nums = [v for v, _ in verses]
            # A paragraph that starts on a skipped (empty) verse moves to the next verse that exists.
            starts = set()
            for n in paragraphs.get(USFM_ID.get(code, code), {}).get(c, set()):
                later = [v for v in nums if v >= n]
                if later and later[0] != nums[0]:
                    starts.add(later[0])
            entry = {"v": nums, "t": [t for _, t in verses]}
            if starts:
                entry["p"] = sorted(starts)
            out_chapters.append(entry)
            total += len(verses)
        (OUT / f"{code}.json").write_text(
            json.dumps({"code": code, "chapters": out_chapters}, ensure_ascii=False, separators=(",", ":")),
            encoding="utf-8",
        )
        index.append({
            "code": code, "name": name, "short": short, "testament": testament, "group": group,
            "verses": [len(ch["v"]) for ch in out_chapters],
        })

    (OUT / "index.json").write_text(json.dumps({
        "translation": "World English Bible",
        "credit": "World English Bible (WEB), public domain. “World English Bible” is a trademark of eBible.org.",
        "books": index,
    }, ensure_ascii=False, indent=1), encoding="utf-8")
    size = sum(p.stat().st_size for p in OUT.glob("*.json"))
    print(f"{len(index)} books, {total} verses, {size / 1024 / 1024:.1f} MB -> {OUT.relative_to(ROOT)}")


if __name__ == "__main__":
    main()
