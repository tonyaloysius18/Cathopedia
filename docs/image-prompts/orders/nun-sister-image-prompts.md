# Nun or Sister? — image generation brief

Two portraits for the new "Nun or Sister?" comparison article (Religious
Orders hub, section "Nun or Sister?"). Same side-by-side comparison layout
as Patriarch and Pope and the Priesthood's "Two Kinds of Priests" — the two
portraits sit face-off with a "VS" badge between them, so they must read as
a matched pair.

| Cell | Asset | Status |
| --- | --- | --- |
| Nun portrait | `nun_portrait.png` | **needed** |
| Sister portrait | `sister_portrait.png` | **needed** |

## Rules for both

- **Transparent background** — a PNG with a real alpha channel, no scenery,
  no chapel, no classroom, no frame, no floor. Just the figure.
- **No text, no labels, no watermark, no signature.**
- Square **1:1, 1024×1024**.
- Classical academic sacred oil painting, warm golden light from the upper
  left, fine detail, reverent and still — same house style as the rest of
  the app's portrait pairs.
- **Half-length, facing the viewer, centred**, same crop at the chest, same
  head size in the frame, calm frontal pose, soft even light — so the two
  match each other exactly as a pair (this matters more than usual, since
  they render side by side with a "VS" badge between them).

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, frame, border, background scenery, room interior, classroom,
chalkboard, floor, cartoon, flat vector, low detail, distorted hands, extra
fingers`

---

## `nun_portrait.png` — the contemplative, enclosed nun

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — a **nun of a contemplative, enclosed order,
half-length, facing the viewer**: a woman religious wearing a **plain black
habit** with a **white wimple and black veil** framing the face, hands
joined in quiet prayer at her chest, perhaps a small wooden rosary at her
waist. Serene, still, eyes calm or gently lowered in contemplation. No
scenery at all — she reads as if withdrawn from the world. Half-length,
centred, soft even light. 1:1.

## `sister_portrait.png` — the active, apostolic sister

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — a **sister of an active, apostolic congregation,
half-length, facing the viewer**: a woman religious wearing a simpler
**dark blue or grey habit** with a short veil that leaves more of the face
and hairline visible than the nun's, a small pectoral cross on a chain,
perhaps holding a book against her chest. Warm, approachable expression, a
gentle smile — she reads as someone about to go out and serve, not
withdrawn. Half-length, centred, soft even light, same scale and crop as
`nun_portrait.png`. 1:1.

---

## After generating

Drop both PNGs into `docs/image-prompts/orders/` (or send them in chat) and
tell me — I'll convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` under exactly those
names. The asset paths are already written into `content/hubs/orders.json`
(article `art.orders.nun_vs_sister`), and the app omits any image it cannot
find, so the page works either way in the meantime.
