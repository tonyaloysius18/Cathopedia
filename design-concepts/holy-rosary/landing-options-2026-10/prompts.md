# Rosary landing UI concept prompts

Mode: built-in image generator, new UI concept images using existing local product assets as references. Opaque full-screen mockups; transparent background disabled for these screen previews. The app rosary asset itself retains transparency.

## Ordered input references

1. `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_landing_single_decade_marian.png`
2. `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_mysteries_joyful.webp`
3. `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_mysteries_sorrowful.webp`
4. `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_mysteries_glorious.webp`
5. `/Users/tonyaloysius/Documents/Cathopedia/shared/src/commonMain/composeResources/drawable/rosary_mysteries_luminous.webp`

## Option 1

```text
Use case: ui-mockup. Create ONE polished, believable Cathopedia Android mobile screen concept, shown inside ONE subtle graphite phone frame. Portrait 2:3 presentation canvas. Straight-on phone, no perspective; slim visible bezel and centered camera punch hole, full device visible with tidy small balanced margins on a neutral cool-gray canvas. App content must dominate, readable at normal size.
Shared Cathopedia visual system: Marian blue, porcelain white and cool navy, polished silver rosary details. Refined humanist sans typography, medium and semibold hierarchy, 28–30sp main headings, 18sp card titles, 16sp body/button copy, 13–14sp secondary labels. Calm 20dp side margins, 16–24dp vertical spacing, 44–52dp touch targets, restrained 12–16dp radii. Flat grouped surfaces with very subtle paper grain, no heavy shadows, no neon, no purple, no random decorative icons, no dashboard stats or invented user counts. Use French UI copy. Respect status bar, app back navigation and bottom gesture safe area. No website header/footer, no new global tab bar on this nested page.
Image roles: reference image 1 is the existing transparent silver-chain blue-and-white floral rosary with crystal-blue crucifix: reuse this recognizable complete object, scaled down, preserve ten loop beads and one pendant bead. Images 2, 3, 4, 5 are the Joyful, Sorrowful, Glorious and Luminous mystery paintings respectively; use faithful crops as app media. They are product assets, never full-screen background clutter. This is a redesigned concept, not a screenshot recreation. The rosary must occupy at most one quarter of the vertical viewport, with the actual prayer controls and mysteries clearly visible.
OPTION 1: COMPACT SPLIT HEADER + FOUR-MYSTERY GRID, in LIGHT MODE.
Surface is soft porcelain white #F7FAFD, pale ice-blue introductory area, deep navy text #102B48, Marian-blue action #2B6397 with white label. Keep all text and boundaries clearly legible.
Layout inside a realistic 390 x 844dp phone screen:
- Minimal status bar, then compact white app bar with back arrow and centered title "Saint Rosaire". No large colored title block.
- A shallow, unboxed two-column introduction, around 200dp tall. LEFT two thirds: small blue line "Aujourd’hui · Douloureux", large two-line heading "Un moment\npour prier.", concise support "Suivez chaque grain.", and a clearly tappable blue button "Commencer" with a small forward arrow. RIGHT third: the complete reference rosary upright, approximately 175dp high, chain, medal, blue pendant bead and crucifix fully visible. It is a tasteful jewelry cutout on the same surface, no image box, no large empty square. Do not increase its height.
- Clear section title "Choisir les mystères", no redundant explanatory paragraph.
- Four image-led entries in a balanced TWO BY TWO grid. Each image is about 110dp high, with an 18sp title and clearly readable 13sp day caption below. Top left reference 2 "Joyeux" / "Lundi · Samedi"; top right reference 3 "Douloureux" / "Mardi · Vendredi"; bottom left reference 4 "Glorieux" / "Mercredi · Dimanche"; bottom right reference 5 "Lumineux" / "Jeudi". The Sorrowful image alone has a restrained blue "Aujourd’hui" marker and a subtle selected outline. Keep labels on plain clean surfaces below pictures, never busy image text. Generous but compact spacing. All FOUR images and their titles must be visible in this first viewport.
- A simple final row "Reprendre ma prière" with a small forward chevron, above the bottom safe area, indicating saved progress without fabricated numbers.
Composition goal: a clear welcoming intro and Start action, the beloved rosary still visible at the right, and all four mystery categories accessible with much less scrolling. Editorial calm, premium native UI, no giant rosary hero.
```

## Option 2

```text
Use case: ui-mockup. Create ONE polished, believable Cathopedia Android mobile screen concept, shown inside ONE subtle graphite phone frame. Portrait 2:3 presentation canvas. Straight-on phone, no perspective; slim visible bezel and centered camera punch hole, full device visible with tidy small balanced margins on a neutral cool-gray canvas. App content must dominate, readable at normal size.
Shared Cathopedia visual system: Marian blue, porcelain white and cool navy, polished silver rosary details. Refined humanist sans typography, medium and semibold hierarchy, 28–30sp main headings, 18sp card titles, 16sp body/button copy, 13–14sp secondary labels. Calm 20dp side margins, 16–24dp vertical spacing, 44–52dp touch targets, restrained 12–16dp radii. Flat grouped surfaces with very subtle paper grain, no heavy shadows, no neon, no purple, no random decorative icons, no dashboard stats or invented user counts. Use French UI copy. Respect status bar, app back navigation and bottom gesture safe area. No website header/footer, no new global tab bar on this nested page.
Image roles: reference image 1 is the existing transparent silver-chain blue-and-white floral rosary with crystal-blue crucifix: reuse this recognizable complete object, scaled down, preserve ten loop beads and one pendant bead. Images 2, 3, 4, 5 are the Joyful, Sorrowful, Glorious and Luminous mystery paintings respectively; use faithful crops as app media. They are product assets, never full-screen background clutter. This is a redesigned concept, not a screenshot recreation. The rosary must occupy at most one quarter of the vertical viewport, with the actual prayer controls and mysteries clearly visible.
OPTION 2: MYSTERY SELECTION FIRST + FIXED BOTTOM START ACTION, in LIGHT MODE.
Use the same porcelain white #F7FAFD, deep navy #102B48 text, Marian-blue #2B6397 action, same humanist sans, same graphite Android phone and overall visual polish as option 1. This must be a genuinely different structure, not the same two-column hero with swapped text.

Layout:
- Small status bar and compact app bar with back arrow and centered "Saint Rosaire".
- A shallow introduction strip, only about 95dp high. Lay the COMPLETE reference rosary gently HORIZONTALLY across the strip, loop on the left and medal, single blue bead and crucifix on the right. It may be rotated as one connected jewelry object, not redrawn or tangled. Entire object is visible at about 255dp wide and 90dp high, floating directly on a pale ice-blue surface, no box or tall empty space.
- Below the rosary, a strong left-aligned 26sp heading "Choisir les mystères" and short readable support "Une série de cinq mystères." Small clean vertical gap, no big hero headline.
- A compact wide selected TODAY entry: the Sorrowful painting (reference 3) in a panoramic 145dp-high frame, with one restrained "Aujourd’hui" marker. Beneath the image on the same plain surface: large "Douloureux", smaller "Mardi · Vendredi", and a clear blue selected check at right. Use a thin Marian-blue border around this ONE selected entry.
- Three compact FLAT rows below this feature, each about 68dp tall, separated by subtle rules rather than nested floating cards. Left: meaningful 72x58dp image thumbnail from the relevant reference; middle: bold 18sp title plus readable 13–14sp day caption; right: unselected radio circle. In order: reference 2 "Joyeux" / "Lundi · Samedi"; reference 4 "Glorieux" / "Mercredi · Dimanche"; reference 5 "Lumineux" / "Jeudi". All three rows and the selected Sorrowful image are visible together. Do not duplicate Sorrowful as an extra fifth category.
- Bottom DOCKED action area with ample safe-area clearance and enough reserved content space. A full-width blue 52dp button "Commencer · Douloureux" with a forward arrow. It clearly reflects the selected set. Above it, a quiet simple text link "Reprendre ma prière" if it fits cleanly. The bottom action does NOT obscure a mystery row.
UX story: select a mystery set directly, then start it with one bottom action, eliminating a redundant separate mystery chooser. Clean selection states, thumb-friendly controls, small horizontal rosary and visible paintings. No unrelated navigation tabs, no floating controls or fake metrics.
```

## Option 3

```text
Use case: ui-mockup. Create ONE polished, believable Cathopedia Android mobile screen concept, shown inside ONE subtle graphite phone frame. Portrait 2:3 presentation canvas. Straight-on phone, no perspective; slim visible bezel and centered camera punch hole, full device visible with tidy small balanced margins on a neutral cool-gray canvas. App content must dominate, readable at normal size.
Shared Cathopedia visual system: Marian blue, porcelain white and cool navy, polished silver rosary details. Refined humanist sans typography, medium and semibold hierarchy, 28–30sp main headings, 18sp card titles, 16sp body/button copy, 13–14sp secondary labels. Calm 20dp side margins, 16–24dp vertical spacing, 44–52dp touch targets, restrained 12–16dp radii. Flat grouped surfaces with very subtle paper grain, no heavy shadows, no neon, no purple, no random decorative icons, no dashboard stats or invented user counts. Use French UI copy. Respect status bar, app back navigation and bottom gesture safe area. No website header/footer, no new global tab bar on this nested page.
Image roles: reference image 1 is the existing transparent silver-chain blue-and-white floral rosary with crystal-blue crucifix: reuse this recognizable complete object, scaled down, preserve ten loop beads and one pendant bead. Images 2, 3, 4, 5 are the Joyful, Sorrowful, Glorious and Luminous mystery paintings respectively; use faithful crops as app media. They are product assets, never full-screen background clutter. This is a redesigned concept, not a screenshot recreation. The rosary must occupy at most one quarter of the vertical viewport, with the actual prayer controls and mysteries clearly visible.
OPTION 3: TODAY'S PRAYER FIRST + BROWSE SHELF, in the DARK VARIANT of the same Marian-blue visual system.
Use refined cool navy #0F2032 background, slightly lighter slate-blue #192E43 surfaces, porcelain-white #F0F5FC typography, soft Marian-blue #A8CEF3 action. Same graphite Android phone, scale, humanist type family, spacing and card language. Deep matte surfaces with extremely subtle grain, clear contrast. No blue neon glow or glass panels. This should feel calm and contemporary.

Layout:
- Status bar, compact top app bar with back arrow and centered "Saint Rosaire". No oversized colored app bar.
- Compact introduction around 115dp high: LEFT two thirds, quiet small uppercase "AUJOURD’HUI", a bold 28sp two-line title "Mystères\ndouloureux", then a secondary 15sp line "Mardi · Vendredi". RIGHT: complete reference rosary displayed upright, around 108dp high, delicate but recognizable, silver chain, floral porcelain, medal, single blue bead and crystal-blue crucifix. Full object visible, no tall empty hero.
- A meaningful wide Sorrowful painting from reference 3, around 180dp high, with restrained 14dp corner radius. The painting is atmospheric but contained, not a poster across the whole screen. Below it a concise readable 16sp line "Cinq mystères, grain par grain." on the plain navy surface.
- One prominent full-width 52dp pale Marian-blue button "Commencer cette série", navy text and a simple forward arrow. This action starts today's Sorrowful mysteries immediately.
- Below the button, clear section header "Autres mystères". A horizontal swipeable media SHELF: three compact portrait image entries, about 110dp wide, showing meaningful crops from reference 2 Joyful, reference 5 Luminous and reference 4 Glorious. Titles "Joyeux", "Lumineux", "Glorieux" on plain navy below images, readable day captions "Lundi · Samedi", "Jeudi", "Mercredi · Dimanche". The last entry can subtly approach the right edge to suggest horizontal swiping, but don't crop away titles. Keep readable text, no excessive pills or carousel dots.
- A quiet separated bottom row "Reprendre ma prière" with a forward chevron before the bottom gesture safe area. Avoid inventing progress numbers.
Hierarchy: today's set and Start are immediately obvious; the rosary is a small identifying artwork; alternate sets remain visible below and can be browsed with a swipe. One clear primary action. All main sections, including the shelf and resume row, must fit in the first viewport with comfortable touch targets.
Generate one complete legible premium phone screen, no collage, no annotations, no captions outside the phone.
```


## Original outputs

1. `/Users/tonyaloysius/.codex/generated_images/01a11df4-be64-7a01-a28b-89d1a0f656b2/exec-3a288504-4646-4acc-85f6-231193bef81e.png`
2. `/Users/tonyaloysius/.codex/generated_images/01a11df4-be64-7a01-a28b-89d1a0f656b2/exec-8b17bba7-9db6-4c33-acaa-d30e4eaf9bf5.png`
3. `/Users/tonyaloysius/.codex/generated_images/01a11df4-be64-7a01-a28b-89d1a0f656b2/exec-79799fc2-3671-44a7-93aa-7954007c0618.png`

All three selected outputs were copied to this workspace directory in their original resolution. Visual review covered content hierarchy, rosary size, the four mystery choices, readable French labels, primary actions and complete framing. These are concept previews, not Android screenshots or implemented UI changes.

