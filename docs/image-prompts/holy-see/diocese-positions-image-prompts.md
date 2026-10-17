# Positions Given to Priests in a Diocese — image generation brief

Images for **"Positions Given to Priests in a Diocese"** (`art.offices.positions`,
Holy See hub → Church Offices, 2nd article). The article uses the same swipeable
card carousel as *Offices in the College of Cardinals* and *Candle Holders*: each
position is a card with its name, a short text and a portrait.

Until an image lands its card shows text only. Export each one as
`<asset>.webp` (quality 82) into
`shared/src/commonMain/composeResources/drawable/`, where the app picks it up by
name. Keep the PNG master in `content/hub/holy_see/`.

| # | Asset | Position | Status |
| --- | --- | --- | --- |
| 1 | `diocese_position_vg.png` | Vicar General | generated |
| 2 | `diocese_position_ev.png` | Episcopal Vicar | generated |
| 3 | `diocese_position_chancellor.png` | Chancellor | generated |
| 4 | `diocese_position_consultors.png` | College of Consultors | generated |
| 5 | `diocese_position_dean.png` | Vicar Forane (Dean) | generated |
| 6 | `diocese_position_judicial.png` | Judicial Vicar | generated |
| 7 | `diocese_position_finance.png` | Finance Officer (Economus) | generated |
| 8 | `diocese_position_presbyteral.png` | Presbyteral Council Member | generated |
| 9 | `diocese_position_moderator.png` | Moderator of the Curia | generated |
| 10 | `diocese_position_seminary.png` | Seminary Rector | generated |
| 11 | `diocese_position_pastor.png` | Parish Priest (Pastor) | generated |
| 12 | `diocese_position_parochial.png` | Parochial Vicar (Curate) | generated |
| 13 | `diocese_position_cathedral.png` | Rector of the Cathedral | generated |
| 14 | `diocese_position_catechesis.png` | Director of Catechesis | generated |
| 15 | `diocese_position_liturgy.png` | Director of Liturgy | generated |
| 16 | `diocese_position_chaplain.png` | Chaplain | generated |

## House style (all sixteen)

Match the existing **`hierarchy_priest.png`** and the **`cardinal_office_*.png`**
set: a realistic painted bust, like a classical oil portrait, with soft warm
light from the upper left and fine detail in the fabric. Do **not** copy the
watercolour/cartoon look of the source infographic.

- **Transparent background is required** (real alpha channel). The only
  scenery allowed is the one small prop named for each image, held or
  beside the figure. There is no room and no landscape.
- **Square 1:1, 1024×1024.** Show the figure waist-up in three-quarter
  view, with the head in the upper third and the figure filling about 90%
  of the frame height.
- **Dress:** a diocesan priest in a black cassock with a white Roman collar.
  A few images use different dress, as listed in the table below.
- **Fictional faces only.** No face may resemble a real priest or bishop.
  Vary age (from about 30 to 70), build, hair and skin tone so the sixteen
  read as sixteen different men from around the world.
- **No text, letters or numbers anywhere** (not on books, files or screens),
  no coats of arms with writing, no watermark and no signature.

**Negative prompt (all):** `text, letters, numbers, watermark, signature, frame,
background scenery, room, real person, celebrity likeness, mitre, zucchetto,
scarlet, purple, pope, cartoon, watercolour, flat vector, anime, low detail`

## One clear prop or pose per position

Every priest wears much the same dress, so each image needs **one prop or pose
you can read at thumbnail size**:

| # | Position | Prop, pose and dress |
| --- | --- | --- |
| 1 | Vicar General | Mature, grey at the temples. Holds a closed leather folio with a small gold cross on the cover; calm, authoritative look. Cassock with simple black buttons. |
| 2 | Episcopal Vicar | Holds an unrolled, wordless parchment map of a region with tiny church shapes; looking down at it thoughtfully. |
| 3 | Chancellor | Glasses; holds a heavy bound register under one arm and a fountain pen in the other hand; an old wax seal hangs from a ribbon on the register. |
| 4 | College of Consultors | **Group image:** five priests of different ages stand close together in conversation, shown waist-up; the centre one is speaking with an open hand. Same transparent background. |
| 5 | Vicar Forane (Dean) | Older and warm, with a hand on the shoulder of a younger priest who is cropped at the frame edge, a gesture of an elder brother. |
| 6 | Judicial Vicar | Holds a slim red book of canon law and a sheet of wordless paper; a small brass balance scale stands beside him. Serious, attentive. |
| 7 | Finance Officer (Economus) | Holds an open ledger with blank ruled columns (**no numbers**) and a pencil. Optional: a lay man in a dark suit and tie instead of a priest, because this office is often lay. |
| 8 | Presbyteral Council Member | Seated at the edge of a council table (only the near corner visible), leaning forward to speak, one hand raised in explanation. |
| 9 | Moderator of the Curia | Stands with a clipboard of blank papers, glancing to the side as if coordinating others; an alert, organised look. |
| 10 | Seminary Rector | Holds a stack of three books against his chest; a young seminarian in a cassock is cropped at the frame edge beside him. |
| 11 | Parish Priest (Pastor) | In **green chasuble** over an alb, arms open in the gesture of greeting at Mass; warm and welcoming. |
| 12 | Parochial Vicar (Curate) | Youngest of the set, about 30. In **white surplice and violet stole**, holding a small purple ritual book for confession or the sick. |
| 13 | Rector of the Cathedral | In **black cassock with white surplice (choir dress)**, holding a ring of large old iron church keys; a small stone Gothic pinnacle beside him. |
| 14 | Director of Catechesis | Holds an open children's catechism with **picture pages only** (simple illustrations, no text), smiling as if teaching. |
| 15 | Director of Liturgy | Holds a large red altar missal closed against his chest; a lit brass altar candlestick stands beside him. |
| 16 | Chaplain | Hospital chaplain in black clerical shirt and collar with a purple stole over his shoulders, holding a small round pyx; kind, compassionate look. |

## Prompt template

Fill in the **[prop and pose]** from the table:

```
A realistic classical oil-painting style portrait of a Catholic diocesan priest,
waist-up, three-quarter view, wearing a black cassock with a white Roman collar,
[prop and pose]. Soft warm light from the upper left, fine detail in the fabric
folds, dignified and serene expression, a fictional face that does not resemble
any real person. Transparent background (alpha channel), no scenery, no text,
no letters, no numbers, no watermark, no signature. Square 1:1, 1024x1024,
figure filling about 90% of the frame height.
```

**Example (11, Parish Priest):**

```
A realistic classical oil-painting style portrait of a Catholic parish priest,
waist-up, three-quarter view, wearing a green chasuble over a white alb, arms
open in the gesture of greeting at Mass, warm and welcoming smile, about 50
years old. Soft warm light from the upper left, fine detail in the fabric
folds, a fictional face that does not resemble any real person. Transparent
background (alpha channel), no scenery, no text, no letters, no numbers, no
watermark, no signature. Square 1:1, 1024x1024, figure filling about 90% of
the frame height.
```

## Checks before handing back

- The background really is transparent (no baked-in checkerboard; see the
  Priesthood hub fix).
- There is no writing anywhere, including on books, ledgers and maps.
- The sixteen faces are clearly different people.
- Square 1024×1024, with names matching the asset column exactly.
