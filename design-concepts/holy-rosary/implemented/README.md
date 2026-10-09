# Implemented Marian Rosary redesign

The opening screen shows a blue-and-white Rosary photograph followed by the four mystery galleries. Tap the photograph or Start Rosary to choose a set; tap a mystery image to preselect that set. Each gallery can expand to show all five mysteries, scripture references and fruits.

The prayer screen has a vertical bead carousel beside the prayer. Swiping the beads up or down snaps to a prayer and updates its text. Previous and Next move the prayer and bead together. Long prayer text scrolls independently. Saved progress, completion tracking and the Rosary meter are retained.

## App screenshots

- [Opening screen — light](landing-light.png)
- [Prayer screen — light](praying-light.png)
- [Opening screen — dark](landing-dark.png)
- [Prayer screen — dark](praying-dark.png)

These are screenshots from the Android emulator, not design mockups.

## Validation

- Android debug build passed.
- iOS simulator Kotlin compilation passed; iOS runtime was not tested.
- 31 shared tests passed across 6 suites, with no failures or skipped tests.
- Checked upward and downward bead swipes, Previous, Next, mystery selection, expanded mystery details and saved-session resume in the Android emulator.
- Checked 140% text size: the gallery uses one column and the Creed scrolls independently while navigation remains visible.
- Restored the emulator's original text size and dark appearance, and left its saved Rosary at prayer 3.

The new artwork was created with the built-in image generator. Its prompt is saved in `docs/image-prompts/prayers/rosary-landing-marian-image-prompt.md`, and the asset is `shared/src/commonMain/composeResources/drawable/rosary_landing_marian.png`.
