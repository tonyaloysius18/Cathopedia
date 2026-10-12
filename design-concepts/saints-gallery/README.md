# Saints gallery

Implements the approved green-and-gold Saints concept using Cathopedia's existing bundled saint portraits.

- A large portrait card has a luminous double gold frame and a single gentle shine on entry.
- The hero description wraps in full, with no line limit. The card grows to fit longer text. A small soft fade blends the portrait's left edge into the text area; the rest of the portrait remains unshaded. The text column stays left of the image and uses brighter description text.
- Today's highlight is labelled **Saint of the day** above the card, with translations in all nine languages. The heading uses the same cream serif styling as the other section titles; the date uses a smaller size of the same serif font. The card starts directly with the saint's name.
- The feast selection uses the current local date and the saint's recorded `MM-DD` anniversary. Multiple saints on the same date can be swiped between, with compact page dots.
- When today's date has no recorded saint, the next anniversary appears as **Upcoming feast**, with its actual date. This is an anniversary highlight, not a regional liturgical calendar or Mass rank.
- Featured portraits are a curated row: Padre Pio, Thérèse of Lisieux, Francis of Assisi, Anthony of Padua, Teresa of Calcutta and Carlo Acutis. Saints already in the feast card are omitted from that row.
- **See all** scrolls to the complete list. Regular rows use compact portraits and localized feast dates.
- Search filters names and descriptions, hides the gallery while searching, and includes an empty state. Existing sort controls and saint-detail navigation are retained.
- All 22 new labels/date fragments are supplied in the app's nine languages. The content version refreshes labels for existing installations.

## Validation

Android debug build and four focused feast/featured-selection tests passed. Tests cover shared anniversaries, the next date across New Year, invalid and leap-day dates, and the curated selection. All nine language files were checked for complete keys and matching placeholders.

Nine emulator interaction/layout checks passed; results are recorded in `checks.json`. `implemented.png` shows the installed screen on 11 October 2026, with Carlo Acutis's upcoming feast on 12 October. `search.png` shows name/description search results. `large-text.png` records the larger-text layout. The emulator's original text-size setting is restored after the check.

The latest card is shown in `left-edge-fade.png` and `left-edge-fade-large-text.png`, captured on 12 October 2026. Both were visually checked on Android at normal and 130% text size: the heading appears once, the complete description and discovery action remain visible, and only the left edge of the image has a soft fade. Android build passed for this revision. The emulator's original text size was restored. Earlier portrait captures document prior iterations.

`reference.png` is the approved design preview, generated with the built-in image-generation tool. The preview uses 4 October as an example; the application selects its own current date.
