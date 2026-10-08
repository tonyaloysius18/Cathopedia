#!/usr/bin/env python3
"""Add localized titles and Gospel verses to the bundled daily-readings index.

`tools/generate_usccb_readings.py` produces English-only data. This script adds, in place:

* `titleTranslations[lang][englishTitle]` from `tools/translate/daily_titles/<lang>.txt`
  (one line per distinct English title, in first-appearance order), and
* `featuredVerse.localized[lang] = {text, translation}` for each day, taken from a
  public-domain Bible in that language. Languages without one (Tamil) get no text, and
  the app then shows the citation alone rather than English Scripture.

Stray USCCB footnote digits glued to titles ("Lenten Weekday5") are also removed.

Re-run after regenerating the English index; Bibles are cached in --cache.
"""

from __future__ import annotations

import argparse
import io
import json
import re
import urllib.request
import zipfile
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
INDEX = ROOT / "shared/src/commonMain/composeResources/files/content/daily_readings_2026.json"
TITLES = ROOT / "tools/translate/daily_titles"

# eBible.org VPL downloads (all public domain), plus the 1632 Gdańsk Bible for Polish.
EBIBLE = {
    "fr": ("fraLSG", "Louis Segond (1910)"),
    "it": ("ita1927", "Riveduta (1927)"),
    "de": ("deu1912", "Lutherbibel (1912)"),
    "es": ("spaRV1909", "Reina-Valera (1909)"),
    "pt": ("porbrbsl", "Bíblia Portuguesa Mundial"),
    "nl": ("nld1939", "Petrus Canisius-vertaling (1939)"),
}
POLISH = ("https://raw.githubusercontent.com/midvash/bible-data/main/versions/pl/bg/bg.json", "Biblia Gdańska (1632)")
BOOKS = {"Mt": ("MAT", "Matthew"), "Mk": ("MAR", "Mark"), "Lk": ("LUK", "Luke"), "Jn": ("JOH", "John")}  # eBible VPL codes
FOOTNOTE = re.compile(r"(?<=[A-Za-z)])\d+$")


def fetch(url: str, path: Path) -> bytes:
    if not path.exists():
        path.write_bytes(urllib.request.urlopen(url).read())
    return path.read_bytes()


def load_ebible(code: str, cache: Path) -> dict[str, str]:
    data = fetch(f"https://ebible.org/Scriptures/{code}_vpl.zip", cache / f"{code}.zip")
    with zipfile.ZipFile(io.BytesIO(data)) as archive:
        text = archive.read(f"{code}_vpl.txt").decode("utf-8")
    verses = {}
    for line in text.splitlines():
        ref, _, body = line.partition(" ")
        rest, _, body = body.partition(" ")
        verses[f"{ref} {rest}"] = body.strip()
    return verses


def load_polish(cache: Path) -> dict[str, str]:
    books = json.loads(fetch(POLISH[0], cache / "pl_bg.json"))["books"]
    by_name = {code: name for code, name in BOOKS.values()}
    verses = {}
    for book in books:
        code = next((c for c, n in by_name.items() if n == book["englishName"]), None)
        if not code:
            continue
        for chapter in book["chapters"]:
            for verse in chapter["verses"]:
                verses[f"{code} {chapter['chapter']}:{verse['number']}"] = verse["text"].strip()
    return verses


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--cache", type=Path, default=Path("/tmp/cathopedia-bibles"))
    args = parser.parse_args()
    args.cache.mkdir(parents=True, exist_ok=True)

    index = json.loads(INDEX.read_text())
    # The title files follow the raw (pre-cleanup) title order, footnote digits included.
    raw = list(dict.fromkeys(day.get("rawTitle", day["title"]) for day in index["days"]))
    for day in index["days"]:
        day.setdefault("rawTitle", day["title"])
        day["title"] = FOOTNOTE.sub("", day["rawTitle"])

    titles = {}
    for path in sorted(TITLES.glob("*.txt")):
        lines = path.read_text().splitlines()
        if len(lines) != len(raw):
            raise SystemExit(f"{path.name}: {len(lines)} lines, expected {len(raw)}")
        titles[path.stem] = {FOOTNOTE.sub("", en): local for en, local in zip(raw, lines)}
    index["titleTranslations"] = titles
    english = set(titles[next(iter(titles))]) if titles else set()

    bibles = {lang: (load_ebible(code, args.cache), name) for lang, (code, name) in EBIBLE.items()}
    bibles["pl"] = (load_polish(args.cache), POLISH[1])

    missing = 0
    for day in index["days"]:
        verse = day.get("featuredVerse")
        if not verse:
            continue
        abbreviation, chapter_verse = verse["citation"].split(" ")
        key = f"{BOOKS[abbreviation][0]} {chapter_verse}"
        localized = {}
        for lang, (text, name) in bibles.items():
            if key in text and text[key]:
                localized[lang] = {"text": text[key], "translation": name}
            else:
                missing += 1
        verse["localized"] = localized

    INDEX.write_text(json.dumps(index, ensure_ascii=False, indent=2) + "\n")
    print(f"titles: {len(english)} x {len(titles)} languages; missing verses: {missing}")


if __name__ == "__main__":
    main()
