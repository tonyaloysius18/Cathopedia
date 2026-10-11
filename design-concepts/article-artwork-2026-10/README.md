# Larger article artwork

Implemented for Types of Candle Holders (`art.mass.candles`) and Offices in
the College of Cardinals (`art.cardinals.offices`) on 2026-10-10.

Each card now presents its transparent illustration above the title and
full-width description. The artwork uses Fit, with a maximum width of
280 dp and a height of 224 dp for holders or 196 dp for portraits, replacing
the previous 90 dp thumbnail. Serif headings, themed green/parchment
surfaces, gold borders and side accents use the existing Cathopedia styles.
Body text is 14 sp with 21 sp line spacing and retains its entity links.

The existing shared symbol-card layout continues to serve other articles.
Missing artwork leaves a normal text card without an empty image area.

Validation: Android debug build passed offline; both redesigned screens
were inspected on the Android emulator, including the complete processional
candlesticks while scrolling. Screenshots here are from the installed build.

- `candle-holders.png`: initial candle-holder screen.
- `candle-holders-scrolled.png`: enlarged processional candlesticks.
- `cardinal-offices.png`: initial cardinal offices screen.
- `preview.png`: both initial screens side by side.
