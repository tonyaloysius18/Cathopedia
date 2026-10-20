# Catholic News feed

The Home screen's **Catholic News** carousel and the **Catholic News**
screen ("See all") read one hand-curated file:

`shared/src/commonMain/composeResources/files/news/news.json`

The same file does two jobs:

1. **It ships inside the app** as the offline fallback.
2. **It is the live feed.** On each launch the app fetches it from
   `raw.githubusercontent.com/tonyaloysius18/Cathopedia/main/...`, so a
   change **merged to `main`** reaches every install within minutes, with
   no app release. Edits on `stage` stay invisible until merged.

The app keeps the newest of the bundled, cached and remote copies, compared
by `updated`. **Bump `updated` on every edit**, or installed apps keep their
cached copy.

## Item fields

| Field | Notes |
| --- | --- |
| `id` | Stable, lowercase, unique (also the image filename). |
| `kind` | `"news"` or `"event"`. Events get a countdown ("In 287 days", "Tomorrow", "Happening now"). |
| `theme` | The card's colours and glyph: `"wyd"` (official WYD Seoul palette), `"papal"` (crossed keys), `"journey"` (plane), `"cardinal"` (galero), `"mission"` (globe), `"document"` (book) or `"default"`. |
| `pinned` | Pinned items come first, soonest `start` first; the rest are sorted newest `published` first. |
| `published` | `YYYY-MM-DD`. |
| `start`, `end` | Event dates (`end` optional). |
| `expires` | Last day shown. **Always set it**: stale news is worse than none. |
| `image` | `null`, or `"images/<id>.webp"` (960×540; a transparent left side lets the card colour show behind the title). Freely licensed Commons photos are fine. |
| `imageAuthor`, `imageLicense`, `imageSource` | Credit for `image`, shown on the detail page as "Photo: author · licence · Wikimedia Commons" and linking to `imageSource`. Required for any CC photo. |
| `badge`, `badgeCredit` | An official logo (transparent WebP) shown on the card. Use a light theme (like `"wyd"`) for logos made for white (e.g. `"images/wyd-seoul-2027-logo.webp"`) and its credit line on the detail page. Only with the owner's consent; delete both fields on `main` to withdraw it from every install. |
| `tagline` | Optional per-language line shown large on the detail banner, as `"quote — reference"` (e.g. the event's theme verse). |
| `source` | Display name of the official source ("Vatican News"). |
| `url` | Per-language map of official links; `en` is the fallback. |
| `title`, `location`, `summary`, `body` | Per-language maps (en, fr, es, it, pt, de, nl, pl, ta). Missing languages fall back to English. Separate `body` paragraphs with a blank line (`\n\n`). |

Home shows the first 5 visible items.

## Editorial rules

- Only facts confirmed by an official source (Vatican News, Holy See Press
  Office, the event's own site), and always link to it.
- Write the summary and body in our own words. Never paste an article.
- Images: our own art, or Wikimedia Commons photos under CC BY / CC BY-SA / CC0 /
  public domain, always credited. No press-agency or Vatican Media photos.
  The WYD Seoul logo needs the Foundation's written consent: see
  `docs/permissions/wyd-seoul-logo-request.md`.
- Health and other sensitive stories: plain facts, no speculation, and an
  invitation to prayer at most.

Card UI: `ui/screens/news/NewsCards.kt`. Screens: `NewsScreen.kt`.
Loader: `data/CatholicNews.kt`. UI strings: `screen.news.*` in
`content/strings/screens.<lang>.json`.
