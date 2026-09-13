#!/usr/bin/env python3
"""Fetch each encyclical's opening from vatican.va, for grounded summarising.

Keeps only what a summary needs — the Holy See's own subject line, the
addressee, and the first ~2500 characters — never the whole letter. Output is a
working file for the authoring pass, not shipped content.
"""
import json, os, re, html, time, urllib.request

UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Cathopedia-summaries"
OUT = "/tmp/encyclical_texts.json"
INDEX = "/tmp/encyclicals.json"

def visible(h):
    h = re.sub(r"(?is)<(script|style|noscript).*?</\1>", "", h)
    t = re.sub(r"(?s)<[^>]+>", " ", h)
    return re.sub(r"\s+", " ", html.unescape(t)).strip()

def extract(text, title):
    """Slice from the real heading, not the copy of the title in the page nav.

    Anchor on the "ENCYCLICAL ..." line that follows the heading and walk back to
    the title occurrence just before it; fall back to the first occurrence.
    """
    up = text.upper()
    anchor = re.search(r"ENCYCLICAL\s+(LETTER|OF\s+(POPE|HIS\s+HOLINESS))", up)
    i = -1
    if anchor:
        i = up.rfind(title.upper(), 0, anchor.start())
    if i < 0:
        i = up.find(title.upper())
    body = text[i:] if i >= 0 else text
    # "TITLE ENCYCLICAL OF POPE X ON SOMETHING To the Venerable Brothers..."
    subject = None
    m = re.search(r"(?i)\b(ENCYCLICAL[^.]{0,120}?)\s+(ON\s+[A-Z][A-Z\s,'\-]{4,90})", body[:600])
    if m:
        subject = re.sub(r"\s+", " ", m.group(2)).strip().rstrip(",")
    addressee = None
    m = re.search(r"(?i)\bTo\s+(the\s+)?(Venerable|Our|All|His|Patriarchs|Bishops)[^.]{0,200}\.", body[:1200])
    if m:
        addressee = re.sub(r"\s+", " ", m.group(0)).strip()
    return subject, addressee, body[:2500]

def main():
    entries = json.load(open(INDEX, encoding="utf-8"))
    done = json.load(open(OUT, encoding="utf-8")) if os.path.exists(OUT) else {}
    for n, e in enumerate(entries, 1):
        if e["slug"] in done:
            continue
        try:
            req = urllib.request.Request(e["url"], headers={"User-Agent": UA})
            h = urllib.request.urlopen(req, timeout=45).read().decode("utf-8", "replace")
        except Exception as ex:
            print(f"  !! {e['slug']}: {ex}", flush=True)
            done[e["slug"]] = {"error": str(ex)}
            continue
        done[e["slug"]] = {
            "title": e["title"], "pope": e["pope"], "date": e["date"],
            "raw": visible(h)[:7000],
        }
        if n % 20 == 0:
            json.dump(done, open(OUT, "w"), ensure_ascii=False)
            print(f"[{n}/{len(entries)}]", flush=True)
        time.sleep(1)
    json.dump(done, open(OUT, "w"), ensure_ascii=False, indent=1)
    ok = [v for v in done.values() if "error" not in v]
    print(f"fetched {len(ok)}/{len(entries)} (parsing happens in extract_subjects.py)")

main()
