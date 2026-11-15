#!/usr/bin/env python3
"""Builds the apostolic nuncios data file.

Inputs:  docs/research/apostolic-nuncios-<date>.csv (one row per post the Holy See fills:
         post, kind, seat, since, envoy = the envoy's English Wikipedia title, blank if vacant)
         tools/nuncios/envoys.json (each envoy's name, birth date, nationality and photo credit)
Outputs: shared/src/commonMain/composeResources/files/nuncios/nuncios.json
         (bundled, and also read live from main like the cardinals list).

Usage: build_nuncios.py <nuncios.csv> <updated-iso>
"""
import csv, json, os, sys

sys.path.insert(0, os.path.dirname(__file__))
from places import PLACES, LANGS

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
OUT = os.path.join(ROOT, "shared/src/commonMain/composeResources/files/nuncios/nuncios.json")
PHOTOS = os.path.join(ROOT, "shared/src/commonMain/composeResources/files/nuncios/photos")
KINDS = {"nunciature", "nunciature_nonres", "delegation", "observer", "representative", "pontifical_representative"}
# The post an envoy lives at comes first; the others are accreditations from there.
RESIDENT = ["nunciature", "pontifical_representative", "observer", "representative", "delegation", "nunciature_nonres"]
PHOTOS_VERSION = 1
# Vacant nunciatures run by a chargé d'affaires rather than left empty.
CHARGE = {"Republic of China (Taiwan)"}


def post(row):
    assert row["post"] in PLACES, row["post"]
    assert row["kind"] in KINDS, row["kind"]
    assert not row["seat"] or row["seat"] in PLACES, row["seat"]
    p = {"place": row["post"], "kind": row["kind"], "since": row["since"]}
    if row["seat"]:
        p["seat"] = row["seat"]
    if row["post"] in CHARGE:
        p["headedBy"] = "charge_d_affaires"
    return p


def main(csv_path, updated):
    rows = list(csv.DictReader(open(csv_path)))
    envoys = {e["title"]: e for e in json.load(open(os.path.join(os.path.dirname(__file__), "envoys.json")))}
    posts, vacant, used = {}, [], set()
    for row in rows:
        if row["envoy"]:
            assert row["envoy"] in envoys, row["envoy"]
            posts.setdefault(row["envoy"], []).append(post(row))
        else:
            vacant.append(post(row))
    out = []
    for title, ps in posts.items():
        e = envoys[title]
        ps.sort(key=lambda p: (RESIDENT.index(p["kind"]), p["place"]))
        entry = {
            "id": (e.get("photo") or {}).get("id") or title.lower().replace(" ", "_"),
            "name": e["name"],
            "born": e.get("born") or "",
            "nationality": e.get("nationality") or "",
            "wikipedia": "https://en.wikipedia.org/wiki/" + title.replace(" ", "_"),
            "posts": ps,
        }
        assert not entry["nationality"] or entry["nationality"] in PLACES, entry["nationality"]
        if e.get("photo"):
            ph = e["photo"]
            assert os.path.exists(os.path.join(PHOTOS, ph["id"] + ".webp")), ph["id"]
            entry["photo"] = {"path": ph["id"] + ".webp", "author": ph["author"][:120], "license": ph["license"],
                              "licenseUrl": ph.get("licenseUrl", ""), "source": ph["page"]}
        out.append(entry)
        used.update(p["place"] for p in ps)
        used.add(entry["nationality"])
        used.update(p.get("seat", "") for p in ps)
    used.update(p["place"] for p in vacant)
    used.update(p.get("seat", "") for p in vacant)
    used.discard("")
    data = {
        "schemaVersion": 1,
        "updated": updated,
        "photosVersion": PHOTOS_VERSION,
        "places": {k: dict(zip(LANGS, PLACES[k])) for k in sorted(used)},
        "envoys": sorted(out, key=lambda e: e["posts"][0]["place"]),
        "vacant": sorted(vacant, key=lambda p: p["place"]),
    }
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w") as f:
        json.dump(data, f, ensure_ascii=False, indent=1)
        f.write("\n")
    print(f"{len(out)} envoys, {sum(len(p) for p in posts.values())} posts, {len(vacant)} vacant -> "
          f"{os.path.relpath(OUT, ROOT)} ({os.path.getsize(OUT) // 1024} KB)")


if __name__ == "__main__":
    main(*sys.argv[1:3])
