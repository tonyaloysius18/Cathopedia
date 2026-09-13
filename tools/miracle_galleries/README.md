# Miracle gallery pipeline

Builds the per-miracle photograph galleries shown on Eucharistic miracle detail
pages, from the public pages of <https://eucharisticmiracles.faith>.

**Rights:** the photographs are third-party material (Carlo Acutis International
Exhibition panels and others); the source site publishes no per-image licence.
Confirm permission before shipping a build that contains them.

## Steps

```
python3 crawl_em.py        # mirror the 144 miracle pages + their images
python3 map_slugs.py       # their slugs -> our content/miracles ids
python3 build_galleries.py # resize to 1000px, WebP q80, write galleries.json
python3 gen_kt.py          # write shared/.../ui/MiracleGalleries.kt
cp out/drawable/*.webp ../../shared/src/commonMain/composeResources/drawable/
```

`crawl_em.py` writes into `em/` next to itself and is resumable — rerunning it
only fetches what is missing. It keeps a 1s delay between page fetches, which is
the `Crawl-delay` the source's robots.txt asks for.

## Coverage

140 of our 141 miracles have a gallery. `mogliano` has no page on the source
site. `kranenburg` and `meerssen` each merge two of the source's pages. The
source also carries Brussels (1370) and Paris (1290), which have no
`content/miracles/` entry here yet — see `NO_ENTRY` in `map_slugs.py`.

Captions come from the source's own hover text, falling back to its `alt` text
(390 of 1,087 images only have the latter, which reads as a visual description
rather than a caption). All captions are English; `GalleryImage.captionFr` is
wired up but unpopulated.
