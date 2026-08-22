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
