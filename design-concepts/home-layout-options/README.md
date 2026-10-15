# Cathopedia home layout options

Three generated concepts for replacing the large content blocks below the readings card. Option 1 has been selected and implemented, retaining the application's existing floating navigation bar.

The lower section has since been refined to the portrait carousel selected in [the later scrolling concepts](../home-scrolling-options-2026-10/README.md), with a shared prayer strip and an outlined Continue reading card. The descriptions and screenshots below record the earlier compact layout.

1. **Compact daily guide (recommended)** — a compact saint row, Morning offering and Rosary shortcuts, and a flat Continue reading row.
2. **Portrait shelf** — a short horizontal shelf combines today's saint and the recently viewed article; a slim Rosary action and topic links sit beneath it.
3. **Simple daily list** — saint, recently viewed article and Rosary appear as flat rows with fine dividers, followed by topic links.

All concepts retain the forest green, ivory serif headings, antique gold details, devotional art and five navigation medallions. The bottom dock has reserved space so it does not obscure content. Long biographies move to detail screens; the reading preview keeps a full-readings action. Reading excerpt, saint, article and date are illustrative examples taken from the supplied screen.

The numbered PNG files remain visual concepts. The implementation uses existing bundled artwork and theme colors, saint/feast rows, a flat recently viewed row, and working Morning offering and Rosary actions. It reserves space below the content for the unchanged floating bar. Longer text can wrap; prayer buttons stack at larger text sizes or on narrow screens. The new labels are translated in the app's nine languages, with content version 257 refreshing existing installations.

The latest refinement enlarges today's saint portrait from 60 to 80 dp, gives the card more padding, and shows up to three lines of description. The header and today's readings stay fixed while the sections below them scroll. A permanent 16 dp gap sits outside the scrolling viewport, keeping its content separated from the readings even during scrolling. If a short screen or very large text leaves too little usable space below the readings, the whole page can scroll so every action remains reachable. The “Browse saints & churches” footer has been removed; Explore remains available through the existing floating navigation bar.

The home content now begins 12 dp lower, including the header artwork and readings; the floating navigation bar keeps its existing position. On Explore, the former “The Holy Mass” card is titled “The Catholic Church”, translated in all nine app languages. Its existing destination and content remain available.

The scripture preview uses the existing bundled verse and translation attribution. The new full-readings action opens that date's English USCCB page in the user's browser, for example [12 October 2026](https://bible.usccb.org/bible/readings/101226.cfm). A failed launch shows an inline retry message. No completion counts, streaks or fabricated prayer progress are introduced.

The three numbered PNG files are the previews. `prompts.json` contains the full design briefs used with the built-in image generator.

## Implementation verification

Android debug assembly and iOS simulator Kotlin compilation pass. Initial emulator checks confirmed the saint detail, Morning offering, Rosary, full-readings browser launch and the former Explore footer navigation. `pinned-readings-checks.json` records the latest checks for fixed readings, removal of the footer and access to the lower content. The existing floating navigation component is unchanged.

The latest checks also confirm that Morning offering opens from the lower section at normal and 130% text sizes, and remains reachable at a shortened 1280 × 1800 display size. The emulator's original text and display settings were restored after these checks.

`implemented-home.png` and `implemented-home-lower.png` show the latest running Android screen; `implemented-home-large-text.png` shows the 130% text check before the final 12 dp spacing adjustment. The current saint and recently viewed item come from app data and can differ from the illustrative concept. `implementation-checks.json` retains the initial implementation's navigation checks. `implemented-explore-navigation.png` shows the renamed Explore card, verified to open its existing content. Chrome's first-use setup prevented verification of the loaded readings webpage; browser launch was verified without changing account or browser settings.
