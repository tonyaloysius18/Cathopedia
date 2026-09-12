# Books Used at Mass — image generation brief

Three images for **Books Used at Mass** (Explore → The Holy Mass → Books Used at
Mass), one per book. The article already points at these paths, so the page picks
them up as soon as the files exist; until then each card shows its number.

| Slot | Asset |
| --- | --- |
| Entry 1 · The Lectionary | `mass_lectionary.png` |
| Entry 2 · The Book of the Gospels | `mass_book_of_gospels.png` |
| Entry 3 · The Roman Missal | `mass_roman_missal.png` |

The existing art was checked first and does not fit. `symbol_gospel_book` is a
gilded open-book **emblem** from the Sacred Symbols set, not a physical book, and
`mass_bookbearer.png` is a walking figure from the Entrance Procession carousel.
Neither reads as one of the three books beside the other two.

House style: classical academic sacred oil painting, still life, fine detail,
warm golden light from the upper left, rich earthy palette, reverent and quiet —
matching the Holy Mass hub's existing art. **No text, no letters, no watermark,
no signature** (see the note below about the symbols on the covers).

## Rules for all three

- **Square 1:1 at 512×512.**
- **Transparent background is required**: a PNG with a real alpha channel. No
  altar, no lectern, no cloth, no table, no shadow plane, no frame.
- **One book per image, centred**, filling about 75% of the frame. The app draws
  these as 72dp tiles cropped to a rounded square, so the silhouette has to read
  at thumbnail size.
- **Same three-quarter view, same eye level, same scale and light in all three**,
  so the column reads as one set. The Gospel book is the grandest of the three;
  the other two are plainer, and should not compete with it.
- Ribbon markers are welcome — they say "liturgical book" at a glance.

**Negative prompt (all three):** `text, letters, words, title, lettering,
readable writing, watermark, signature, frame, border, background scenery, table,
altar, lectern, church interior, floor, hands, people, cartoon, flat vector, low
detail`

**About the cover symbols:** each cover carries one emblem — a Chi-Rho, a cross,
a figure of Christ. These are shapes, not writing. Generators habitually turn
them into pseudo-lettering or add a spine title, so keep "no text" in the
negative prompt for every run and reject any result with letter-like marks. The
Greek letters alpha and omega, which the source infographic shows, are best
avoided for that reason.

---

## `mass_lectionary.png` — entry 1

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **closed liturgical book bound
in deep oxblood-red leather**, seen at a slight three-quarter angle, standing
upright with its cover facing the viewer. The cover is plain but for a **gold
Chi-Rho monogram embossed at the centre** and a fine gold rule around the edge.
**Four coloured ribbon markers** — green, red, white and violet — hang from the
bottom edge. Substantial, well used, the corners a little worn. 1:1.

*Notes:* this is the workaday book of the readings, so it should look handled
rather than jewelled. No clasps, no gemstones.

## `mass_book_of_gospels.png` — entry 2

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **magnificent Book of the
Gospels**, closed and standing upright at a slight three-quarter angle, its
cover **sheathed in worked gold** over cream leather: a **central round medallion
holding an enamel image of Christ blessing**, a **jewelled cross** worked into
the metal above it, and small **blue and red cabochon gems** set at the corners
of a chased border. A single **red silk ribbon** falls from the lower edge.
Byzantine in feel, radiant, clearly the most precious of the three. 1:1.

*Notes:* the medallion figure should be a small icon-like bust of Christ, calm
and frontal, with a cruciform halo — painted as enamel, not as a portrait. Keep
the face simple and undistorted at this scale.

## `mass_roman_missal.png` — entry 3

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **large closed altar missal
bound in burgundy leather**, thicker than the other two, at a slight
three-quarter angle. A **broad gold cross is embossed across the cover**, with a
plain double gold rule near the edges and **gilt page edges** catching the light.
**Three ribbon markers** — red, gold and white — hang from the bottom. Dignified
and heavy, the book of the altar. 1:1.

*Notes:* thickness is the tell here: this book should be visibly the fattest of
the three. No jewels; the gold is tooling, not metalwork.

---

## After generating

Drop the three PNGs into `shared/src/commonMain/composeResources/drawable/` and
rebuild. No content change is needed: `content/hubs/mass.json` already points at
`hub/mass/mass_lectionary.png`, `hub/mass/mass_book_of_gospels.png` and
`hub/mass/mass_roman_missal.png`, and `hubAssetPainter` resolves drawables by
file name.
