# The apostolic nuncios list

Holy See → Vatican Diplomacy has three articles:

1. **The Holy See in the World** (`art.diplomacy.overview`): the existing overview.
2. **The Apostolic Nunciature** (`art.diplomacy.nunciature`): a normal block article on
   what a nunciature is, the nuncio's two missions (cann. 362–367), the other titles
   (delegate, observer, chargé d'affaires), the deanship, and the Academy.
3. **Apostolic nuncios today** (`art.diplomacy.nuncios`, rendered by
   `ui/screens/holysee/ApostolicNunciosScreen.kt`): every envoy of the Holy See with a
   photo, the countries he serves, and the posts that are vacant.

## Where the data lives

| What | Path | Shipped in the app? |
| --- | --- | --- |
| The list | `shared/src/commonMain/composeResources/files/nuncios/nuncios.json` | Yes, and also read live from `main` |
| Photos (320×400 WebP, cut out onto the same emerald backdrop as the cardinals) | `shared/src/commonMain/composeResources/files/nuncios/photos/<id>.webp` | Yes |
| Verified source table (one row per post) | `docs/research/apostolic-nuncios-<date>.csv` | — |
| Each envoy's name, birth date, nationality and photo credit | `tools/nuncios/envoys.json` | — |
| Place names in the 9 languages | `tools/nuncios/places.py` (adds to `tools/cardinals/countries.py`) | — |

Like the cardinals list, the copy with the newest `updated` wins, so an edit merged to
**main** reaches every install without a release. An envoy whose photo is not bundled
yet downloads it from the same path on `main` (`ApostolicNuncios.photo`).

CSV columns: `post` (a key of `places.py`), `kind` (`nunciature`,
`nunciature_nonres`, `delegation`, `observer`, `representative`,
`pontifical_representative`), `seat` (where a delegation sits, if outside the
territory), `since` (appointment date, or date the post fell vacant) and `envoy`
(his English Wikipedia title; blank if vacant). An envoy's first post is the one he
lives at; the rest are listed as extra accreditations.

## When an appointment changes

1. Edit the row(s) in a new dated copy of the CSV (source: the Holy See Press Office
   bulletin "Resignations and Appointments"; Wikipedia's "List of heads of the
   diplomatic missions of the Holy See" is a convenient cross-check).
2. A new envoy needs an entry in `envoys.json`. Add a photo only if a freely licensed one
   exists on Wikimedia Commons (CC BY, CC BY-SA, CC0, public domain; **no GFDL**, no
   Vatican Media or agency photos). Make the portrait with the cardinals pipeline
   (`tools/cardinals/photos/README.md`) and check it by eye: Wikipedia's lead image is
   often a coat of arms, or a group photo.
3. Rebuild and bump nothing else:

   ```
   python3 tools/nuncios/build_nuncios.py docs/research/apostolic-nuncios-<date>.csv <ISO now>
   ```

4. Merge to main. Re-edited photos need a higher `PHOTOS_VERSION` in `build_nuncios.py`.

## Things to keep right

- The nunciature in Taipei is listed among the vacancies but is run by a chargé
  d'affaires (`CHARGE` in the builder), as it has been since 1971.
- The UN rows are split by office (New York, Geneva, Vienna, Nairobi, FAO in Rome,
  UNESCO in Paris, UN Tourism in Madrid).
- Nationality comes from Wikidata (citizenship, ignoring Vatican City). It is
  overridden by hand where Wikidata lists two countries, and for envoys with no Wikidata
  item yet; the Press Office curriculum vitae is the source for those.
- About a third of the envoys have no free photo; they show a silhouette and the line
  "No freely licensed photo is available yet."
