# Marian Rosary: refined UI and UX

## 1. Enter and choose

A single-decade Marian rosary sits directly on the app surface as a transparent cutout: ten white floral Hail Mary beads in a balanced upright oval, polished silver chain links, a centered Marian medal, one faceted blue pendant bead and a silver crucifix with shining blue crystal inlays and a silver corpus. The selected Option 1 layout displays the complete upright artwork in a compact frame on the right, beside today's set, a short introduction and Start on the left. Start opens today's set directly. The four mystery images follow in a two-column grid, with short set names, prayer days and a Today marker. Tap an image to open its five mysteries in a bottom panel, then Start to pray that set. A compact Resume row and completion history follow the gallery. At larger text sizes the gallery uses one column. [Current implementation and previews](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/README.md).

## 2. Pray with the right-side strand

The prayer occupies the left side and scrolls independently. The Rosary hangs on the right: a single cross, pendant, medal and every decade element remain in the strand. The beads stay upright and aligned to visible alternating metal links, with short intervals between Hail Mary beads and longer spacing around large beads and the pendant.

The selected element has a soft Marian blue radial glow with blurred edges and no selection outline. It stays fully lit and is slightly larger than surrounding beads, which are dimmed without becoming translucent. Each swipe moves exactly one neighboring physical element in either direction, regardless of its length or speed. The strand follows the finger within that one-bead interval, then settles with the selected bead centered. Tap a bead to select it directly. Because the cross hangs at the bottom, later beads are above it. Next and Previous follow the complete prayer sequence. The Creed shares the cross; Glory Be and Fatima share the last Hail Mary bead. An indicator shows which prayer is being recited on a shared bead. The Marian medal is a separate swipe stop, just like the beads and cross. Selecting it shows its Hail, Holy Queen prayer; another swipe selects the adjacent element.

## 3. See the whole Rosary

View Rosary opens a complete overview with the active element highlighted. Close it to return to the same prayer and progress. This gives the user both a detailed strand while praying and a complete view of the physical Rosary.

## Screens

- The current landing previews are `../implemented-option1/landing-light.png` and `../implemented-option1/landing-dark.png`. `landing-silver-crystal-rosary.png` records the earlier layout and the same accepted rosary asset.
- `praying-light.png`: readable prayer on the left, connected strand on the right.
- `praying-medal-soft-glow.png`: Marian medal selected by swiping, with a soft glow and no outline.
- `praying-bead-soft-glow.png`: outline-free glow on a selected Hail Mary bead.
- `overview-medal-soft-glow.png`: complete Rosary with a soft selected-medal glow and no selection ring.
- `praying-shared-bead.png`: several prayers on one bead, without duplicate bead artwork.

The transparent artwork was created with the built-in image generator: [saved PNG](/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_landing_single_decade_marian.png) and [exact edit prompt](/Users/tonyaloysius/Documents/Cathopedia/docs/image-prompts/prayers/rosary-single-decade-silver-crystal-prompt.md).

## Validation

- Android build and iOS simulator Kotlin compilation passed. iOS runtime was not tested.
- All 37 shared tests passed with zero failures or errors.
- Live Android checks covered bead selection, swiping both directions, Next and Previous, the complete Rosary overview and shared-bead prayers.
- All nine Rosary language files include the new landing labels; their existing translation status is preserved.
- The image is an RGBA PNG, with fully transparent pixels in the loop and around the object.
- At 140% text size, the mystery gallery uses one column and the prayer, View Rosary, Previous and Next controls remain readable.
- The medal locks at the center when selected by swiping from either side; subsequent swipes move one adjacent element. The saved session was restored after testing, and the original emulator text size and dark appearance were preserved.

### One-bead swipe refinement

- Native Android checks passed for short swipes, slow long drags, fast long flicks, two consecutive gestures, reverse gestures, different bead sizes and stopping at the Marian medal. Each gesture selected one neighboring physical element and settled it at the carousel center.
- Full check results: `medal-swipe-checks.json`.
- The existing saved session was preserved during testing.

### Marian medal and glow refinement

- The medal is included as its own physical swipe stop. Native Android checks confirmed centering from both directions with short, long and fast gestures, plus two consecutive swipes and both prayers sharing the medal.
- Selection rings were removed from the prayer strand and the complete Rosary overview; only a soft radial blue glow remains.
- The saved session was restored to its original prayer 73, with no completion recorded.

### Single-decade landing artwork

- Replaced the landing image with ten floral Hail Mary beads in an upright oval, silver chain links, one blue pendant bead, a centered Marian medal and a silver crucifix with shining blue crystal panels.
- The built-in image generator created the final PNG. Transparent alpha was verified around the object and inside the loop; the count was checked visually before integration.
- Android build and iOS simulator Kotlin compilation passed. The installed Android app rendered the transparent image correctly on the Rosary landing screen.
- Artwork preview: `landing-silver-crystal-rosary.png`. Current landing layout: `../implemented-option1/landing-light.png`.
