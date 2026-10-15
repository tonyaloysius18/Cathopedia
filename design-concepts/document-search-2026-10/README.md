# Adaptive document search cards

Papal-document search results now use content-driven height with a 56 × 72 dp decorative thumbnail instead of a fixed 126 dp card. Titles wrap in full; summaries remain two-line previews. Other search results retain their existing larger portrait dimensions while also allowing long titles to grow the card.

The shared search/saved filter row scrolls horizontally and keeps each label on one line, preventing the Documents filter from wrapping one character at a time.

## Artwork

The [original illustration](papal-document-illustration.png) was generated with the built-in image tool using the [saved prompt](prompt.json). It is decorative category artwork for the 215 papal documents, not a reproduction of any historical document. The existing document content, attribution, source links and detail screens are unchanged.

The bundled asset is [papal_document_cover.webp](../../shared/src/commonMain/composeResources/drawable/papal_document_cover.webp), encoded at 384 × 512 pixels (approximately 38 KB) for the search thumbnails. The original PNG is preserved here.

## Verification

Android debug assembly and iOS simulator Kotlin compilation pass. Emulator checks using the screenshot's `sacre` query confirm that the Documents filter scrolls into view and stays on one line, document cards are shorter than the old fixed height, and tapping a result opens its document. At 140% system text size the cards grow to accommodate the larger title. The original font setting was restored afterward. Results are recorded in [implementation-checks.json](implementation-checks.json).

[All results](implemented-all-results.png), [document results](implemented-document-results.png) and [larger text](implemented-document-large-text.png) show the running Android app.
