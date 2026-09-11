# The Liturgical Year — image brief

**One image needed.** The other five seasons reuse symbols already in the app:

| Season | Symbol | Status |
| --- | --- | --- |
| 1 · Advent | `symbol_advent_wreath.png` | **needed** |
| 2 · Christmas | `symbol_christmas_nativity.png` | already in the app |
| 3 · Ordinary Time | `symbol_tree_of_life.png` | already in the app |
| 4 · Lent | `symbol_crown_of_thorns.png` | already in the app |
| 5 · Sacred Paschal Triduum | `symbol_crucifix.png` | already in the app |
| 6 · Easter | `symbol_paschal_candle.png` | already in the app |

Until it is generated, Advent falls back to a plain numeral — nothing is broken.

## `symbol_advent_wreath.png`

Matches the rest of the `symbol_*` set: **a single emblem, centred, upright, on
transparency**, fine detail, a soft sheen, jewel-like and reverent, with a
generous margin inside the frame.

Unlike the other symbols this one is **not** all-gold — the candle colours are
the point, since the card beside it names Advent's purple and rose.

- Square **1:1, 512×512**.
- **Transparent background**, a PNG with a real alpha channel. No table, no
  cloth, no floor, no frame.
- **No text, no numbers, no labels, no watermark, no signature.**

Classical sacred illustration, warm golden light, transparent background, no
scenery — an **Advent wreath seen from a slight three-quarter angle above**: a
circular wreath of deep green fir with small pine cones and gold accents, holding
**four upright candles — three purple and one rose**, all lit, the rose candle
clearly distinct in colour from the other three. The wreath fills the frame, the
candles rising above it. Rich and reverent, not cartoonish.

**Negative prompt:** `text, letters, numbers, labels, watermark, signature,
frame, border, table, tablecloth, background scenery, room interior, cartoon,
flat vector, low detail, five candles, three candles`

*Note:* the count matters — exactly **four** candles, **three purple and one
rose**. Generators frequently produce five, or make all four the same colour.

## After generating

Drop it into `shared/src/commonMain/composeResources/drawable/` under that exact
name and rebuild. The asset path is already in `content/hubs/mass.json`; no
content edit and no `CONTENT_VERSION` bump is needed.
