# Prayer text sources

Support files for the Wikisource importer (task 3b). See
`cathopedia-task-3b-importer.md` and `content/README.md`'s provenance note
for the rules this exists to enforce: no prayer text in this repo originates
from the model — every character is fetched from a published source or
pasted in by hand.

- `wikisource-map.json` — one entry per prayer per expected language (`en`
  + `fr` for all, `la` for the prayers with a Latin original). `page` and
  `edition` start blank; the importer never guesses them. `manual: true`
  marks a language row that will be pasted in by hand instead of fetched.
- `fetched/` — cached raw MediaWiki API responses (`<slug>.<lang>.json`),
  one per successful fetch, committed to git. Re-running the importer makes
  no network calls for an entry that's already cached here, unless
  `-PrefreshWikisource` is passed.

## Gradle tasks

All opt-in — network access, never part of a normal build or CI:

- `./gradlew :shared:importPrayerTexts` — fetches everything with a filled-in
  `page`, converts it, and writes it into `/content/prayers/*.json`. Never
  overwrites a text whose `source` doesn't mention Wikisource (hand-sourced
  entries win).
- `./gradlew :shared:verifyPrayerImport` — runs the importer against
  `sign-of-the-cross`, `anima-christi`, `salve-regina` only and diffs the
  result character-by-character against the committed text, without writing
  anything.
- `./gradlew :shared:importReport` — prints the same state table as
  `importPrayerTexts` (imported / manual / hand-sourced / no-map / failed)
  without writing to `/content/prayers`.
