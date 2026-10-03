# Basilica or Cathedral? — image generation brief

Two images for the "Basilica or Cathedral?" comparison article (Holy Mass
hub, section 19). Same side-by-side "VS" layout as Cathedra or Presider's
Chair — the two buildings sit face-off, so they must read as a matched pair.

| Cell | Asset | Status |
| --- | --- | --- |
| Basilica | `mass_basilica.png` | **needed** |
| Cathedral | `mass_cathedral.png` | **needed** |

Both are **original, generic** buildings — not portraits of St Peter's or
any real cathedral. The article names real examples in text; the art only
needs to show the two architectural ideas clearly.

## Rules for both

- **Transparent background is required** — a PNG with a real alpha channel.
  The building stands on its own with at most a thin strip of paving at its
  base: no sky, no clouds, no trees, no street, no people.
- **No text, no labels, no watermark, no signature.**
- Square **1:1, 1024×1024**.
- Classical architectural illustration, warm golden light from the upper
  left, fine detail, reverent and still — matching the cathedra and chair.
- **Same scale, same three-quarter view, same light** for both, so they sit
  as a pair. Each building fills most of the frame with a small margin.

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, sky, clouds, trees, people, cars, street, city, landscape,
background scenery, cartoon, flat vector, low detail`

---

## `mass_basilica.png` — the basilica

A grand Renaissance/Baroque basilica: a great ribbed dome on a drum with a
lantern and cross, a wide classical façade with columns and a pediment, two
smaller side domes, a balustrade with statues along the roofline. Add the
two small marks of a basilica's papal bond as discreet details: a crossed-
keys emblem carved over the central door, and a small red-and-gold striped
canopy (umbraculum) half-open beside the entrance.

```
Create an original illustration: a grand Renaissance-Baroque basilica,
three-quarter view, centered, on a plain transparent background. Warm
cream-and-honey stone, a large ribbed grey dome on a columned drum topped
by a lantern and a small cross, two smaller domes to either side, a wide
classical façade with tall paired columns, a central pediment, and a
balustrade lined with small statues along the roof. Over the central door,
a small carved emblem of two crossed keys; beside the entrance, a small
half-open canopy striped in red and gold. Only a thin strip of paving at
the base. Warm golden light from the upper left, fine architectural detail,
reverent, classical illustration style. No sky, no clouds, no trees, no
people, no text, no labels, no watermark, no signature, no background at
all — export with a transparent alpha channel. Square 1:1.
```

## `mass_cathedral.png` — the cathedral

A Gothic cathedral: twin pointed spires flanking a façade with a large rose
window, pointed-arch portals, pinnacles and flying buttresses visible along
the side. Same stone palette and light as the basilica so they pair.

```
Create an original illustration: a Gothic cathedral, three-quarter view,
centered, on a plain transparent background, matching a companion basilica
illustration in scale, angle and light. Warm cream-and-honey stone, two
tall pointed spires flanking the west front, a large round rose window
above three deep pointed-arch portals, slender pinnacles, tall lancet
windows, and flying buttresses along the side of the nave. Only a thin
strip of paving at the base. Warm golden light from the upper left, fine
architectural detail, reverent, classical illustration style. No sky, no
clouds, no trees, no people, no text, no labels, no watermark, no
signature, no background at all — export with a transparent alpha channel.
Square 1:1.
```

---

## After generating

Drop both PNGs into `docs/image-prompts/mass/` and tell me — I'll convert
them to WebP into `shared/src/commonMain/composeResources/drawable/`. The
content already references them (`content/hubs/mass.json`, article
`art.mass.basilica_cathedral`, `CONTENT_VERSION` already bumped to 114).
