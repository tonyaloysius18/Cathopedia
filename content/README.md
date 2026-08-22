# Cathopedia content

The source of truth for every entity in the app — one JSON file per entry,
organised by type. This is what the roadmap calls the "content pipeline": a
Git repo of JSON, compiled at build time into the single bundle the app
actually reads.

## Adding an entity

Create `<type>/<id>.json`, where `<type>` is one of `saints`, `popes`,
`apostles`, `churches`, `apparitions`, `miracles`, `feasts`, `prayers`, and
`<id>` is a stable slug (lowercase, hyphenated) — it becomes the entity's
permanent ID, so don't rename it once other entities or relations reference
it.

Every file has the same shape: type-specific metadata fields (see the
existing files for the field names per type — they match the SQLDelight
schema in `shared/src/commonMain/sqldelight/.../db/`), plus a `text` map
keyed by language code:

```json
{
  "id": "example-slug",
  "...metadata fields...": "...",
  "text": {
    "en": {
      "name": "Display Name",
      "summary": "One sentence for list rows and search results.",
      "body": "The full detail-page prose.",
      "sourceAttribution": "Where this came from."
    }
  }
}
```

Optional fields (`imageUrl`, `sourceUrl`, most metadata) can be omitted or
set to `null`.

`prayers` is the one exception to the shape above — its `text` entries use
`title`/`subtitle`/`bodyMd`/`attribution`/`source` instead of
`name`/`summary`/`body`/`sourceAttribution` (see PrayerText in Prayer.sq),
and `source` is required, not optional:

```json
{
  "id": "our-father",
  "category": "everyday",
  "sortOrder": 1,
  "isSequence": false,
  "text": {
    "en": {
      "title": "Our Father",
      "bodyMd": "Our Father, who art in heaven...",
      "source": "Traditional English form"
    },
    "la": {
      "title": "Pater Noster",
      "bodyMd": "Pater noster, qui es in caelis...",
      "source": "Traditional Latin form"
    }
  }
}
```

`category` must be one of PrayerCategory's tags (`everyday`, `marian`,
`holy-spirit`, `eucharistic`, `saints`, `penitential`, `sequences`,
`occasional`). Only the six `sequences` prayers should set `isSequence: true`.

### Prayer texts: provenance, not recall

Every prayer text must come from a published, citable, public-domain edition
— never generated or "recalled" wording, however well-known the prayer. A
single drifted word in something like the Salve Regina is a bug people will
notice immediately, and `source` exists precisely so a wrong text can be
traced back to its edition and fixed. If a text isn't sourced yet, its file
stays skeletal (`"text": {}`) rather than getting a plausible-looking
placeholder — `prayerCoverage` (below) tracks that as "not yet sourced," not
an error.

English uses pre-2010 traditional wording throughout — the ICEL 2010 Missal
translation is copyrighted, so the Nicene Creed and Confiteor should use the
older forms. `"consubstantial with the Father"` or `"through my most
grievous fault"` in a prayer file is a bug, not a style choice. French
wording is provisional pending an AELF licence request, which is why `source`
is mandatory on every language row — it's what makes that audit possible.

### Validating and tracking coverage

`./gradlew validatePrayerContent` runs on every `compileContent` (so a normal
build always validates) and fails on: malformed JSON, an `id` that doesn't
match its filename, a duplicate `id`, an unknown `category`, a `sortOrder`
collision within a category, `isSequence: true` on a slug that isn't one of
the six known sequences, a prayer missing `en` or `fr` once it has *any*
language sourced, a blank `title`/`bodyMd`/`source`, or markdown that fails
to parse. A completely empty `"text": {}` is tolerated as a warning, not an
error — pass `-PstrictPrayerValidation` (what CI uses) to promote that to a
failure too.

`./gradlew prayerCoverage` prints a slug × language table (present / missing
/ empty-source), grouped by category with summary counts — the working
checklist while filling the catalogue in.

Both tasks live in `buildSrc/src/main/kotlin/prayercontent/`, since `shared`
has no JVM target for `compileContent`'s own build script to call into for
real JSON/markdown parsing; the field shapes there mirror
`ContentSchema.kt`'s `PrayerContent`/`PrayerLocalizedText` by hand and need
to stay in sync if either changes.

## Rosary mysteries

`mysteries/<id>.json`, one per mystery (`<set>-<n>`, e.g. `joyful-1`):

```json
{
  "id": "joyful-1",
  "mysterySet": "joyful",
  "sortOrder": 1,
  "scriptureRef": "Lk 1:26-38",
  "text": {
    "en": {
      "title": "Annunciation",
      "fruit": "humility",
      "meditation": null
    }
  }
}
```

`mysterySet` is one of `joyful`, `sorrowful`, `glorious`, `luminous`.
`scriptureRef` is a plain Bible citation and lives on the entity, not per
language, since it reads the same in every language — it's `null` for the
Assumption and Coronation, which aren't narrated in a single Gospel passage.
`meditation` is left `null` deliberately; those are being written separately
and aren't in scope for the pipeline itself. There's no `validateMysteryContent`
task — unlike prayer texts, mystery titles/fruits are short factual metadata
supplied directly in the Rosary brief, not liturgical wording needing a
provenance audit, so the same validation concern doesn't apply.

## Cross-links

Add relations in `relations.json` (one flat array, not split per entity):

```json
{ "from": { "type": "saint", "id": "francis-of-assisi" },
  "to":   { "type": "feast", "id": "all-saints" },
  "kind": "commemorated_with" }
```

Relations are resolved and shown from both directions automatically — you
only need to add each edge once.

## Building

`./gradlew :shared:compileContent` (or any normal build — it's wired to run
automatically before the app bundles its resources) reads every file here
and writes the combined bundle to
`shared/src/commonMain/composeResources/files/content/catalog.json`, which
is git-ignored — it's a build output, not something to hand-edit.
