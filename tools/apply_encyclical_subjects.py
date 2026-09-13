#!/usr/bin/env python3
"""Put the Holy See's own subject line into each encyclical's summary.

Replaces the "Encyclical of X, date." placeholder with what the document itself
says it is about — sourced, not invented. Entries that already carry a written
Cathopedia summary are left alone.
"""
import json, os, re

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
DOCS = os.path.join(ROOT, "content", "documents")
TEXTS = "/tmp/encyclical_texts.json"

SMALL = {"a", "an", "and", "as", "at", "by", "for", "from", "in", "of", "on",
         "or", "the", "to", "upon", "with", "against", "concerning"}

# The universal address formula carries no information; a named country or
# person does, so only the latter is worth surfacing.
UNIVERSAL = re.compile(
    r"(?i)patriarchs,?\s+primates|catholic world|whole world|everywhere|"
    r"all the (faithful|bishops|patriarchs)")

def title_case(s):
    words = s.lower().split()
    out = []
    for i, w in enumerate(words):
        out.append(w if (w in SMALL and i) else w[:1].upper() + w[1:])
    return " ".join(out)

def addressee_clause(a):
    if not a or UNIVERSAL.search(a):
        return ""
    a = re.sub(r"(?i)^to\s+", "", a).strip().rstrip(".")
    a = re.sub(r"(?i)^(our\s+)?(venerable\s+)?(beloved\s+)?(brothers?|brethren|sons?),?\s*", "", a)
    a = re.sub(r"\s+", " ", a).strip().rstrip(",.")
    if not a or len(a) > 90:
        return ""
    return f" — to {a[:1].lower() + a[1:]}"

# Run-together words in the source that cannot be split programmatically.
CORRECTIONS = {
    "On Catholicismin the United States.": "On Catholicism in the United States.",
    "On the Latin American Bishops' Plenarycouncil.": "On the Latin American Bishops' Plenary Council.",
    "On Propagation of the Faith and Easternchurches.": "On Propagation of the Faith and Eastern Churches.",
    # vatican.va's own heading misspells "Hundredth".
    "On the Hundreth Anniversary of Rerum Novarum.": "On the Hundredth Anniversary of Rerum Novarum.",
}


def main():
    texts = json.load(open(TEXTS, encoding="utf-8"))
    changed = skipped = nosubject = 0
    for slug, t in texts.items():
        path = os.path.join(DOCS, slug + ".json")
        if not os.path.exists(path) or "error" in t:
            continue
        doc = json.load(open(path, encoding="utf-8"))
        en = doc["text"]["en"]
        if not en["body"].startswith("Cathopedia has not yet published"):
            skipped += 1        # hand-written entry; leave it be
            continue
        if not t.get("subject"):
            nosubject += 1
            continue
        subject = title_case(t["subject"])
        summary = subject + "."
        en["summary"] = CORRECTIONS.get(summary, summary)
        json.dump(doc, open(path, "w", encoding="utf-8"), indent=2, ensure_ascii=False)
        open(path, "a").write("\n")
        changed += 1
    print(f"{changed} summaries taken from the source, {skipped} written entries untouched, "
          f"{nosubject} have no subject line on vatican.va")

main()
