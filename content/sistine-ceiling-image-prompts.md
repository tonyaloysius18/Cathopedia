# The Sistine ceiling — image generation brief

One image, replacing the placeholder now in the app.

| Slot | Asset |
| --- | --- |
| The ceiling, panel by panel | `sistine_ceiling.png` |

The current `sistine_ceiling.webp` is a wireframe — plain tan rectangles standing
in for the panel layout. Every hotspot therefore sits on a blank box, and zooming
in reveals a bigger blank box. This brief replaces it.

## The hard constraint

**The artwork must match the hotspot geometry below exactly.** Twenty-one
hotspots are already authored against normalized coordinates in
`content/hubs/holy_see.json`; the app hit-tests taps against those numbers, not
against the picture. If a panel in the new art sits anywhere other than its box,
tapping it opens the wrong caption.

- Canvas **1024 × 3103** (aspect ratio 0.33, portrait).
- **Altar end at the top**, entrance end at the bottom — the ceiling is rotated
  from its real landscape proportions so it reads on a phone.
- The nine Genesis panels run down the centre, alternating large and small.
  Prophets and sibyls alternate down the left and right margins.

Coordinates are fractions of the canvas: `x` and `w` across, `y` and `h` down.

| # | Panel | x | y | w | h |
| --- | --- | --- | --- | --- | --- |
| 1 | The Separation of Light from Darkness | 0.42 | 0.06 | 0.16 | 0.07 |
| 2 | The Creation of the Sun, Moon and Plants | 0.36 | 0.14 | 0.28 | 0.09 |
| 3 | The Separation of Land from Water | 0.42 | 0.24 | 0.16 | 0.07 |
| 4 | The Creation of Adam | 0.36 | 0.32 | 0.28 | 0.09 |
| 5 | The Creation of Eve | 0.42 | 0.42 | 0.16 | 0.07 |
| 6 | The Fall and the Expulsion | 0.36 | 0.50 | 0.28 | 0.09 |
| 7 | The Sacrifice of Noah | 0.42 | 0.60 | 0.16 | 0.07 |
| 8 | The Flood | 0.36 | 0.68 | 0.28 | 0.09 |
| 9 | The Drunkenness of Noah | 0.42 | 0.78 | 0.16 | 0.07 |
| 10 | The Prophet Jonah | 0.44 | 0.01 | 0.12 | 0.05 |
| 11 | The Prophet Jeremiah | 0.20 | 0.10 | 0.14 | 0.06 |
| 12 | The Libyan Sibyl | 0.66 | 0.10 | 0.14 | 0.06 |
| 13 | The Persian Sibyl | 0.20 | 0.26 | 0.14 | 0.06 |
| 14 | The Prophet Daniel | 0.66 | 0.26 | 0.14 | 0.06 |
| 15 | The Prophet Ezekiel | 0.20 | 0.42 | 0.14 | 0.06 |
| 16 | The Cumaean Sibyl | 0.66 | 0.42 | 0.14 | 0.06 |
| 17 | The Erythraean Sibyl | 0.20 | 0.58 | 0.14 | 0.06 |
| 18 | The Prophet Isaiah | 0.66 | 0.58 | 0.14 | 0.06 |
| 19 | The Prophet Joel | 0.20 | 0.74 | 0.14 | 0.06 |
| 20 | The Delphic Sibyl | 0.66 | 0.74 | 0.14 | 0.06 |
| 21 | The Prophet Zechariah | 0.44 | 0.90 | 0.12 | 0.05 |

## House style

An **illustrated interpretation** in Cathopedia's own hand, not an attempted
photograph of the fresco. Classical academic sacred painting, warm and slightly
faded, as though seen in raking afternoon light: ochres, terracotta, dusty blues
and greens, a soft cream ground for the architectural framing. Each scene painted
loosely enough to read as an evocation rather than a copy, but unmistakable —
Adam reclining with his arm outstretched in panel 4; the ark on the waters in
panel 8; each prophet and sibyl seated and turned into or away from the centre.

- **No text, no numerals, no labels, no watermark.** The app draws the panel
  outlines and captions over the top.
- Paint the **architectural framing** between panels — the painted cornices, the
  ignudi and the bronze-toned medallions — since that framing is what makes the
  layout legible when zoomed out. Keep it quieter than the scenes themselves.
- Leave the outer margin plain. The lunettes and spandrels below the sibyls are
  **not** hotspotted and should stay suggestive, not detailed.
- The whole image must still read at thumbnail size: at a glance the eye should
  find the alternating rhythm of large and small panels down the spine.

## Why an interpretation rather than a photograph

The frescoes themselves are public domain — Michelangelo died in 1564 — but
photographs of the restored ceiling are a different matter: the Vatican Museums
restrict photography inside the chapel, and image rights were asserted over the
restoration campaign's photography. An illustration in house style sidesteps the
question entirely and matches the rest of the app.

## After the art lands

1. Convert to WebP (q90, as with the medals) into
   `shared/src/commonMain/composeResources/drawable/sistine_ceiling.webp`,
   keeping the master PNG in `content/hub/holy_see/`.
2. Purge the stale copies from `shared/build` and `androidApp/build` before
   rebuilding, or the old placeholder ships alongside the new file.
3. Check three or four hotspots against the art on device — tap panel 4 and
   confirm the caption says *The Creation of Adam*.
