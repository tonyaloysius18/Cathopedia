# Altar Table, Altar Frontal, and Retablo — image generation brief

Three images for the new "Altar Table, Altar Frontal, and Retablo" section
(Holy Mass hub, section 18), one per term — but only **two are actually
needed**:

| Term | Asset | Status |
| --- | --- | --- |
| Altar Table | `mass_altar_scene.png` | **already in the app** — reused as-is |
| Altar Frontal | `mass_altar_frontal.png` | **needed** |
| Retablo | `mass_retablo.png` | **needed** |

The "Altar Table" image is the same dressed-altar scene already used for
"Altar or Sacristy?" (candles, crucifix, chalice, missal, white cloth,
lilies) — it already shows exactly what that callout describes, so there's
no need to generate a near-duplicate. Only the frontal and the retablo are
new.

Same house style as the rest of the Mass hub's sacred-object and scene
images: classical academic sacred oil painting, warm golden light,
reverent and still. **No text, no labels, no watermark, no signature.**

## Rules for both new images

- **Transparent background is required** — a PNG with a real alpha
  channel, isolated on transparency, no church interior, no floor.
- Square **1:1, 1024×1024**.
- Warm, reverent, gilded detail, matching the finish of the app's other
  sacred-object images.

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, full room, church interior, floor, people, cartoon, flat
vector, low detail`

---

## `mass_altar_frontal.png` — the altar frontal (antependium)

A single rectangular panel of liturgical fabric — the decorative covering
that hangs on the front face of the altar — shown on its own, not attached
to a altar, so it reads clearly as "the frontal" rather than "the altar."

```
Create an original illustration: a single rectangular liturgical altar
frontal (antependium) panel, shown frontally and upright, centered on a
plain transparent background, as if displayed on its own rather than
mounted on an altar. Rich green fabric with a gold-embroidered border along
all four edges, and a large gold embroidered cross at the centre with
scrolling vine ornament radiating from it. Slight natural fabric folds and
sheen to show it is cloth, not a flat graphic. Warm golden light, fine
embroidery detail, classical sacred-textile illustration style. No altar
structure around it, no stand, no background at all. No text, no labels,
no watermark, no signature — export with a transparent alpha channel.
Square 1:1.
```

## `mass_retablo.png` — the retablo (reredos)

A tall gilded Gothic altarpiece structure: a central canopied niche with a
crucifix, flanked by two smaller niches each holding a saint's statue —
shown as a standalone architectural object, matching the scale and style
of the app's other single-object sacred images (e.g. the cathedra).

```
Create an original illustration: a tall gilded Gothic retablo (reredos),
centered and upright, on a plain transparent background. A richly carved
and gilded wooden altarpiece structure with pointed arches and fine
tracery: a tall central canopied niche holding a crucifix, flanked by two
smaller side niches each containing a small statue of a saint (one in
blue, one in simple robes), with slender pinnacles and gold filigree
throughout. Three-quarter or frontal view, freestanding — not attached to
an altar table beneath it. Warm golden light, fine gilded detail, reverent
and dignified, classical sacred-architecture illustration style. No altar
table, no floor, no church walls, no background at all. No text, no
labels, no watermark, no signature — export with a transparent alpha
channel. Square 1:1.
```

---

## Deliverable

2 **transparent PNG** files, square 1:1 (1024×1024), named exactly:
`mass_altar_frontal.png`, `mass_retablo.png`. Hand them back as a batch,
then drop them into `docs/image-prompts/mass/` (or send them in chat) and
tell me — I'll convert them to WebP into
`shared/src/commonMain/composeResources/drawable/`. The content already
references all three assets (`content/hubs/mass.json`, article
`art.mass.altar_parts`, `CONTENT_VERSION` already bumped to 113) — the
"Altar Table" image needs no action since it's already in the app.
