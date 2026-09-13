#!/usr/bin/env python3
"""Turn the mirrored crawl into Cathopedia gallery assets + a manifest.

Reads  scratchpad/em/manifest.json + scratchpad/slug_map.json
Writes scratchpad/out/drawable/miracle_<slug>_g<N>.webp
       scratchpad/out/galleries.json   (our id -> [{res, caption, alt, source}])
"""
import json, os, re
from PIL import Image

HERE = os.path.dirname(os.path.abspath(__file__))
EM = os.path.join(HERE, "em")
OUT = HERE
DRW = os.path.join(HERE, "out", "drawable")
MAXDIM = 1000          # gallery images are tapped-to-zoom, not full-bleed heroes
QUALITY = 80

def main():
    man = json.load(open(os.path.join(EM, "manifest.json")))
    smap = json.load(open(os.path.join(HERE, "slug_map.json")))
    os.makedirs(DRW, exist_ok=True)

    galleries, total, skipped = {}, 0, []
    for ours in sorted(smap):
        entries, seen = [], set()
        for theirs in smap[ours]:
            for im in man.get(theirs, []):
                if im["src"] in seen:      # ger-walldurn repeats a few tiles
                    continue
                seen.add(im["src"])
                entries.append((theirs, im))
        if not entries:
            skipped.append(ours); continue

        res_base = "miracle_" + ours.replace("-", "_")
        out = []
        for n, (theirs, im) in enumerate(entries):
            src = os.path.join(EM, im["file"])
            name = f"{res_base}_g{n}"
            dst = os.path.join(DRW, name + ".webp")
            if not os.path.exists(dst):
                img = Image.open(src)
                if img.mode not in ("RGB", "RGBA"):
                    img = img.convert("RGB")
                w, h = img.size
                if max(w, h) > MAXDIM:
                    s = MAXDIM / max(w, h)
                    img = img.resize((round(w * s), round(h * s)), Image.LANCZOS)
                img.save(dst, "WEBP", quality=QUALITY, method=6)
            total += os.path.getsize(dst)
            # The hover caption is the factual one; alt is a visual description.
            # Some tiles reuse alt as caption - keep only one in that case.
            cap = im["caption"].strip()
            alt = im["alt"].strip()
            out.append({
                "res": name,
                "caption": cap,
                "alt": "" if alt == cap else alt,
                "source": f"https://eucharisticmiracles.faith/miracle/{theirs}",
            })
        galleries[ours] = out

    json.dump(galleries, open(os.path.join(OUT, "galleries.json"), "w"),
              indent=2, ensure_ascii=False, sort_keys=True)
    n = sum(len(v) for v in galleries.values())
    print(f"{len(galleries)} galleries, {n} images, {total/1e6:.1f} MB webp")
    print("no gallery:", skipped)
    counts = sorted((len(v), k) for k, v in galleries.items())
    print("smallest:", counts[:5])
    print("largest:", counts[-5:])

main()
