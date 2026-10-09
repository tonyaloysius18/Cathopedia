# Cathopedia Rosary — selected Option 1

Implemented the [compact-header concept](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/landing-options-2026-10/option-1-compact-header.png) in the existing shared Compose screen.

The introduction and Start action sit on the left, with the complete transparent rosary on the right. The image takes about 208 dp of height instead of a full-width square. The four mysteries appear immediately below in a two-column image grid. Each card shows its set name and prayer days; today's set has a label and outline. Saved progress uses a compact Resume row, followed by completion history.

Start opens today's set directly. Tapping a mystery opens a bottom panel with its five mysteries, scripture references and fruits, plus a Start action for that set. Resume returns to the exact saved prayer. The accepted ten-bead rosary artwork, silver chain, single blue pendant bead and crystal-blue crucifix are retained.

The screen follows the existing light or dark setting. At 140% text size the mystery gallery switches to one column, and the detail list scrolls while its Start action remains visible. Existing tablet layouts retain their wider split arrangement.

## Actual Android previews

- [Light appearance](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/landing-light.png)
- [Dark appearance](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/landing-dark.png)
- [Mystery detail panel](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/mystery-details-light.png)
- [Landing with larger text](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/landing-large-text.png)
- [Detail panel with larger text](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/mystery-details-large-text.png)
- [Resume at the saved prayer](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/resume-check.png)

## Validation

- Android debug assembly and iOS simulator Kotlin compilation passed. iOS runtime was not tested.
- All 37 shared tests passed, with zero failures or errors.
- Native Android checks confirmed today's Start opens the Sorrowful set at prayer 1, the Joyful detail panel starts the Joyful set at prayer 1, and Resume returns to the original saved prayer 6.
- Both appearances and 140% text size were visually checked. All five mysteries were reachable by scrolling the larger-text panel, with Start still visible.
- All nine Rosary translation files passed schema validation. New landing labels preserve the files' existing translation status.
- The content bundle version was raised to 243 so existing installs receive the new labels.
- The two temporary test sessions were removed. All seven original session records, including the two completed sessions, were preserved exactly. Database integrity passed. Original text size, dark appearance and French language were retained.

## Bead tap feedback refinement

Removed the default rectangular tap ripple from every bead's touch area. The existing soft glow remains the selected-state feedback, with no background block. Native Android checks covered pressing the selected bead and a neighboring bead, selecting the neighbor, and returning to the original prayer 11. All nine current session positions and completion records were preserved. Android assembly and shared iOS simulator compilation passed.

[Bead while pressed, with no rectangular background](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/bead-pressed-no-rectangle.png).

## Matching blue-crystal crucifix

The scrolling prayer strand and complete Rosary overview now reuse the exact silver and blue-crystal crucifix from the small landing rosary. The shared painter displays only the crucifix region of the existing transparent image, including its top eyelet. The source image is unchanged; the normalized display bounds account for density-aware decoding on Android and iOS. Existing bead selection, glow and one-bead swipe handling are retained.

Android assembly and shared iOS simulator Kotlin compilation passed. The shared crucifix painter was visually verified in the native Android [complete Rosary preview](/Users/tonyaloysius/Documents/Cathopedia/design-concepts/holy-rosary/implemented-option1/overview-blue-crystal-crucifix.png), then the overview was closed without advancing its saved prayer. The same painter supplies the scrolling strand.
