# Inside a Catholic Church — image generation brief

Two original illustrations for the Holy Mass hub, section 13 "Inside a Catholic
Church":

1. **mass_church_map** — a cutaway map of a church, numbered 1–15 (article "The Church Like a Map").
2. **mass_sanctuary_furnishings** — the sanctuary furnishings, numbered 1–8 (article "The Sanctuary and Its Furnishings").

These **replace** the two placeholder images currently in the app, which were
taken from someone else's infographics and must not ship. The prompts below
deliberately use a different composition, architecture and palette so the new
art is clearly original.

## Rules for both images

- **No words at all** — no titles, captions, labels, quotes, logos, watermarks or
  signature. The app supplies every name and description as text (image
  generators garble lettering). The only allowed marks are the **numbered
  badges** described below.
- **Numbered badges:** small round badges, deep burgundy with a thin gold rim and
  a cream numeral, all the same size, each touching its object with a short thin
  leader line. Numerals must be clean and correct.
- Same house style as the rest of Cathopedia: classical, warm, reverent, gilded
  detail. Soft even light, no harsh shadows.
- Plain **warm cream / ivory background**, edge to edge, no border, no frame.
- Save as PNG. Portrait for the map, landscape for the furnishings (sizes below).

---

## 1. mass_church_map — portrait 1024 × 1536

Numbers **must match this list** (they match the article text):

| # | Item | # | Item |
|---|------|---|------|
| 1 | Narthex (entrance) | 9 | Altar |
| 2 | Nave | 10 | Tabernacle |
| 3 | Pews | 11 | Sanctuary lamp |
| 4 | Baptismal font | 12 | Crucifix |
| 5 | Confessional | 13 | Presider's chair |
| 6 | Stations of the Cross | 14 | Credence table |
| 7 | Ambo | 15 | Sacristy |
| 8 | Sanctuary | | |

### Prompt (paste into ChatGPT)

```
Create an original illustration: a cutaway bird's-eye view of a traditional
Romanesque-style Catholic church interior, drawn as an elegant hand-painted
architectural map on warm ivory paper. Portrait format, 2:3.

Viewpoint: high, looking straight down the long axis of the church with the roof
removed, the entrance doors at the BOTTOM of the picture and the sanctuary at the
TOP. Rounded Romanesque arches, warm honey-coloured stone, a patterned marble
floor, a soft golden glow.

Show these elements in this order from bottom to top, clearly separated and easy
to recognise:
- a small entrance porch with a pair of wooden doors at the very bottom
- inside the entrance, a stone baptismal font with a domed cover, on the left
- a long central aisle with rows of dark wooden pews on both sides
- along the left wall, a wooden confessional with a purple curtain
- along both side walls, a series of small framed Stations of the Cross plaques
- toward the top, a few marble steps up to the sanctuary
- on the left of the sanctuary, a lectern (ambo) with a green cloth
- in the centre, a white-clothed stone altar with candles and a small cross
- on the right of the altar, a presider's chair with a red cushion
- beside the chair, a small credence table with a chalice and cruets
- behind the altar, a golden tabernacle on a plinth, with a red hanging sanctuary
  lamp beside it
- on the wall above and behind them, a large wooden crucifix
- in the top-right corner, a small sacristy room with vestments on a rack

Add 15 small numbered badges, deep burgundy with a thin gold rim and a cream
numeral, each touching its object with a short thin leader line:
1 entrance porch, 2 central aisle, 3 pews, 4 baptismal font, 5 confessional,
6 Stations of the Cross, 7 ambo, 8 sanctuary floor, 9 altar, 10 tabernacle,
11 sanctuary lamp, 12 crucifix, 13 presider's chair, 14 credence table,
15 sacristy. Numerals must be legible and exactly correct.

Strictly NO other text: no title, no captions, no labels, no legend, no quotes,
no logo, no watermark. Plain cream background around the church, no border.
Reverent, calm, classical illustration style, soft even lighting.
```

If a number lands wrong, follow up with:
`Keep everything, but fix the badges so that 1 = entrance porch, 2 = aisle, ... exactly as listed. Do not add any other text.`

---

## 2. mass_sanctuary_furnishings — landscape 1536 × 1024

Numbers **must match this list** (I'll renumber the article to these):

| # | Item |
|---|------|
| 1 | Altar table |
| 2 | Ambo |
| 3 | Tabernacle |
| 4 | Sanctuary lamp |
| 5 | Retablo (reredos) |
| 6 | Credence table |
| 7 | Relic (in the altar) |
| 8 | Altar rail (communion rail) |

### Prompt (paste into ChatGPT)

```
Create an original illustration: a catalogue-style "plate" showing the
furnishings of a traditional Catholic sanctuary, painted in a refined classical
style on a warm cream background. Landscape format, 3:2.

Composition: a Baroque-style sanctuary as the centrepiece (NOT Gothic) —
a white-and-gold reredos (retablo) with twisted columns and a painted panel of the
Virgin and Child, a plain stone altar in front with a white altar cloth, two lit
candles, and a small gilded tabernacle with a tiny door set in the reredos above
the altar. In the marble front of the altar, a small glazed window showing a
reliquary with a relic.

Around the centrepiece, as separate objects on the cream background with plenty
of space between them:
- on the left, a wooden ambo (lectern) with a green liturgical cloth, a book
  resting on it
- top right, a hanging brass sanctuary lamp with a red glass, on chains
- on the right, a small wooden credence table with a chalice, two cruets, a
  purificator and a book
- along the bottom, a low carved-wood altar rail with a red kneeling cushion at
  its base, in a simple arched design

Add 8 small numbered badges, deep burgundy with a thin gold rim and a cream
numeral, each touching its object with a short thin leader line:
1 altar, 2 ambo, 3 tabernacle, 4 sanctuary lamp, 5 reredos (the decorated
structure behind the altar), 6 credence table, 7 relic window in the altar,
8 altar rail. Numerals must be legible and exactly correct.

Strictly NO other text: no title, no captions, no labels, no logo, no watermark,
no signature. Plain cream background, no border. Warm gold and marble palette,
soft even lighting, reverent and calm.
```

---

## After generating

Drop the two PNGs into `docs/image-prompts/mass/` (or send them in chat) and tell
me. I'll then:

1. convert them to WebP and replace `mass_church_map.webp` and
   `mass_sanctuary_furnishings.webp`,
2. renumber the furnishings list to the 1–8 order above (the map list already
   uses 1–15), and
3. bump `CONTENT_VERSION` and rebuild.

If ChatGPT insists on adding text, ask it to redo the image "with no lettering
anywhere except the numeral badges"; if a numeral is wrong I can also paint the
badges out and you can regenerate just that image.
