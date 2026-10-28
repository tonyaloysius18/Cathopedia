#!/usr/bin/env python3
"""Builds the College of Cardinals data file and its photo credits.

Inputs:  docs/research/current-cardinals-<date>.csv (the verified list) and
         the Commons photo metadata JSON written while preparing the photos.
Outputs: shared/src/commonMain/composeResources/files/cardinals/cardinals.json
         (bundled, and also read live from main like the news feed).

Usage: build_cardinals.py <cardinals.csv> <photo-meta.json> <updated-iso>
"""
import csv, json, os, re, sys, unicodedata

sys.path.insert(0, os.path.dirname(__file__))
from countries import COUNTRIES, LANGS

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
OUT = os.path.join(ROOT, "shared/src/commonMain/composeResources/files/cardinals/cardinals.json")
PHOTOS = os.path.join(ROOT, "remote/cardinals/photos")

# Under 80 but without a vote: renounced the rights of a cardinal (Sept 2020).
EXCEPTIONS = {"giovanni_angelo_becciu": "renounced"}
CREATORS = {"Francis": "francis", "Benedict XVI": "benedict_xvi", "John Paul II": "john_paul_ii"}


def slug(name):
    n = unicodedata.normalize("NFKD", name).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", "_", n).strip("_")


def main(csv_path, meta_path, updated):
    rows = list(csv.DictReader(open(csv_path)))
    meta = {m["title"]: m for m in json.load(open(meta_path))}
    titles = list(meta)  # same order as the CSV (both come from the same table)
    assert len(titles) == len(rows), (len(titles), len(rows))
    cardinals = []
    for row, title in zip(rows, titles):
        m = meta[title]
        cid = m["id"]
        assert os.path.exists(os.path.join(PHOTOS, cid + ".webp")), cid
        assert row["country"] in COUNTRIES, row["country"]
        entry = {
            "id": cid,
            "name": row["name"],
            "country": row["country"],
            "born": row["born"],
            "order": row["order"],
            "consistory": row["consistory"],
            "createdBy": CREATORS[row["created_by"]],
            "office": row["office"],
            "pressBio": row["source"] if "press.vatican.va" in row["source"] else "",
            "wikipedia": "https://en.wikipedia.org/wiki/" + title.replace(" ", "_"),
            "photo": {
                "path": cid + ".webp",
                "author": m["artist"][:120],
                "license": m["license"],
                "licenseUrl": m.get("licenseUrl", ""),
                "source": m["page"],
            },
        }
        if cid in EXCEPTIONS:
            entry["nonElector"] = EXCEPTIONS[cid]
        cardinals.append(entry)
    data = {
        "schemaVersion": 1,
        "updated": updated,
        # v2 (2026-10-20): every portrait cut out onto the same cardinal-red backdrop.
        # v3 (2026-10-28): the same cut-outs on the app's emerald-and-gold backdrop; also bundled.
        # v4 (2026-10-28): uniform framing (face size and eye line), 22 photos swapped for red choir dress.
        "photosVersion": 4,
        "countries": {k: dict(zip(LANGS, v)) for k, v in sorted(COUNTRIES.items())},
        "cardinals": cardinals,
    }
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w") as f:
        json.dump(data, f, ensure_ascii=False, indent=1)
        f.write("\n")
    print(f"{len(cardinals)} cardinals -> {os.path.relpath(OUT, ROOT)} ({os.path.getsize(OUT) // 1024} KB)")


if __name__ == "__main__":
    main(*sys.argv[1:4])
