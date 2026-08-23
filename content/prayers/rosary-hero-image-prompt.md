# Holy Rosary hero card image prompt

## Where this is used

The `RosaryHeroCard` on the Prayers home screen (the "THE HOLY ROSARY / Pray
a decade, or all twenty mysteries" card) — as a background/accent photo
behind or beside that card's text.

**Not** for the interactive Rosary screen itself. That screen
(`RosaryCanvas.kt`) draws every bead procedurally in Compose — position,
color, and highlight state are all computed in code so each bead has an
exact, independently-tappable hit target and can be recolored live as you
pray through a decade. A static photo has no per-bead geometry to tap, so it
can't replace that layer; it only supplies decorative art for the card that
launches it. The blue/gold palette below already matches
`RosaryColors`/`rosaryColors()` (`marian` ≈ `#5E8CC4`, `candle` ≈ `#E8C77A`),
so the two will read as one consistent rosary across the app even though
one is a photo and the other is drawn.

## The prompt

Match your reference photo closely:

> A traditional five-decade Catholic rosary laid out in soft loose coils on
> a rustic dark-stained wood plank table, photographed close-up from
> directly above at a slight angle. The rosary: round ~10mm porcelain beads
> in white with a hand-painted cobalt-blue floral (chinoiserie-style) sprig
> pattern on each bead, small faceted gold-tone metal caps/spacers between
> every bead. Five larger Our Father beads are transparent deep cobalt-blue
> faceted glass, also capped in gold, spaced evenly around the loop marking
> each decade. At the center where the loop closes, a small oval gold
> devotional medal stamped with an image of the Virgin Mary, bordered by a
> fine ring of tiny seed pearls. From the medal, a short extension of two
> more blue glass beads leads down to a gold filigree Latin cross pendant,
> its arms encrusted with small white pearls, no corpus (no figure of
> Christ — a plain ornamented cross, not a crucifix). Warm directional
> natural light from the upper left, soft shadows, shallow depth of field
> with the medal and cross pendant in sharpest focus. A loosely woven piece
> of burlap/hessian fabric visible in one corner of the frame, and a hint of
> an aged gilt picture-frame edge in the opposite corner, both softly out of
> focus. Photorealistic product photography, rich detail on the porcelain
> floral pattern and the gold filigree, no text, no watermark, no hands.

**Format**: landscape, roughly 3:2 or 16:10 (to sit behind/beside the hero
card's text block — check `RosaryHeroCard`'s current layout before
cropping), high resolution (at least 1600px on the long edge) so it holds up
scaled for both the card thumbnail and a possible full-bleed use elsewhere.

## Once you have the image

Save it as `rosary_hero.jpg` and hand it back — I'll drop it into
`shared/src/commonMain/composeResources/drawable/` and wire it into
`RosaryHeroCard` in `PrayersHomeScreen.kt` (currently a flat gradient +
Compose-drawn bead decoration; this would sit behind or alongside that,
at low-to-moderate opacity so the card's title/subtitle/button stay legible
per the same treatment used on the prayer detail screens' backgrounds).
