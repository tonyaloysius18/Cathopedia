# Home scrolling section concepts — 14 October 2026

Three visual alternatives for the content beneath the fixed Today's Readings card. Option 2 has been selected and implemented with Continue reading changed to a card.

1. **Open layout (recommended)** — borderless saint feature, open prayer actions and a compact recent-reading row.
2. **Portrait carousel** — swipeable saint imagery, compact prayer strip and a recent-reading row.
3. **Daily journal** — one softly shaded panel with saint, prayer and recent-reading sections separated by fine rules.

The supplied screenshot anchors the header, readings, green/gold palette, saint portrait and existing floating navigation. All options depict a clear separation below the readings. The portrait carousel's next-card edge and dots illustrate swipe behavior rather than promise multiple saints on a given date. The short saint description is illustrative UI copy.

Generated with the built-in image tool using the full prompt set in `prompts.json`. The numbered images remain concepts.

The implementation uses bundled portraits, a large portrait card with a soft fade into the text area, a localized discovery action, and a shared prayer strip. The carousel contains the actual saints for that date (including sainted popes) and shows a neighboring card edge and closely spaced indicators on dates with multiple saints. Single-saint dates show a full-width card. Continue reading is an outlined card with a larger thumbnail, title, content type and chevron, linked to the actual most recently viewed item. The fixed 16 dp gap and original floating navigation remain in place; larger text stacks the prayer actions and allows card heights to grow.

Android debug assembly and iOS simulator Kotlin compilation pass. Android emulator checks confirm that the portrait and Continue reading cards open their detail pages, both prayer shortcuts open correctly, and the readings remain fixed during lower scrolling. The prayer strip also works at 130% text size; the original setting was restored. The emulator's current date has one saint, so this native check covers the single-card state. `implementation-checks.json` records the results.

`implemented-home.png` shows the installed screen, `implemented-home-scrolled.png` shows the Continue reading card above the floating bar, and `implemented-home-large-text.png` records the larger-text layout. These screenshots use actual app content and viewing history; the numbered concepts retain their illustrative copy.
