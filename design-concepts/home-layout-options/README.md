# Cathopedia home layout options

Three generated concepts for replacing the large content blocks below the readings card. Option 1 has been selected and implemented, retaining the application's existing floating navigation bar.

1. **Compact daily guide (recommended)** — a compact saint row, Morning offering and Rosary shortcuts, and a flat Continue reading row.
2. **Portrait shelf** — a short horizontal shelf combines today's saint and the recently viewed article; a slim Rosary action and topic links sit beneath it.
3. **Simple daily list** — saint, recently viewed article and Rosary appear as flat rows with fine dividers, followed by topic links.

All concepts retain the forest green, ivory serif headings, antique gold details, devotional art and five navigation medallions. The bottom dock has reserved space so it does not obscure content. Long biographies move to detail screens; the reading preview keeps a full-readings action. Reading excerpt, saint, article and date are illustrative examples taken from the supplied screen.

The numbered PNG files remain visual concepts. The implementation uses existing bundled artwork and theme colors, compact saint/feast rows, a flat recently viewed row, and working Morning offering, Rosary and Explore actions. It reserves space below the content for the unchanged floating bar. Longer text can wrap; prayer buttons stack at larger text sizes or on narrow screens. All six new labels are translated in the app's nine languages, with content version 256 refreshing existing installations.

The scripture preview uses the existing bundled verse and translation attribution. The new full-readings action opens that date's English USCCB page in the user's browser, for example [12 October 2026](https://bible.usccb.org/bible/readings/101226.cfm). A failed launch shows an inline retry message. No completion counts, streaks or fabricated prayer progress are introduced.

The three numbered PNG files are the previews. `prompts.json` contains the full design briefs used with the built-in image generator.

## Implementation verification

Android debug assembly and iOS simulator Kotlin compilation pass. Emulator checks confirm the saint detail, Morning offering, Rosary, full-readings browser launch and Explore navigation work. The prayer shortcuts also work with system text enlarged to 130%; the original text size was restored after verification. The existing floating navigation component is unchanged.

`implemented-home.png`, `implemented-home-lower.png` and `implemented-home-large-text.png` show the running Android screen. The current saint and recently viewed item come from app data and can differ from the illustrative concept. `implementation-checks.json` records the navigation checks. Chrome's first-use setup prevented verification of the loaded readings webpage; browser launch was verified without changing account or browser settings.
