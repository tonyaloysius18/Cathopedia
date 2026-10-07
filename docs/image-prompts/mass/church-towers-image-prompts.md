# Types of Church Towers — image generation brief

15 images for "Types of Church Towers" (`art.mass.towers`, Holy Mass hub,
section 21 "Church Towers"). The page uses the **Sacred Symbols card
layout**: each tower is a card with its name, a short text and the image
shown **small, to one side**. So every image must read clearly as a small
thumbnail. A strong silhouette matters more than fine detail.

| # | Asset | Tower type | Status |
| --- | --- | --- | --- |
| 1 | `tower_bell.png` | Bell tower (campanile) | **done** 2026-10-07 |
| 2 | `tower_steeple.png` | Steeple | **done** 2026-10-07 |
| 3 | `tower_spire.png` | Spire | **done** 2026-10-07 |
| 4 | `tower_clock.png` | Clock tower | **done** 2026-10-07 |
| 5 | `tower_fleche.png` | Flèche | **done** 2026-10-07 |
| 6 | `tower_crossing.png` | Crossing tower | **done** 2026-10-07 |
| 7 | `tower_west.png` | West tower | **done** 2026-10-07 |
| 8 | `tower_twin.png` | Twin towers | **done** 2026-10-07 |
| 9 | `tower_central.png` | Central tower | **done** 2026-10-07 |
| 10 | `tower_detached.png` | Detached bell tower | **done** 2026-10-07 |
| 11 | `tower_gable.png` | Bell gable (campanile a vela) | **done** 2026-10-07 |
| 12 | `tower_onion.png` | Onion-domed tower | **done** 2026-10-07 |
| 13 | `tower_romanesque.png` | Romanesque tower | **done** 2026-10-07 |
| 14 | `tower_gothic.png` | Gothic tower | **done** 2026-10-07 |
| 15 | `tower_baroque.png` | Baroque tower | **done** 2026-10-07 |

## House style (all 15)

Match the app's existing church art, **`mass_basilica.png` and
`mass_minor_basilica.png`**: open one of them beside every new image.

- Original, generic buildings, **not** portraits of real churches (the text
  names real examples; the art only shows the idea).
- Classical architectural illustration, **warm cream-and-honey stone**,
  terracotta or slate roofs where a roof shows, **warm golden light from the
  upper left**, fine but not fussy detail, reverent and still.
- **Same three-quarter view and the same light** in all 15, so the set reads
  as one family.
- **Transparent background is required**: a PNG with a real alpha channel,
  with at most a thin strip of paving at the base. No sky, no clouds, no
  trees, no bushes, no people, no street. (The source infographic has trees
  and sky; leave them out.)
- Square **1:1, 1024×1024**. The subject is centred and fills about 85% of
  the frame height, so it still reads when shown at thumbnail size.
- Every tower carries a small cross at its top, except where noted.
- **No text, no labels, no numbers, no watermark, no signature.**
- After generating, check the alpha channel. Earlier cut-outs arrived with
  the transparency checkerboard baked into the edges.

**Negative prompt (all):** `text, letters, numbers, labels, watermark,
signature, sky, clouds, trees, bushes, grass, people, cars, street,
landscape, background scenery, cartoon, flat vector, sketchy outline, low
detail`

**Keep the confusable pairs apart.** The page exists to tell these apart, so
the art must too:

- **Steeple (2) vs Spire (3):** the steeple shows the whole tower with its
  spire on a small church; the spire is a close view of just the tapering
  top and the upper stage of the tower it sits on.
- **Crossing (6) vs Central (9):** the crossing tower sits where a
  cross-shaped church's arms meet, with the **transepts clearly visible**.
  The central tower sits mid-way along a **simple rectangular church with no
  transepts**.
- **Bell tower (1) vs Detached (10):** the bell tower is **joined** to the
  church; the detached one stands **apart** with a visible gap of paving.
- **Flèche (5):** a slim spire rising **straight from the roof ridge**, with
  **no tower** beneath it.

Each prompt below starts with this shared opening; paste it in front of
the subject line:

```
Create an original classical architectural illustration, warm
cream-and-honey stone, warm golden light from the upper left, three-quarter
view, centred, on a plain transparent background with only a thin strip of
paving at the base, matching a companion set of church illustrations. No
sky, no clouds, no trees, no people, no text, no labels, no watermark, no
signature. Export with a transparent alpha channel. Square 1:1.
Subject:
```

---

### 1. `tower_bell.png`: bell tower (campanile)

```
A square Italian-style bell tower attached to the side of a small church
with a terracotta roof. The tower rises well above the roof in several
stages, with an open belfry at the top: arched openings on each side with
a bronze bell clearly visible inside. A low pyramidal terracotta cap and a
small cross on top.
```

### 2. `tower_steeple.png`: steeple

```
A small village church seen whole, with a single tall square tower at its
front rising above the slate roof, crowned by a tall slender pointed slate
spire with a small cross. The tower and spire together form one continuous
steeple silhouette, clearly the dominant feature.
```

### 3. `tower_spire.png`: spire

```
A close view of just the top of a stone church tower and the tall, very
slender, tapering stone spire that crowns it, with small gabled openings
(lucarnes) on the spire's lower part, crockets along its edges and a cross
at the very tip. Only the upper stage of the tower shows; the spire fills
most of the frame.
```

### 4. `tower_clock.png`: clock tower

```
A tall square church tower with a large round clock face (white dial, black
Roman numerals shown only as small marks, gilded hands) set prominently on
the side facing the viewer, a second clock face visible on the adjacent
side, an arched belfry above, and a small domed cap with a cross.
```

### 5. `tower_fleche.png`: flèche

```
A long Gothic church roof of grey slate seen from a three-quarter angle,
with a single small, slender, open-work lead and timber spire rising
straight out of the roof ridge above the crossing. There is no stone tower
beneath it. A small cross at its tip. The church walls and buttresses show
below the roof.
```

### 6. `tower_crossing.png`: crossing tower

```
A cross-shaped stone church seen from above at a three-quarter angle, with
the nave and both transept arms clearly visible, and a square tower with a
low pyramidal roof and a cross rising exactly where the four arms meet.
Small arched windows in the tower let light into the crossing below.
```

### 7. `tower_west.png`: west tower

```
A simple English-style stone parish church seen from the west end: a single
square battlemented tower standing at the end of a long nave, the main door
at the foot of the tower, the nave roof stretching back behind it. Small
pointed belfry windows near the top. Crenellations around the tower top and
a small cross on one corner pinnacle.
```

### 8. `tower_twin.png`: twin towers

```
The west façade of a large church with two identical tall square towers on
either side, framing a great central portal and a round rose window between
them. Both towers have arched belfry openings and pointed caps with crosses.
A strongly symmetrical, frontal-leaning three-quarter view.
```

### 9. `tower_central.png`: central tower

```
A long, simple rectangular stone church with no transepts, seen from the
side at a three-quarter angle, with a single square tower rising from the
middle of the roof halfway along the nave. The tower has arched openings
and a low pyramidal cap with a cross. The plain unbroken nave on both sides
of the tower makes its central position obvious.
```

### 10. `tower_detached.png`: detached bell tower

```
A small Italian church with a terracotta roof on one side, and standing
clearly apart from it, separated by a gap of paving, a tall slender
free-standing square bell tower with arched belfry openings at the top and
a small cross. The open space between church and tower is unmistakable.
```

### 11. `tower_gable.png`: bell gable (campanile a vela)

```
The front of a small whitewashed or stone chapel whose façade wall rises
above the roof into a flat, stepped bell gable pierced by two or three
round-arched openings, each holding a small bronze bell, topped by a small
cross. No tower behind it: the bells hang in the wall itself.
```

### 12. `tower_onion.png`: onion-domed tower

```
A tall church tower in the Central European baroque style, cream stone and
white plaster, crowned with a green copper onion-shaped dome, a small
lantern above it and a second smaller onion, finished with a gilded cross.
The bulbous onion silhouette is the clear focus.
```

### 13. `tower_romanesque.png`: Romanesque tower

```
A massive, square Romanesque stone tower with very thick walls, few small
round-arched windows arranged in pairs at the top stage, flat pilaster
strips and blind arcading, and a low pyramidal terracotta roof with a small
cross. It is solid, heavy and fortress-like, rising from a low church roof.
```

### 14. `tower_gothic.png`: Gothic tower

```
A tall, soaring Gothic stone tower with tall pointed-arch windows, slender
pinnacles at every corner and stage, delicate stone tracery, and an
openwork stone spire with crockets rising to a cross. Every line points
upward and the whole tower looks light and lace-like.
```

### 15. `tower_baroque.png`: Baroque tower

```
An ornate Baroque church tower rising in several decorated stages, with
columns, volutes, curved pediments and small statues, topped by a curved
cupola, a lantern and a small gilded cross. Cream stone with honey-gold
accents, rich and joyful, its silhouette full of curves.
```

---

## After generating

Drop the PNGs into `docs/image-prompts/mass/` and say so. They will be
converted to WebP in `shared/src/commonMain/composeResources/drawable/`. The
content already references them (`content/hubs/mass.json`, article
`art.mass.towers`, as `hub/mass/tower_<name>.png`). Until they land, each
tower card shows its name and text without a picture.
