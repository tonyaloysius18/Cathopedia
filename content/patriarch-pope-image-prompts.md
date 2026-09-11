# Patriarch and Pope — image generation brief

Only **two** new images are needed for the Patriarchs & the Pope section
(Explore → The Holy See → Patriarchs & the Pope). The Pope's own portrait and
symbol are already in the app and are reused:

| Cell | Asset | Status |
| --- | --- | --- |
| Patriarch portrait | `patriarch_portrait.png` | **needed** |
| Pope portrait | `hierarchy_pope.png` | already in the app |
| Patriarch symbol | `patriarch_cross.png` | **needed** |
| Pope symbol | `papal_keys.png` | already in the app |

Because these sit **side by side** with images that already exist, the new pair
must match their existing partners closely — same framing, same lighting, same
finish. That matters more here than in any previous brief.

## Rules for both

- **Transparent background** — a PNG with a real alpha channel, no scenery, no
  frame, no floor.
- **No text, no labels, no watermark, no signature.**
- Square **1:1**. Export at **512×512** to match `hierarchy_pope.png`.
- Classical academic sacred oil painting, warm golden light from the upper left,
  fine detail, reverent and still.

**Negative prompt (both):** `text, letters, words, labels, watermark, signature,
frame, border, background scenery, room interior, floor, cartoon, flat vector,
low detail, distorted hands, extra fingers`

---

## `patriarch_portrait.png`

This is the direct counterpart of the existing `hierarchy_pope.png`, which shows
the Pope **half-length, facing the viewer, centred, warmly lit, in white**. Match
that framing and scale exactly — same crop at the chest, same head size in the
frame, same calm frontal pose, same soft even light — so the two portraits sit as
a matched pair.

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — an **Eastern Catholic patriarch, half-length, facing the
viewer**: an elderly bishop with a long full white beard, wearing a **black
monastic klobuk with a veil falling over the shoulders**, a **black outer riasa
over a purple-and-gold inner vestment**, and a **gold pectoral cross and panagia
(an oval icon medallion) on chains at his chest**. Dignified, serene, a gentle
expression. Half-length, centred. 1:1.

*(If your generator drifts toward a Latin-rite bishop: the giveaways are the
black veiled klobuk instead of a mitre, the long beard, and the panagia
medallion beside the pectoral cross.)*

## `patriarch_cross.png`

The counterpart of the existing `papal_keys.png` — a **single gilded emblem,
centred, upright, on transparency**, with the same jewel-like warm gold, fine
engraving and soft sheen, and the same generous margin inside the frame.

Gilded sacred emblem, warm gold with fine engraving, transparent background,
centred, no scene — a **patriarchal cross: an upright cross with two horizontal
bars**, the upper bar shorter than the lower one, ornamented finials at the ends
of each arm. Jewel-like and reverent, no staff, no hand, no ribbon. 1:1.

---

## After generating

Drop both PNGs into `shared/src/commonMain/composeResources/drawable/` under
exactly those names and rebuild. The asset paths are already written into
`content/hubs/holy_see.json`, and the app omits any image it cannot find, so the
page works either way.
