# Types of Chalices — image generation brief

Images for "Types of Chalices" (`art.mass.chalices`, Holy Mass hub, section
"Sacred Vessels", second article). It uses the same card layout as Church
Towers and Sacred Symbols: each chalice is a card with its name, a short text
and the image shown **small, to one side**, so each one needs a clear, strong
silhouette.

| # | Asset | Type | Status |
| --- | --- | --- | --- |
| 1 | `chalice.png` | Mass chalice | **reused**, already in the app (Sacred Vessels) |
| 2 | `chalice_communion.png` | Communion chalice | **needed** |
| 3 | `chalice_concelebration.png` | Concelebration chalices | **needed** |
| 4 | `chalice_travel.png` | Travel or portable chalice | **needed** |
| 5 | `chalice_baroque.png` | Baroque chalice | **needed** |
| 6 | `chalice_gothic.png` | Gothic chalice | **needed** |
| 7 | `chalice_romanesque.png` | Romanesque chalice | **needed** |
| 8 | `chalice_modern.png` | Modern chalice | **needed** |

## House style (all seven)

Match the existing **`chalice.png`**: open it beside each new image. It is a
gilded chalice with an embossed cross on the cup, a knop on the stem and a
round embossed foot, drawn as a polished, realistic devotional illustration
on transparency.

- **Transparent background is required**: a PNG with a real alpha channel.
  No table, no altar cloth, no shadow box, no background at all.
- Square **1:1, 1024×1024**. The vessel is centred, upright, shown from a
  slight angle so the inside rim of the cup is just visible, and fills about
  85% of the frame height.
- Warm golden light from the upper left, a polished metal finish, fine
  detail in the ornament.
- **Empty and still**: no wine visible, no host, no hands.
- **No text, no labels, no numbers, no watermark, no signature.**
- After generating, check the alpha channel. Earlier cut-outs arrived with
  the transparency checkerboard baked into the edges.

**Negative prompt (all):** `text, letters, numbers, labels, watermark,
signature, wine, liquid, host, hands, people, table, altar, cloth, candles,
background, glass, ceramic, clay, wood, cartoon, flat vector, low detail`

The material rule matters on this page, which says chalices must be made of
noble, non-absorbent material. So **every chalice is metal (gold, silver or
gilded), never glass, ceramic, pewter-grey or wood.**

**Keep them distinct at thumbnail size.** The style chalices (5 to 8) differ
in **shape**, not just ornament:

- **Romanesque:** wide, shallow, low.
- **Gothic:** tall and slender, with a six-lobed foot and architectural detail.
- **Baroque:** swelling, curvy and heavily embossed.
- **Modern:** smooth and plain, with clean geometric lines.

---

### 2. `chalice_communion.png`: Communion chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a polished gold Communion chalice, simpler and slightly
taller than a celebrant's chalice, with a deep plain cup, a gently
flaring rim for drinking from easily, a slim stem with a small plain knop
and a smooth round foot, and a white linen purificator folded neatly over
the rim. Centred, upright, slight angle. Plain transparent background.
Warm golden light from the upper left. No wine, no hands, no text, no
labels, no watermark, no signature. Transparent alpha channel. Square 1:1.
```

### 3. `chalice_concelebration.png`: concelebration chalices

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a matched set of three gold chalices of identical design
standing side by side, the central one very slightly taller (the principal
chalice), each with a plain polished cup, a knop with a small engraved
cross and a round foot. They are clearly a coordinated set made to stand
together on an altar. Plain transparent background. Warm golden light
from the upper left. No wine, no altar, no text, no labels, no watermark,
no signature. Transparent alpha channel. Square 1:1.
```

### 4. `chalice_travel.png`: travel or portable chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a small gold travel chalice, noticeably compact, with a
short stem visibly made to unscrew in the middle, a small flat paten
resting against it, and beside it an open fitted case of black leather
lined with red velvet, shaped to hold the chalice in pieces, with a small
gold cross on its lid. Plain transparent background. Warm golden light
from the upper left. No wine, no hands, no text, no labels, no watermark,
no signature. Transparent alpha channel. Square 1:1.
```

### 5. `chalice_baroque.png`: Baroque chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: an ornate Baroque gold chalice from the eighteenth century,
the lower half of the cup wrapped in a pierced embossed sleeve of cherub
heads, flowers and acanthus scrolls, a swelling pear-shaped knop richly
embossed, and a broad domed foot covered in repoussé scrollwork with a
few small red and green stones. Curvy, rich and opulent. Plain
transparent background. Warm golden light from the upper left. No wine,
no text, no labels, no watermark, no signature. Transparent alpha
channel. Square 1:1.
```

### 6. `chalice_gothic.png`: Gothic chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a tall, slender Gothic-style gold chalice with a conical
cup, a hexagonal stem with tiny architectural pinnacles and pointed-arch
tracery, a prominent knop set with six enamelled lozenges in blue and red,
and a six-lobed star-shaped foot engraved with Gothic tracery. Elegant,
vertical and architectural. Plain transparent background. Warm golden
light from the upper left. No wine, no text, no labels, no watermark, no
signature. Transparent alpha channel. Square 1:1.
```

### 7. `chalice_romanesque.png`: Romanesque chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a Romanesque-style chalice of gilded silver with a wide,
shallow hemispherical bowl decorated with a band of engraved round
medallions with crosses, a short thick stem with a large round knop, and
a broad round spreading foot. Low, heavy, solid and ancient-looking, with
a softly worn gilding. Plain transparent background. Warm golden light
from the upper left. No wine, no text, no labels, no watermark, no
signature. Transparent alpha channel. Square 1:1.
```

### 8. `chalice_modern.png`: Modern chalice

```
Create an original illustration, matching a companion image of a gilded
Mass chalice: a contemporary chalice of polished silver with a gilded
interior just visible at the rim, a simple straight-sided cup, a smooth
slender stem without ornament flowing into a wide plain circular foot,
and only one small engraved cross on the foot. Clean geometric lines,
quiet and dignified. Plain transparent background. Warm golden light
from the upper left. No wine, no text, no labels, no watermark, no
signature. Transparent alpha channel. Square 1:1.
```

---

## After generating

Drop the PNGs into `docs/image-prompts/mass/` and say so. They will be
converted to WebP in `shared/src/commonMain/composeResources/drawable/`. The
content already references them (`content/hubs/mass.json`, article
`art.mass.chalices`). Until they land, each card shows its text without a
picture.
