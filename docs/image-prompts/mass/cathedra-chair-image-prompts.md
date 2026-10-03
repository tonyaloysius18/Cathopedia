# Cathedra or Presider's Chair? — image generation brief

Two images for the new "Cathedra or Presider's Chair?" comparison article
(Holy Mass hub, section "Cathedra or Presider's Chair?"). Same side-by-side
"VS" comparison layout as Altar and Sacristy, Patriarch and Pope, and the
Priesthood's "Two Kinds of Priests" — the two chairs sit face-off, so they
need to read as a matched pair despite being quite different in ornament.

| Cell | Asset | Status |
| --- | --- | --- |
| Cathedra | `mass_cathedra.png` | **needed** |
| Presider's chair | `mass_presiders_chair.png` | **needed** |

## Rules for both

- **Transparent background is required** — a PNG with a real alpha
  channel. No cathedral interior, no sanctuary, no floor or wall: just the
  chair itself, isolated on transparency, the same convention used for the
  app's other single-object images (the monstrance, the thurible).
- **No text, no labels, no watermark, no signature.**
- Square **1:1, 1024×1024**.
- Classical academic sacred oil painting / fine furniture illustration
  style, warm golden light, reverent and still.
- **Same scale and a roughly three-quarter angled view for both**, so they
  read as a matched pair side by side with a "VS" badge between them —
  the cathedra should simply look grander, not differently staged.

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, people, a bishop, a priest, full room, cathedral interior,
sanctuary, floor, wall, background scenery`

---

## `mass_cathedra.png` — the bishop's cathedra

An ornate Gothic throne: dark carved wood with gilded detail, a tall
pointed canopy (baldachin) rising behind and above the seat back, a deep
red velvet cushion and backrest, armrests, raised on a small plinth or
step to mark its dignity.

```
Create an original illustration: an ornate Gothic bishop's throne (a
cathedra), centered and upright, on a plain transparent background. Dark
carved wood with fine gilded tracery, a tall pointed canopy (baldachin)
rising above and behind the high seat back, a deep red velvet cushion and
backrest panel, carved wooden armrests, and a small raised wooden plinth
or step beneath the seat. Three-quarter angled view. Warm golden light,
fine detail, reverent and dignified, classical sacred-furniture
illustration style. No people, no room, no floor beyond the small plinth
it stands on, no background at all. No text, no labels, no watermark, no
signature — export with a transparent alpha channel. Square 1:1.
```

---

## `mass_presiders_chair.png` — the presider's chair

A simpler wooden chair: plain carved wood, a modest cushion (a deep green
liturgical tone), no canopy, no plinth — dignified but visibly plainer than
the cathedra, matching it in scale and angle.

```
Create an original illustration: a simple wooden presider's chair, centered
and upright, on a plain transparent background. Plain carved dark wood with
modest detailing, a fitted cushion in deep green fabric on the seat and
backrest, plain wooden armrests, no canopy, no plinth, standing directly on
the implied ground. Three-quarter angled view, matching the scale and
framing of a companion cathedra image. Warm golden light, fine detail,
dignified but visibly plainer and smaller than an episcopal throne,
classical sacred-furniture illustration style. No people, no room, no
floor, no background at all. No text, no labels, no watermark, no
signature — export with a transparent alpha channel. Square 1:1.
```

---

## Deliverable

2 **transparent PNG** files, square 1:1 (1024×1024), named exactly:
`mass_cathedra.png`, `mass_presiders_chair.png`. Hand them back as a single
batch, then drop them into `docs/image-prompts/mass/` (or send them in
chat) and tell me — I'll convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` and the content already
references them (`content/hubs/mass.json`, article
`art.mass.cathedra_chair`, `CONTENT_VERSION` already bumped to 112).
