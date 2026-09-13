#!/usr/bin/env python3
"""Build the encyclical index from the Holy See's own listing pages.

Titles, dates and URLs only — factual listing data. The letters themselves stay
on vatican.va; see content/documents/README.md.
"""
import re, json, time, urllib.request, unicodedata, os

UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) Cathopedia-index"
# vatican.va path segment -> our content/popes slug
POPES = [
    ("leo-xiii", "leo-xiii"), ("pius-x", "pius-x"), ("benedict-xv", "benedict-xv"),
    ("pius-xi", "pius-xi"), ("pius-xii", "pius-xii"), ("john-xxiii", "john-xxiii"),
    ("paul-vi", "paul-vi"), ("john-paul-i", "john-paul-i"),
    ("john-paul-ii", "john-paul-ii"), ("benedict-xvi", "benedict-xvi"),
    ("francesco", "francis"), ("leo-xiv", "leo-xiv"),
]

# Leo XIV's listing wraps the title in a full formula:
# "Encyclical Letter of His Holiness Leo XIV Magnifica Humanitas (15 May 2026)".
TITLE_PREFIX = re.compile(
    r"^(?:Encyclical(?:\s+Letter)?|Lettera\s+enciclica)"
    r"(?:\s+of\s+His\s+Holiness)?\s+(?:Pope\s+)?"
    r"(?:Leo|Francis|Benedict|John\s+Paul|John|Paul|Pius)\s+[IVX]+\s+",
    re.I,
)
MONTHS = {m: i + 1 for i, m in enumerate(
    "January February March April May June July August September October November December".split())}

def get(url):
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    return urllib.request.urlopen(req, timeout=45).read().decode("utf-8", "replace")

def slugify(t):
    t = unicodedata.normalize("NFKD", t).encode("ascii", "ignore").decode().lower()
    t = re.sub(r"[^a-z0-9]+", "-", t).strip("-")
    return t

out, unparsed = [], []
for vat, ours in POPES:
    url = f"https://www.vatican.va/content/{vat}/en/encyclicals.index.html"
    try:
        h = get(url)
    except Exception as e:
        print(f"  !! {vat}: {e}"); continue
    seen, found = set(), 0
    for m in re.finditer(r'href="([^"]*encyclicals/documents/[^"]+\.html)"[^>]*>(.*?)</a>', h, re.S):
        href, label = m.group(1), re.sub(r"\s+", " ", re.sub(r"<[^>]+>", "", m.group(2))).strip()
        # Two date styles across the site: "April 17, 2003" and "17 April 2003".
        d = re.match(r"^(.+?)\s*\(\s*([A-Z][a-z]+)\s+(\d{1,2}),\s*(\d{4})\)\s*$", label)
        if d:
            title, mon, day, year = d.group(1).strip(), d.group(2), int(d.group(3)), int(d.group(4))
        else:
            d = re.match(r"^(.+?)\s*\((\d{1,2})\s+([A-Z][a-z]+)\s+(\d{4})\)\s*$", label)
            if d:
                title, day, mon, year = d.group(1).strip(), int(d.group(2)), d.group(3), int(d.group(4))
            else:
                # vatican.va misplaces the comma on at least one entry:
                # "Pacem in Terris (April, 11 1963)". Accept that shape too.
                d = re.match(r"^(.+?)\s*\(([A-Z][a-z]+),\s*(\d{1,2})\s+(\d{4})\)\s*$", label)
                if not d:
                    if len(label) > 3 and not label.isupper():
                        unparsed.append((vat, label))
                    continue              # the EN/IT/LA language chips carry no title
                title, mon, day, year = d.group(1).strip(), d.group(2), int(d.group(3)), int(d.group(4))
        # Leo XIV's entries carry the full formula around the title; strip it.
        title = TITLE_PREFIX.sub("", title).strip()
        if mon not in MONTHS:
            continue
        full = href if href.startswith("http") else "https://www.vatican.va" + href
        if full in seen:
            continue
        seen.add(full); found += 1
        out.append({
            "slug": slugify(title), "title": title, "pope": ours,
            "date": f"{year:04d}-{MONTHS[mon]:02d}-{day:02d}", "year": year, "url": full,
        })
    print(f"{ours:15} {found:3d}")
    time.sleep(1)

json.dump(out, open("/tmp/encyclicals.json", "w"), indent=1, ensure_ascii=False)
print("TOTAL", len(out), "unique slugs", len({e['slug'] for e in out}))
if unparsed:
    print("UNPARSED labels (check these):")
    for v, l in unparsed:
        print(f"  {v}: {l!r}")
