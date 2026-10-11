# Mystery illustrations on Rosary prayer cards

Each decade's dark-blue Our Father bead now shows the corresponding mystery as a transparent illustration in the top-right corner of the prayer card. The title, scripture reference and fruit remain alongside it, and the prayer text follows below the header.

The header uses a flexible row with matching card and image-area heights. Artwork targets 44% of the prayer pane's width, up to 124 dp, while reserving room for complete title words. Compact details padding and a title size between 14 and 16 sp avoid awkward word breaks. The row uses each image's natural proportions and grows if the details need more room. Images use `ContentScale.Fit` with no background, frame, crop or overlay over the prayer.

All twenty mysteries have their own illustration, generated with the built-in `image_gen` tool. Presentation uses the existing feast artwork as its source; the others use matching devotional art. Resurrection and Assumption received a final background-removal edit to remove the surrounding glow.

- Original PNG masters: `/Users/tonyaloysius/Documents/Cathopedia/content/artwork/rosary/mysteries/`
- Bundled WebP illustrations: `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_mystery_*.webp`
- Complete prompts and individual saved paths: [generation manifest](../../../docs/image-prompts/rosary/mystery-prayer-artwork-prompts.json)
- Visual review of all twenty cutouts: [artwork-review.png](artwork-review.png)
- Presentation in the app: [joyful-4-prayer.png](joyful-4-prayer.png)

The twenty optimized images total approximately 1.2 MB, retain transparency, and are bundled locally. `asset-checks.json` records each master and drawable's dimensions and alpha validation.

Android debug build passed. The packaged APK contains all twenty assets matching the content mystery IDs. Presentation was checked on the emulator: artwork sits to the right of the mystery details and above the prayer heading, with the full Our Father visible.

Final emulator check passed for all twenty blue mystery beads across the four sets. Each showed the correct localized mystery title and illustration, positioned to the right of its details and above the Our Father heading without overlapping the prayer. `all-mystery-placement-checks.json` records the verified bounds.

The subsequent size increase uses 40% of the available header width, approximately 18% larger on the checked phone. The Android build passed again; the Eucharist card's long title and complete Our Father remain visible without scrolling. See [larger artwork preview](luminous-5-larger-artwork.png) and `larger-artwork-placement.json` for this check.

The latest header adjustment increases the artwork by another 10% on the checked phone and matches the details card to the image area's height. The Android build passed. Transfiguration now fits its title on one line, with the full prayer visible. See [Transfiguration preview](luminous-4-equal-height-header.png) and `equal-height-header-checks.json` for this layout check.
