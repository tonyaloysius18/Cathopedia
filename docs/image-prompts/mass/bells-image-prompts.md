# The Bells of the Church — image generation brief

Five images for the new "The Bells of the Church" section (Holy Mass hub,
section 14), one per bell type covered in the article:

1. **mass_bell_sanctus** — the Sanctus Bell (small handbell).
2. **mass_bell_chime** — the Four-Bell Altar Chime.
3. **mass_bell_tower** — the Church Tower Bell.
4. **mass_bell_carillon** — Carillon Bells.
5. **mass_bell_altar** — the Altar Bell.

Same house style as the rest of the Mass hub's sacred-object images
(`mass_monstrance`, `mass_thurible`): classical academic sacred-object
rendering, warm golden light, rich earthy palette, reverent and still.
**No text, no labels, no arrows, no watermark, no signature.**

## Rules for every image

- **Transparent background is required** — export a PNG with a real alpha
  channel. No church interior, no floor, no pews, no frame: just the object
  (with only the minimal mount it needs to read correctly, e.g. a short
  stub of wooden yoke for the tower bell), isolated on transparency, so it
  drops onto the app's own surface.
- **One subject per image.** Never a row, grid, or composite infographic —
  each bell type gets its own image.
- Square **1:1, 1024×1024**.
- Materials: aged bronze and polished brass with a soft sheen, warm wood for
  handles/frames where present — matching the gilded, jewel-like finish of
  the existing monstrance and thurible images, but in bronze/brass rather
  than gold.

---

## 1. mass_bell_sanctus — the Sanctus Bell

A single small brass handbell with a turned wooden handle, held upright as
if resting on a surface, the clapper hanging visibly inside. Simple,
dignified, no engraving needed beyond a plain moulded rim.

```
Create an original illustration: a single small brass altar handbell,
centered and upright, on a plain transparent background. A polished brass
bell with a turned dark wood handle on top and a visible brass clapper
hanging inside. Warm golden highlights, soft reverent lighting, fine detail,
classical sacred-object rendering style. No exploded parts, no labels, no
text, no watermark, no signature, no background at all — export with a
transparent alpha channel. Square 1:1.
```

---

## 2. mass_bell_chime — the Four-Bell Altar Chime

Four small brass bells mounted on an X-shaped frame around a central
vertical wooden handle, each bell's clapper visible.

```
Create an original illustration: a four-bell altar chime, centered and
upright, on a plain transparent background. A central turned wood handle
with four small polished brass bells radiating outward on an X-shaped metal
frame, each with a visible clapper. Warm golden highlights, soft reverent
lighting, fine detail, classical sacred-object rendering style. No exploded
parts, no labels, no text, no watermark, no signature, no background at
all — export with a transparent alpha channel. Square 1:1.
```

---

## 3. mass_bell_tower — the Church Tower Bell

A large bronze bell hanging from a short wooden yoke/headstock (just enough
of the mount to read as a tower bell, not a full tower), engraved with a
plain cross in relief, clapper visible.

```
Create an original illustration: a single large bronze church tower bell,
centered and upright, hanging from a short wooden headstock/yoke, on a
plain transparent background. Deep aged bronze-green patina bell with a
plain cross in relief on its face, a thick visible clapper inside, hung
from a stub of dark wood beam (not a full tower or building — just enough
mount to read as a tower bell). Warm golden highlights on the bronze, soft
reverent lighting, fine detail, classical sacred-object rendering style. No
exploded parts, no labels, no text, no watermark, no signature, no
background at all — export with a transparent alpha channel. Square 1:1.
```

---

## 4. mass_bell_carillon — Carillon Bells

A compact wooden carillon console (a small baton keyboard) with a rack of
graduated bronze bells above it.

```
Create an original illustration: a compact carillon console, centered and
upright, on a plain transparent background. A wooden frame holding a small
keyboard of wooden batons at the base, with a rack of five to seven
graduated bronze bells of different sizes mounted above and behind it,
connected by thin wires. Warm golden highlights on the bronze, soft
reverent lighting, fine detail, classical sacred-object rendering style. No
exploded parts, no labels, no text, no watermark, no signature, no
background at all — export with a transparent alpha channel. Square 1:1.
```

---

## 5. mass_bell_altar — the Altar Bell

A small brass bell (or a small set of two or three) mounted on a short
wall bracket, as found beside an altar.

```
Create an original illustration: a small altar bell on its wall bracket,
centered and upright, on a plain transparent background. A small polished
brass bell (or a cluster of two or three tiny bells) hanging from a short
ornate brass wall bracket with a small cross finial above it. Warm golden
highlights, soft reverent lighting, fine detail, classical sacred-object
rendering style. No exploded parts, no labels, no text, no watermark, no
signature, no background at all — export with a transparent alpha channel.
Square 1:1.
```

---

## Deliverable

5 **transparent PNG** files, square 1:1 (1024×1024), named exactly:
`mass_bell_sanctus.png`, `mass_bell_chime.png`, `mass_bell_tower.png`,
`mass_bell_carillon.png`, `mass_bell_altar.png`. Hand them back as a single
batch, then drop them into `docs/image-prompts/mass/` (or send them in
chat) and tell me — I'll convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` and the content already
references them (`content/hubs/mass.json`, section `mass.bells`,
`CONTENT_VERSION` already bumped).
