# The Fisherman's Ring: image generation brief

**Status: done (2026-09-19).** All three images are in the app. Masters are in
`content/hub/holy_see/`, and they ship as 1024×1024 WebP in
`shared/src/commonMain/composeResources/drawable/`. This brief is kept for
reference in case any image is regenerated.

Three images for **The Fisherman's Ring** (Explore → The Holy See → Titles &
Insignia → The Fisherman's Ring). The article already points at these paths, so
each image appears as soon as its file exists. Until then the page just skips it.

| Slot | Asset | Where it sits |
| --- | --- | --- |
| 1 · The ring | `fisherman_ring.png` | After the opening paragraph |
| 2 · The face | `fisherman_ring_face.png` | Under "Why a fisherman", after Matthew 4:19 |
| 3 · The cancelled ring | `fisherman_ring_cancelled.png` | Under "When a pontificate ends" |

Nothing in the existing art can stand in. The ring in `holy_see_insignia` (the
section hero) is a tiny detail in a wide still life, and it shows a seated figure,
not the fishing scene.

## Where the art departs from the source infographic

- **No name on the ring.** The source letters "FRANCISCUS" round the face. Real
  rings carry the pope's name, but lettering garbles in generation. A named ring
  would also tie the art to one pope, and Francis's actual ring showed Peter with
  the keys, not in a boat (the article says so in its note). The art shows the
  **traditional** design, left unnamed.
- **No Pope, St Peter or St Peter's figures.** The source surrounds the ring with
  vignettes. The app already has that art elsewhere, and the ring is the subject.

## House style (all three)

Match the section hero, `holy_see_insignia`:

> Classical academic **still-life oil painting**. Warm golden light falling from
> the upper left, deep soft shadows, rich earthy palette. The object rests on
> **dark forest-green velvet** with soft folds. The gold is heavy and warm, with
> worn highlights, not mirror-polished. Reverent, quiet, museum-like.

- **Square 1:1, 1024 × 1024.** The article shows these as rounded square tiles
  at about half the screen width, so the object must **fill about 70% of the
  frame, centred**, and read at thumbnail size.
- **Keep the velvet backdrop. Don't make the background transparent.** It keeps
  the three images consistent with the section hero, and the tile is clipped to a
  rounded square anyway.
- **Generate them as a matched set:** the same velvet, light, gold tone and
  camera height. Images 1 and 3 must clearly be the same ring.
- **No text, no letters, no numerals, no watermark, no signature.**

**Negative prompt (all three):** `text, letters, inscription, name, numerals,
watermark, signature, hand, finger, person, jewellery shop display, gemstone,
diamond, chain, modern studio product shot, chrome, silver, cartoon, flat vector,
blurry`

---

## 1 · `fisherman_ring.png`: the ring

Classical still-life oil painting, warm golden light from the upper left. A
**heavy gold papal signet ring** resting on dark green velvet, seen at a three-quarter
angle so both the flat oval face and the band are visible. The face is a **large
oval bezel** showing, in raised bas-relief, **St Peter in a small boat
casting a net**, with water in engraved wave-lines. The shoulders of the band are
chased with small raised **crossed keys** on one side and a **papal tiara** on the
other. A plain beaded rim runs round the bezel, with **no lettering**. Solid,
ancient and dignified. 1:1.

*Notes:* it must read as a **signet**: flat face, relief image, massive band. No
stone, no jewel. Keep the relief shallow so Peter and the net read as one clear
silhouette.

## 2 · `fisherman_ring_face.png`: the face, head-on

The same ring's bezel, seen **straight on and close up**, so the oval face fills
the frame. In gold bas-relief, **St Peter**, bearded and in a simple tunic, stands
in the stern of a small wooden fishing boat, **hauling a heavy net** over the side
with both hands. The net sags with fish. Gentle waves are engraved beneath, and
there's a hint of the far shore behind. A beaded rim runs round the edge, with **no
lettering**. Warm light rakes across the relief to show its depth. The dark green
velvet shows only at the corners. 1:1.

*Notes:* this is the traditional image the article describes. The action should
be **drawing in the net**, the gathering of people to Christ, not a fisherman
at rest.

## 3 · `fisherman_ring_cancelled.png`: the cancelled ring

The **same ring as image 1**, at the same three-quarter angle and on the same
velvet, but now **cancelled**. A **deep cross is scored across the face**: two
rough gouges, vertical and horizontal, cut through the relief of Peter in the
boat, with bright raw metal in the cuts. Beside the ring lie a **small steel
chisel** and, softly out of focus, a **violet** ribbon. The mood is solemn and
still, marking an ending. The gold is a little duller than in image 1. 1:1.

*Notes:* the scoring should look **deliberate and ceremonial**, a clean cross,
not smashed or broken metal. No hammer blow, no fragments. The ribbon is violet,
the Church's colour of mourning. It must not read as red.

---

## After generating

1. Keep the masters in `content/hub/holy_see/`.
2. Ship them as **WebP (q90)** with the same base names in
   `shared/src/commonMain/composeResources/drawable/`.
3. Rebuild. No content change is needed: `content/hubs/holy_see.json` already
   points at `hub/holy_see/fisherman_ring*.png`, and `hubAssetPainter` resolves
   drawables by file name.
