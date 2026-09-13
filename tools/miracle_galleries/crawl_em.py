#!/usr/bin/env python3
"""Mirror eucharisticmiracles.faith miracle galleries into the scratchpad.

Politeness: 1s crawl-delay between page fetches (their robots.txt asks for it),
0.25s between image fetches. Resumable - skips anything already on disk.
"""
import json, os, re, time, urllib.request, urllib.error, html

BASE = "https://eucharisticmiracles.faith"
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "em")
UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 Cathopedia-mirror"

def get(url, binary=False):
    req = urllib.request.Request(url, headers={"User-Agent": UA})
    with urllib.request.urlopen(req, timeout=45) as r:
        d = r.read()
    return d if binary else d.decode("utf-8", "replace")

def slugs():
    sm = get(BASE + "/sitemap.xml")
    return [u.rsplit("/", 1)[1] for u in re.findall(r"<loc>([^<]+)</loc>", sm) if "/miracle/" in u]

# each gallery tile: <button ...><img src=".." alt=".." ...><div ...>caption</div></div></button>
TILE = re.compile(
    r'<img src="(/images/miracles/[^"]+)" alt="([^"]*)"[^>]*>'
    r'.*?line-clamp-2">(.*?)</div>', re.S)

def parse(page):
    out = []
    for src, alt, cap in TILE.findall(page):
        cap = re.sub(r"<[^>]+>", "", cap).strip()
        out.append({"src": src, "alt": html.unescape(alt).strip(), "caption": html.unescape(cap)})
    return out

def main():
    os.makedirs(OUT, exist_ok=True)
    sl = slugs()
    print(f"{len(sl)} miracle pages", flush=True)
    manifest, nimg, nbytes = {}, 0, 0
    for n, s in enumerate(sl, 1):
        hp = os.path.join(OUT, f"{s}.html")
        if os.path.exists(hp):
            page = open(hp, encoding="utf-8").read()
        else:
            try:
                page = get(f"{BASE}/miracle/{s}")
            except Exception as e:
                print(f"  !! page {s}: {e}", flush=True); continue
            open(hp, "w", encoding="utf-8").write(page)
            time.sleep(1.0)
        imgs = parse(page)
        d = os.path.join(OUT, "img", s)
        os.makedirs(d, exist_ok=True)
        for im in imgs:
            fn = os.path.join(d, im["src"].rsplit("/", 1)[1])
            if not os.path.exists(fn):
                try:
                    b = get(BASE + im["src"], binary=True)
                except Exception as e:
                    print(f"  !! img {im['src']}: {e}", flush=True); continue
                open(fn, "wb").write(b)
                time.sleep(0.25)
            im["file"] = os.path.relpath(fn, OUT)
            im["bytes"] = os.path.getsize(fn)
            nbytes += im["bytes"]; nimg += 1
        manifest[s] = imgs
        print(f"[{n}/{len(sl)}] {s}: {len(imgs)} images", flush=True)
    json.dump(manifest, open(os.path.join(OUT, "manifest.json"), "w"), indent=2, ensure_ascii=False)
    print(f"DONE {nimg} images, {nbytes/1e6:.1f} MB across {len(manifest)} miracles", flush=True)

main()
