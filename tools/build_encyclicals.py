#!/usr/bin/env python3
"""Generate content/documents/*.json for every encyclical on vatican.va.

Index data (title, pope, date, URL) is scraped from the Holy See's own listing
pages by tools/scrape_encyclicals.py. Entries that already carry a hand-written
Cathopedia summary keep it untouched; the rest get an honest placeholder that
says so and links to the source.
"""
import json, os, glob

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
DOCS = os.path.join(ROOT, "content", "documents")
INDEX = "/tmp/encyclicals.json"

MONTHS_EN = "January February March April May June July August September October November December".split()
MONTHS_FR = "janvier février mars avril mai juin juillet août septembre octobre novembre décembre".split()

def pope_names():
    """Display names per language, straight from content/popes."""
    out = {}
    for f in glob.glob(os.path.join(ROOT, "content", "popes", "*.json")):
        d = json.load(open(f, encoding="utf-8"))
        out[d["id"]] = {lang: t.get("name", d["id"]) for lang, t in d.get("text", {}).items()}
    return out

def write_relations(entries):
    """Link each encyclical to the pope who promulgated it.

    Rewrites only the pope->document rows, so hand-authored relations of every
    other kind survive a regeneration.
    """
    path = os.path.join(ROOT, "content", "relations.json")
    existing = json.load(open(path, encoding="utf-8"))
    kept = [
        r for r in existing
        if not (r["from"]["type"] == "pope" and r["to"]["type"] == "document")
    ]
    added = [
        {
            "from": {"type": "pope", "id": e["pope"]},
            "to": {"type": "document", "id": e["slug"]},
            "kind": "promulgated",
        }
        for e in entries
    ]
    json.dump(kept + added, open(path, "w", encoding="utf-8"), indent=2, ensure_ascii=False)
    open(path, "a").write("\n")
    print(f"relations: {len(kept)} kept, {len(added)} pope->encyclical written")


def main():
    names = pope_names()
    entries = json.load(open(INDEX, encoding="utf-8"))
    os.makedirs(DOCS, exist_ok=True)

    written = kept = 0
    for e in entries:
        path = os.path.join(DOCS, e["slug"] + ".json")
        y, m, d = (int(x) for x in e["date"].split("-"))
        pope_en = names.get(e["pope"], {}).get("en", e["pope"])
        pope_fr = names.get(e["pope"], {}).get("fr", pope_en)

        doc = {
            "id": e["slug"],
            "kind": "encyclical",
            "popeId": e["pope"],
            "promulgated": e["date"],
            "documentYear": y,
            "imageUrl": None,
            "sourceUrl": e["url"],
        }

        if os.path.exists(path):
            # Hand-written entry: keep its prose, refresh only the index fields.
            old = json.load(open(path, encoding="utf-8"))
            doc["text"] = old["text"]
            kept += 1
        else:
            doc["text"] = {
                "en": {
                    "name": e["title"],
                    "summary": f"Encyclical of {pope_en}, {MONTHS_EN[m-1]} {d}, {y}.",
                    "body": (
                        "Cathopedia has not yet published a summary of this encyclical. "
                        "The full text is available from the Holy See."
                    ),
                    "sourceAttribution": "Index data from vatican.va. Full text © Libreria Editrice Vaticana.",
                },
                "fr": {
                    "name": e["title"],
                    "summary": f"Encyclique de {pope_fr}, {d} {MONTHS_FR[m-1]} {y}.",
                    "body": (
                        "Cathopedia n'a pas encore publié de résumé de cette encyclique. "
                        "Le texte intégral est disponible auprès du Saint-Siège."
                    ),
                    "sourceAttribution": "Données d'index de vatican.va. Texte intégral © Libreria Editrice Vaticana.",
                },
            }
            written += 1

        json.dump(doc, open(path, "w", encoding="utf-8"), indent=2, ensure_ascii=False)
        open(path, "a").write("\n")

    write_relations(entries)

    print(f"{written} new, {kept} existing summaries preserved, {written + kept} total")
    by_pope = {}
    for e in entries:
        by_pope[e["pope"]] = by_pope.get(e["pope"], 0) + 1
    for p, n in sorted(by_pope.items(), key=lambda kv: -kv[1]):
        print(f"  {p:15} {n:3d}")

main()
