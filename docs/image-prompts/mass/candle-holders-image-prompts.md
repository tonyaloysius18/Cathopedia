# Types of Candle Holders — image generation brief

Images for "Types of Candle Holders" (`art.mass.candles`, Holy Mass hub,
section 22 "Candle Holders"). It uses the same card layout as Church Towers
and Sacred Symbols: each holder is a card with its name, a short text and
the image shown **small, to one side**, so each one needs a clear, strong
silhouette that reads at thumbnail size.

| # | Asset | Holder | Status |
| --- | --- | --- | --- |
| 1 | `altar_candles.png` | Altar candlesticks | **reused**, already in the app (Altar) |
| 2 | `candle_processional.png` | Processional candlesticks | pending |
| 3 | `candle_sanctuary_lamp.png` | Sanctuary lamp holder | pending |
| 4 | `candle_paschal_stand.png` | Paschal candle stand | pending |
| 5 | `candle_candelabrum.png` | Candelabrum | pending |
| 6 | `candle_votive.png` | Votive candle holders | pending |
| 7 | `candle_devotional.png` | Devotional candle stand | pending |

Until an image lands, its card shows without a picture. No code change is
needed when it arrives: export it as `<asset>.webp` (quality 82) into
`shared/src/commonMain/composeResources/drawable/` and it is picked up by
name.

## House style (all six)

Match the existing **`altar_candles.png`**: open it beside each new image.
It shows a pair of polished, embossed gilt-brass candlesticks with tall
ivory candles and small warm flames, drawn as a realistic devotional
illustration on transparency.

- **Transparent background is required**: a PNG with a real alpha channel.
  No church interior, no altar, no wall, no floor, no shadow box.
- **Portrait 2:3, 1024×1536** (the reused altar image is tall too). The
  object is centred, upright, seen from the front at eye level, and fills
  about 85% of the frame height.
- Warm golden light from the upper left; polished metal with fine embossed
  ornament (acanthus leaves, small crosses); candles in warm ivory with a
  small, calm, realistic flame.
- **No people** — not even hands. The processional candlesticks stand on
  their own, as they do in the sacristy.
- **No text, no labels, no numbers, no watermark, no signature.**
- After generating, check the alpha channel. Earlier cut-outs arrived with
  the transparency checkerboard baked into the edges.

**Negative prompt (all):** `text, letters, numbers, labels, watermark,
signature, people, hands, altar servers, priest, church interior, wall,
floor, background, table cloth, cartoon, flat vector, low detail,
electric bulb, LED, neon`

**Keep them distinct at thumbnail size.** Each holder needs its own outline:

- **Processional:** two very tall, slender poles, each topped by a candle.
- **Sanctuary lamp:** a hanging lamp on three chains, red glass.
- **Paschal stand:** one thick, tall decorated candle on a heavy stand.
- **Candelabrum:** one stem branching into several arms.
- **Votive:** a tiered rack of many small glass cups.
- **Devotional:** a low stand with many thin tapers in sand.

---

### 2. `candle_processional.png`: processional candlesticks

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a pair of tall processional candlesticks
standing upright side by side, each a long slender gilded pole with
decorative knops along its length and a small drip pan at the top holding
a tall ivory candle with a small warm flame. The poles are about four times
taller than the candles, so the pair reads as tall and narrow. No one is
holding them. Centred, front view, eye level. Plain transparent background.
Warm golden light from the upper left, polished metal, fine embossed
detail. No people, no hands, no text, no labels, no watermark, no
signature. Transparent alpha channel. Portrait 2:3.
```

### 3. `candle_sanctuary_lamp.png`: sanctuary lamp holder

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a hanging sanctuary lamp, a gilded,
ornately embossed bowl suspended from three fine gold chains that meet at
a small crown-shaped canopy at the top, holding a deep red glass cylinder
with a small, steady candle flame glowing inside, and a small ornamental
cross hanging beneath the bowl. Centred, front view, eye level, the chains
running to the top edge of the frame. Plain transparent background. Warm
golden light, the red glass glowing softly. No tabernacle, no wall, no
text, no labels, no watermark, no signature. Transparent alpha channel.
Portrait 2:3.
```

### 4. `candle_paschal_stand.png`: Paschal candle stand

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a tall, sturdy gilded Paschal candle stand
with a broad heavy base, a column with embossed ornament and a wide drip
pan, holding one very large, thick ivory Paschal candle with a small warm
flame. On the candle, in red and gold relief: a cross, the Greek letters
Alpha above and Omega below the cross, and five small red incense grains
set in the shape of a cross. No year numbers. Centred, front view, eye
level. Plain transparent background. Warm golden light from the upper
left. No flowers, no font, no text other than Alpha and Omega, no labels,
no watermark, no signature. Transparent alpha channel. Portrait 2:3.
```

### 5. `candle_candelabrum.png`: candelabrum

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a gilded candelabrum with one central stem
on a round embossed foot, branching into five graceful curved arms with
scrolled ornament, each arm ending in a small drip pan holding a tall
ivory candle with a small warm flame; the central candle slightly taller.
Symmetrical, centred, front view, eye level. Plain transparent background.
Warm golden light from the upper left, polished metal, fine detail. No
table, no altar, no text, no labels, no watermark, no signature.
Transparent alpha channel. Portrait 2:3.
```

### 6. `candle_votive.png`: votive candle holders

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a tiered votive candle rack in dark
wrought iron with three stepped rows, filled with small votive candles in
ruby-red glass cups, most of them lit with small warm flames and a few
unlit, a small brass offering slot box at one end, and a pierced cross
pattern on the front panel. Three-quarter front view, eye level, centred.
Plain transparent background. Warm candlelight glow on the iron. No
statue, no wall, no people, no text, no labels, no numbers, no watermark,
no signature. Transparent alpha channel. Portrait 2:3, the rack filling
the lower two-thirds of the frame.
```

### 7. `candle_devotional.png`: devotional candle stand

```
Create an original illustration, matching a companion image of embossed
gilt-brass altar candlesticks: a devotional candle stand, a low wrought
iron and brass table-stand with a long tray filled with sand, holding many
thin ivory taper candles of slightly different heights standing upright
in the sand, most of them lit with small warm flames, and a pierced cross
pattern along the front edge. Three-quarter front view, eye level,
centred. Plain transparent background. Warm candlelight glow. No statue,
no wall, no people, no text, no labels, no numbers, no watermark, no
signature. Transparent alpha channel. Portrait 2:3, the stand filling the
lower two-thirds of the frame.
```

## Content notes for the artist

- The **sanctuary lamp's red glass** is a widespread custom, not a rule (the
  page says so); red is used here because it is what most people recognise.
- The **Paschal candle** is shown without a year: the year changes every
  Easter, and the app shows the same image all year.
- **Votive and devotional** holders are both for candles the faithful light
  in prayer; the difference the images should show is small glass cups on a
  tiered rack versus thin tapers in a sand tray.
