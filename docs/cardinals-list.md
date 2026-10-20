# The College of Cardinals list

Holy See → Cardinals → **The College of Cardinals today** (`art.cardinals.college`,
rendered by `ui/screens/holysee/CollegeOfCardinalsScreen.kt`) shows every
living cardinal with a photo, country, age, order, the Pope who created him
and his current office.

## Where the data lives

| What | Path | Shipped in the app? |
| --- | --- | --- |
| The list | `shared/src/commonMain/composeResources/files/cardinals/cardinals.json` | Yes, and also read live from `main` |
| Photos (320×400 WebP, ~11 KB each, cut out onto one cardinal-red backdrop) | `remote/cardinals/photos/<id>.webp` | **No**: each downloads from `main` on first view, then stays in app storage (`RemoteCache`) |
| Photo credits | `tools/cardinals/photo-meta.json` → copied into each `photo` entry | — |
| Verified source list | `docs/research/current-cardinals-<date>.csv` / `.md` | — |

Like the news feed, the copy with the newest `updated` wins, so an edit merged
to **main** reaches every install without a release. **Photos only appear once
`remote/` is on main.**

Elector status is not stored: the app counts a cardinal as an elector while he
is under 80 (Universi Dominici Gregis 33), so the numbers change on birthdays
by themselves. A cardinal under 80 without a vote gets `"nonElector"`
(currently only Becciu, `"renounced"`, 2020).

## When the College changes

- **A cardinal dies:** delete his entry from `cardinals.json` (and his photo),
  bump `updated`.
- **New cardinals (a consistory):** add the rows to the CSV, make each photo
  with `tools/cardinals/photos/` (freely licensed Commons image only; it crops,
  cuts out and puts it on the red backdrop), add its credit to
  `photo-meta.json`, then rebuild:

  ```
  python3 tools/cardinals/build_cardinals.py docs/research/current-cardinals-<date>.csv tools/cardinals/photo-meta.json <ISO now>
  ```

- **New office:** edit `office` (offices stay in English, as the Holy See
  publishes them; the screen says so).

## Photo rules

- Only Wikimedia Commons images under CC BY, CC BY-SA, CC0 or public domain.
  No GFDL (it requires shipping the licence text), no Vatican Media or agency
  photos.
- Every photo is credited on its cardinal's page: author · licence ·
  Wikimedia Commons, linking to the file page.
- Check each new crop by eye: Wikipedia's lead image is sometimes a coat of
  arms, and group photos can centre the wrong face (Coccopalmerio needed a
  manual crop).

- Re-edited photos need a new `photosVersion` (in `build_cardinals.py`):
  installs cache photos per version and would otherwise keep the old ones.
- Five photos could not be cut out cleanly and use an oval fade into the same
  backdrop instead: Ferrão, Okogie, Pierre, Zerbo, Martínez Flores.
