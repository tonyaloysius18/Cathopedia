# The Four Marks of the Church — image generation brief

Four images for the **Four Marks** screen (Catechism → The Creed → The Four Marks
of the Church). Each one is the illustration for a single mark, shown in its own
card down the left of the screen beside that mark's description card.

House style: classical academic sacred oil painting with fine detail, warm golden
light, rich earthy palette, reverent and still — the same style as the Holy Mass
and Religious Orders images. **No text, no labels, no numbers, no arrows, no
watermark, no signature.**

## Rules for every image

- **Square, 1:1, 1024×1024.**
- **Transparent background is required** — a PNG with a real alpha channel. No
  sky, no floor, no frame: the subject is isolated on transparency so it drops
  onto the app's dark green surface.
- **Keep the subject centred with a little margin**; the tile is drawn whole
  (fit, not cropped) inside a rounded card about 104dp square.
- **Read as one set.** The four are seen together down one column, so match
  scale, light direction (warm light from upper left) and palette across all
  four: cross and roof → chalice and book → globe → foundation stone.
- **No composite infographic.** One subject per tile, no side panels, no cards.

**Negative prompt (all four):** `text, letters, numbers, labels, captions, arrows,
diagram, infographic, watermark, signature, frame, border, background scenery,
sky, floor, cartoon, flat vector, low detail, extra limbs, distorted hands`

---

## 1 · ONE — `cat_four_marks_one.png`

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — the **crowning of a church**: a golden cross standing at
the apex, a radiant dove of the Holy Spirit descending just beneath it in a halo
of soft golden rays, and below them the **steep tiled roof and gable of a church**
with a rose window at its centre. The cross sits at the top of the frame, the roof widening below it. One body under one Lord.
Reverent and still. 1:1.

## 2 · HOLY — `cat_four_marks_holy.png`

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — the **interior of the sanctuary**: a golden chalice with
a white host raised above it, beside an open Gospel book on a stand, both resting
on a stone altar, lit by a warm glow falling from above. Fine engraving on the gold, a soft sheen. Christ sanctifying his
Church. Centred, reverent. 1:1.

## 3 · CATHOLIC — `cat_four_marks_catholic.png`

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — a **globe of the earth encircled by a ring of people of
every nation**, standing shoulder to shoulder around it, hands joined, in varied
traditional dress. The globe is the centre of the tile, the figures ringing it
small and evenly spaced. Universal in every place and every age. Warm, reverent,
no crowd chaos. 1:1.

## 4 · APOSTOLIC — `cat_four_marks_apostolic.png`

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — the **Twelve Apostles standing shoulder to shoulder in a
single row on a broad stone foundation block**, in first-century robes, each with
a halo, St Peter at the centre holding the keys. Full-length figures, the stone
foundation running along the bottom of the tile. The Church
built on the Apostles. Dignified and still. 1:1.

---

## Deliverable

Save the four PNGs straight into
`shared/src/commonMain/composeResources/drawable/` as
`cat_four_marks_one.png`, `cat_four_marks_holy.png`,
`cat_four_marks_catholic.png` and `cat_four_marks_apostolic.png`, then rebuild.
`FourMarksScreen.kt` names them directly, so nothing else is needed.

The originals as first generated are archived in `content/four-marks/`; nothing
in the build reads that folder.
