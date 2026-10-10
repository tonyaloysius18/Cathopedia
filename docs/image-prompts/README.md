# Image prompts

Image-generation briefs for Cathopedia's artwork, grouped by the part of the app
they illustrate. Each brief names the exact files the content already points
at, so an image appears in the app as soon as its file exists. Most briefs open
with a **Status** line saying whether their images have been generated.

| Folder | Covers |
| --- | --- |
| `holy-see/` | The Holy See hub: Sistine ceiling, Fisherman's Ring, hierarchy, cardinals, patriarchs |
| `mass/` | The Holy Mass hub: the Mass, Mass types, books, monstrance parts, liturgical year, candle holders |
| `catechism/` | Catechism articles: sacraments, Four Marks, Last Things, angels, relics, sacred images, the Bible |
| `symbols/` | Sacred Symbols: the symbol set, medals, scapulars |
| `orders/` | Religious Orders: founders and emblems, the Franciscan family |
| `priesthood/` | The Priesthood hub |
| `biblical/` | Biblical Characters |
| `prayers/` | Prayers, novenas, category icons, the Rosary screens |
| `stations/` | Stations of the Cross |

`mass/monstrance-parts-image-prompts.md` and `-v2.md` are two versions of the
same brief, written from different angles. Both are kept.

## Delivering images

1. Keep the generated masters (PNG) under `content/hub/<hub>/`.
2. Ship them as **WebP** with the same base name in
   `shared/src/commonMain/composeResources/drawable/`. Use q90 for new art, and
   keep the alpha channel when the brief asks for a transparent background.
   Every file in `drawable/` is WebP; keep it that way.
3. Rebuild. Content JSON may still spell a path `.png` or `.jpg`. That's
   harmless, because `hubAssetPainter` resolves drawables by file name without
   the extension.
