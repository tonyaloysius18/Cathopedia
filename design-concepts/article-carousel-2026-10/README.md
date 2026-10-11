# Illustrated article carousels

Implemented in `HubArticleScreen.kt` and `IllustratedArticleCarousel.kt` for **Types of Candle Holders** and **Offices in the College of Cardinals**.

- Horizontal snapping artwork shelf uses the existing College of Cardinals perspective, scaling, and fading effect.
- Swipe the artwork, tap a neighboring card, or tap a dot to select one of seven entries.
- The complete selected title and description appear together beneath the image. The introductory paragraph follows the gallery.
- Artwork height adapts to the viewport after measuring the longest localized description. Shorter screens use a compact header. Very large accessibility text can still scroll instead of being truncated.
- Green surfaces, gold borders and accents, serif headings, and existing illustrations are retained.

Validation captures and JSON reports in this folder document emulator checks. `*-initial.png` shows the initial selected item; `*-swiped.png` shows the next item after a horizontal swipe.

Validation completed: Android debug build and whitespace checks passed. On a 426 × 952 dp emulator, all seven candle entries and all seven cardinal offices showed complete descriptions without vertical scrolling; horizontal swipes and all seven dot controls selected the matching image and description. The longest cardinal description also fit on a 360 × 720 dp viewport; captures and bounds are in `compact/`. The emulator size was restored after this check.

Follow-up adjustment: description cards now use their own content height, removing the unused space beneath shorter entries. Indicator centers are 24 dp apart instead of 48 dp, keeping the dots in a compact centered group. The artwork still uses the longest description for screen-fit sizing so its position remains stable between entries. Earlier captures above predate this spacing adjustment.
