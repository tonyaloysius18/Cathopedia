# Types of Relics — image generation brief

Three images for **Types of Relics** (Explore → Catechism → What We Believe →
Types of Relics), one per class. The article already points at these paths, so
the page picks them up as soon as the files exist; until then each card shows
its number in the image slot.

| Slot | Asset |
| --- | --- |
| Entry 1 · First-class relics | `relic_first_class.png` |
| Entry 2 · Second-class relics | `relic_second_class.png` |
| Entry 3 · Third-class relics | `relic_third_class.png` |

The `symbol_*` set was checked first and has nothing usable: it is emblems, and
this page needs the actual objects. `symbol_monstrance`, `symbol_rosary` and
`symbol_miraculous_medal` are close but would be misleading here, so the prompts
steer away from all three.

House style: classical academic sacred oil painting, still life, fine detail,
warm golden light from the upper left, rich earthy palette, reverent and quiet.
**No text, no labels, no watermark, no signature.**

## Rules for all three

- **Square 1:1 at 512×512**, like the `symbol_*` files.
- **Transparent background is required**: a PNG with a real alpha channel. No
  table, no altar, no cloth backdrop, no shadow plane, no frame.
- **One compact object group, centred, filling about 75% of the frame.** The app
  shows these as 72dp tiles cropped to a rounded square, so fine detail at the
  edges is lost. The silhouette has to read at thumbnail size.
- **Generate them as a matched set**, with the same light, scale, painterly
  finish and camera height. They sit one above the other in a single column.

**Negative prompt (all three):** `text, letters, words, labels, watermark,
signature, frame, border, background scenery, table, altar, church interior,
floor, shadow plane, people, hands, skull, skeleton, gore, cartoon, flat vector,
low detail, sunburst rays`

---

## `relic_first_class.png`: parts of the saint's body

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **small gilded Gothic
reliquary**: a shrine-shaped case of engraved gold on a short footed base, with
a pointed arch roof, tiny pinnacles and a small cross on top. At its centre is a
**round glass window** showing a **small fragment of bone resting on crimson
silk**, with a thin gold filigree border around the glass. Rich, jewel-like and
reverent. Centred, upright. 1:1.

*Notes:* the relic is a **small, discreet fragment** behind glass. It should
never be a skull, a hand, a whole bone or anything macabre. The reliquary must
not read as a monstrance, so no sunburst rays and no host.

## `relic_second_class.png`: articles used by the saint

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **neatly folded, worn brown
wool religious habit**, its coarse weave and faded patches visible, with a
**simple wooden-bead rosary and a small plain wooden crucifix laid across the
top fold**, and the knotted end of a white rope cincture trailing to one side.
Humble, well used, cherished. Centred. 1:1.

*Notes:* this should read as the saint's own belongings, poor and much used,
not new church goods. The rosary is an accent on the habit and not the main
subject, so it does not duplicate `symbol_rosary`.

## `relic_third_class.png`: objects touched to a relic

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **small square of plain white
linen with a simple red cross embroidered at its centre**, lying slightly
angled on a **cream-coloured devotional card with a tiny disc of red sealing
wax** at one corner. Beside them lies a **small oval bronze devotional medal**
on a short loop, its face worn smooth and softly lit. Modest, simple and gentle.
Centred. 1:1.

*Notes:* the linen square is the hero, and the card and medal are secondary. The
card must be **blank, with no printed image and no words**. Keep the medal
generic, **not the Miraculous Medal** (no Marian figure, no stars, no letters).

---

## After generating

Put the three PNGs in `shared/src/commonMain/composeResources/drawable/` and
rebuild. No content change is needed: `content/hubs/catechism.json` already
points at `hub/relics/relic_*.png`, and `hubAssetPainter` resolves drawables by
file name.
