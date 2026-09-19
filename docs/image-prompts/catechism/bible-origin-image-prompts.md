# Who Created the Bible? — image generation brief

Twelve images for the **Sacred Scripture** section of the Catechism hub
(Explore → Catechism → Sacred Scripture → "Who Created the Bible?").

House style: classical academic sacred oil painting with fine detail, warm
golden light, rich earthy palette, reverent and still — the same style as the
Four Marks, Holy Mass and Religious Orders images. **No text, no labels, no
letters on any page or scroll, no numbers, no arrows, no watermark, no
signature.**

## Rules for every image

- **Square, 1:1, 1024×1024.**
- **Transparent background is required** — a PNG with a real alpha channel. No
  room, no floor, no sky, no frame: the subject is isolated on transparency so
  it drops onto the app's dark green surface.
- **One subject per image**, centred, with a little margin on all sides. Never a
  row, grid, or composite infographic.
- Writing surfaces must be **blank or show illegible decorative strokes** — the
  app renders in English and French, so any real lettering would be wrong in one
  of them.

**Negative prompt (all twelve):** `text, letters, words, numbers, labels,
captions, arrows, diagram, infographic, watermark, signature, frame, border,
background scenery, room interior, sky, floor, cartoon, flat vector, low detail,
extra limbs, distorted hands`

Save every file into `shared/src/commonMain/composeResources/drawable/` with
exactly the name given (the app resolves hub assets by basename).

---

## The hero

### `cat_bible_hero.png`
Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — a **large closed leather-bound Holy Bible standing
upright**, deep black-brown tooled leather, gilded page edges, a gold cross
embossed on the front cover, a red ribbon marker trailing from the pages. Lit
warmly from the upper left, a soft glow around it. Reverent, jewel-like. 1:1.

---

## The six points

### 1 · `cat_bible_author_god.png` — God is the Ultimate Author
An aged, majestic **God the Father in the manner of the Ancient of Days**:
long white hair and beard, flowing robes, one hand raised in blessing, haloed in
radiant golden light with soft clouds close around him. Half-length, serene and
paternal, not stern. 1:1.

### 2 · `cat_bible_human_authors.png` — Human Authors Wrote the Bible
A **close group of four or five sacred writers of different ages and walks of
life** — a robed king, a bearded priest, a young fisherman, an older scholar —
standing shoulder to shoulder, each holding a scroll, codex or reed pen. Varied
faces and dress, unified in one gathering. Half-length. 1:1.

### 3 · `cat_bible_long_period.png` — Written Over a Long Period
A **hand holding a quill, writing on an open scroll** that unrolls across the
frame, an inkwell beside it. Only the hand and forearm in a linen sleeve are
shown. The scroll bears faint illegible decorative strokes, never real letters.
Warm parchment tones. 1:1.

### 4 · `cat_bible_holy_spirit.png` — Inspired by the Holy Spirit
A **white dove descending with wings spread**, seen from slightly below, in a
burst of soft golden rays radiating outward. Luminous, weightless, reverent. The
dove fills the centre of the frame. 1:1.

### 5 · `cat_bible_canon_council.png` — The Church Discerned the Canon
A **council of early bishops seated around a table**, in ancient vestments and
mitres, codices and scrolls open before them, in calm discussion — one bishop
gesturing toward an open book. Five or six figures, seen from the front, the
table running across the lower frame. 1:1.

### 6 · `cat_bible_handed_down.png` — Handed Down by the Church
A **monk in a brown habit copying a manuscript at a scriptorium desk**, seen in
three-quarter view, quill in hand, an open codex before him, warm light falling
across the page from the left. Absorbed and patient. 1:1.

---

## The five journey steps

These render small (about 44dp) inside a circle, so keep each one to a **single
bold, simple silhouette** — readable at thumbnail size, no fine detail, no
groups of small figures except where noted.

### `cat_bible_journey_reveals.png` — God Reveals His Word
A **burning bush**: a low bush wrapped in golden flame that does not consume it,
glowing from within. Centred, symmetrical, simple. 1:1.

### `cat_bible_journey_write.png` — Human Authors Write
A **rolled scroll with a quill and an inkwell** resting beside it, arranged as
one compact group. Warm parchment and gold. 1:1.

### `cat_bible_journey_discern.png` — The Church Discerns
A **small stone church with a bell tower and a cross on its roof**, seen
straight on, compact and solid. 1:1.

### `cat_bible_journey_preserve.png` — The Church Preserves
A **large open illuminated codex** seen from the front, pages fanned slightly,
gilded edges, faint illegible decorative strokes on the pages. 1:1.

### `cat_bible_journey_reaches.png` — The Bible Reaches Us
A **family of three reading together** — two parents and a child, close
together, an open Bible held between them, warm light on their faces. Simple
silhouette, half-length. 1:1.

---

## After generating

Drop all twelve PNGs into
`shared/src/commonMain/composeResources/drawable/` and rebuild. Nothing else is
needed — the app resolves these by basename and simply omits any image that is
not yet present, so you can add them a few at a time.
