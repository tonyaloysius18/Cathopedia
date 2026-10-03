# Altar and Sacristy — image generation brief (v2, full scenes)

Two images for the "Altar and Sacristy" comparison article (Holy Mass hub,
section "Altar and Sacristy"). This replaces the first brief, which asked
for isolated still-life vignettes on transparent backgrounds — too sparse
next to the source infographic. This version asks for a full illustrated
**scene** for each side, matching the reference image's actual content: a
real dressed altar, and a real sacristy interior with its wardrobe and
furnishings — in the same "plate on warm ivory paper" house style already
used for `mass_church_map` and `mass_sanctuary_furnishings`.

| Cell | Asset | Status |
| --- | --- | --- |
| Altar scene | `mass_altar_scene.png` | **needed** |
| Sacristy scene | `mass_sacristy_scene.png` | **needed** |

## Rules for both

- **Plain warm cream / ivory background**, edge to edge, no border, no
  frame — the same paper tone as `mass_church_map.png` and
  `mass_sanctuary_furnishings.png`, not a transparent cutout this time.
  Both should sit on the same shade of cream so they read as a matched
  pair.
- **No text at all** — no titles, captions, labels, quotes, logos,
  watermarks or signature. The app supplies every name and description as
  text.
- Square **1:1, 1024×1024**, so both sit at the same size in the
  side-by-side "VS" layout.
- Classical, warm, reverent, gilded detail — the same house style as the
  rest of Cathopedia. Soft even light, no harsh shadows.
- Save as PNG (no transparency needed this time — a solid cream
  background is correct).

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, frame, border, cartoon, flat vector, low detail, modern
photograph, people, faces`

---

## `mass_altar_scene.png` — the dressed altar

A close, elegant view of a dressed altar — similar in spirit to the
reference photo: white cloth with a gold-embroidered edge, tall candles,
a crucifix, sacred vessels, and lilies at the base — but painted as an
original illustration, not copied from any source.

```
Create an original illustration: a close view of a beautifully dressed
Catholic altar, painted as an elegant classical scene on warm ivory paper.
Square format, 1:1.

Show a stone altar table draped in a white altar cloth with a gold-
embroidered border hanging down the front. On top of the altar, arranged
neatly: two tall brass candlesticks with lit candles on the outer edges, a
standing crucifix at the centre behind them, a golden chalice and a small
paten to one side, and a missal resting open on a small stand to the other
side. In front of the altar, at its base, a generous arrangement of white
Easter lilies and green leaves. A hint of the altar's stone platform and a
step or two beneath it is fine; do not show the rest of the church, the
walls, or the ceiling — let the altar and its dressing fill the frame.

Strictly NO text anywhere: no title, no captions, no labels, no legend, no
quotes, no logo, no watermark, no signature. Plain warm cream/ivory
background beyond the altar itself, no border, no frame. Reverent, calm,
classical illustration style, warm golden light, soft even shadows.
```

---

## `mass_sacristy_scene.png` — the sacristy interior

A cosy corner of a sacristy — an open wardrobe of hanging vestments, a
wooden cabinet of books and vessels, a crucifix on the wall, and a small
vesting table — again an original composition inspired by, not copied
from, the reference.

```
Create an original illustration: a cosy corner of a church sacristy,
painted as an elegant classical scene on warm ivory paper, matching the
altar scene in style and palette. Square format, 1:1.

Show an open wooden wardrobe on the left with three liturgical vestments
(chasubles) hanging inside — one green, one white, one violet — each on a
simple wooden hanger. Beside the wardrobe, a sturdy wooden cabinet with
open shelves holding a few golden chalices, ciboria and a small stack of
leather-bound liturgical books. On the wall above the cabinet, a plain
wooden crucifix, and beside it a small arched window letting in soft light.
In the foreground, a small wooden vesting table holding a large red-bound
missal and a brass ewer (water vessel). A hint of a stone or wood-panelled
wall is fine; do not show a full room, other furniture, or any people —
keep the composition close and warm, like a corner glimpsed through a
doorway.

Strictly NO text anywhere: no title, no captions, no labels, no legend, no
quotes, no logo, no watermark, no signature. Plain warm cream/ivory tone
in the light areas, no border, no frame. Reverent, calm, classical
illustration style, warm golden light, soft even shadows, matching the
altar scene's palette and finish.
```

---

## After generating

Drop both PNGs into `docs/image-prompts/mass/` (or send them in chat) and
tell me — I'll convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` under
`mass_altar_scene.webp` / `mass_sacristy_scene.webp`. The content already
references them (`content/hubs/mass.json`, article
`art.mass.altar_sacristy`), and no further content changes are needed —
just the two images.

If either image comes back with any lettering, ask for it again "with no
lettering anywhere at all."
