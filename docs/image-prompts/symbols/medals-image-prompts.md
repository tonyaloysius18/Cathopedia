# Catholic Medals — image generation brief

**Status: done.** All four were generated and are in the app — masters in
`content/hub/symbols/`, shipped as 1024×1024 WebP (q90, alpha preserved) in
`shared/src/commonMain/composeResources/drawable/`. Kept for reference in case
any of them is ever regenerated.

Four images for the **Catholic Medals** section (Explore → Sacred Symbols →
Catholic Medals).

| Slot | Asset |
| --- | --- |
| Medal 3 · Saint Christopher | `symbol_christopher_medal.png` |
| Medal 4 · Sacred Heart | `symbol_sacred_heart_medal.png` |
| Medal 5 · Saint Joseph | `symbol_joseph_medal.png` |
| Medal 6 · Holy Family | `symbol_holy_family_medal.png` |

Medals 1 and 2 reuse existing art: `symbol_miraculous_medal.png` and
`symbol_benedictine_medal.png`. The existing set was checked for the other four
and has nothing that reads as a medal — `symbol_sacred_heart.png` is the flaming
heart **emblem**, not a struck medal, and `symbol_joseph_lily.png` is the lily
alone. Both would sit wrong beside the two real medals.

## House style

Match `symbol_miraculous_medal.png` exactly, since it sits first in the same
article: a single **oval** medal, struck in **polished gold relief**, shown
face-on and filling the frame, with a small ribbed suspension loop at the top
and a beaded rim running round the edge. Soft studio light from the upper left,
warm highlights, deep shadow in the recesses. Shallow bas-relief — figures
raised out of the metal, not painted onto it.

- **1024 × 1024, transparent background (PNG with alpha).** No backdrop, no
  chain, no cast shadow on a surface, no hand holding it.
- **No text, no letters, no numerals, no watermark.** The rim stays beaded and
  plain. (`symbol_benedictine_medal.png` carries lettering because the letters
  *are* that medal's subject; these four must not.)
- Reverent and devotional, not photoreal jewellery-catalogue.

## Per-medal subject

**`symbol_christopher_medal.png`** — St Christopher wading through a river,
water breaking round his knees, a staff in one hand, the Child Jesus seated on
his left shoulder with one hand raised in blessing. Christopher is a big
broad-shouldered man, bearded, his cloak blown back. The water should read as
engraved wave-lines in the metal.

**`symbol_sacred_heart_medal.png`** — the Sacred Heart of Jesus alone, centred:
the heart encircled by the crown of thorns, surmounted by a small cross, flame
rising from the top, and the lance-wound at its side. Rays radiating out to the
rim behind it, struck as fine engraved lines.

**`symbol_joseph_medal.png`** — St Joseph standing, bearded and in a working
man's robe, holding the Child Jesus in the crook of his left arm, a stem of
lilies in his right hand. Quiet, paternal, eyes on the child.

**`symbol_holy_family_medal.png`** — Jesus, Mary and Joseph together: Mary and
Joseph standing on either side, the Child between and slightly forward of them,
their heads inclined toward him. A plain nimbus round each head, struck flat
into the metal. Composition should read clearly at thumbnail size, so keep the
three figures large and the background bare.
