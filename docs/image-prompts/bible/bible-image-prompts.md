# Holy Bible: image generation brief

**Status:** all three images generated and applied. PNG masters are in `content/hub/bible/`;
alpha-preserving WebP assets are bundled in `composeResources/drawable/`. The Explore card uses
the new artwork and medallion, and the centre Bible tab stays selected throughout the reader.

Three images for the **Holy Bible** reader. Two are covered here: the wide
Explore card artwork and the small round icon in the card's corner. The third,
the bottom-bar tab icon, has its own section below (§3). The first two are for
the reader (Explore → Holy Bible): the wide
Explore card artwork and the small round icon in the card's corner. They must
sit alongside the other Explore cards (Catechism, Holy See, Holy Mass, Biblical
Characters…), so they use the same look.

House style: rich, glossy **gilded-emblem** illustration, with polished gold,
deep forest-green leather, warm cream parchment and soft golden light, the same
style as `catechism_icon` and `explore_catechism`. Reverent, jewel-like, finely
detailed.

## Rules for both images

- **Transparent background is required.** Deliver a PNG with a real alpha
  channel, with no room, sky, table edge or frame behind the subject. The card
  is dark green in dark mode and pale in light mode, so the art must look right
  on both.
- **No lettering at all.** The app ships in 9 languages, so do not put
  "Holy Bible", "Bible", "IHS" or any other word on covers or pages. Pages show
  only fine illegible lines of decorative "text".
- No watermark, no signature, no border.

**Negative prompt (both):** `text, letters, words, title, lettering, numbers,
labels, watermark, signature, frame, border, background scenery, room, sky,
table, floor, cartoon, flat vector, low detail, blurry, distorted hands, extra
fingers`

---

## 1 · `explore_bible.png`: the Explore card artwork

**Size:** wide, **16:9, 1672×941** (the same as `explore_catechism`).
**Composition:** the subject sits in the **right two-thirds** of the frame. The
left third stays **empty and transparent**, because the card prints its title
and subtitle over the bottom-left corner. Keep everything within the frame,
with nothing cropped at the edges.

> Rich gilded-emblem illustration, warm golden light, transparent background.
> On the right, a **large open Holy Bible** lies on a low gold lectern-cushion,
> its cream parchment pages fanned and gently curving, covered in fine
> illegible lines of text, with gilded page edges and a **red silk ribbon
> marker** trailing down over the front. The cover is deep forest-green leather
> with ornate gold corner pieces. Behind the book rises a soft **golden radiance
> of fine rays**, with a small glowing **gold Latin cross** at its centre above
> the pages. On one side, a sprig of **gold olive leaves**. On the other, a
> **lit white candle** in a polished gold candlestick. Gold sparkles float
> faintly around it. The left third of the image is empty transparency.
> Elegant, reverent, highly detailed, glossy polished gold. 16:9.

*Optional variant, if the first looks too much like the Catechism card:* swap
the candle for **two small closed books** stacked beside the open Bible, one
bound in green and one in red, both with gold clasps, suggesting the Old and
New Testaments together.

---

## 2 · `bible_icon.png`: the round card icon

**Size:** square, **1:1, 1024×1024** (it is shown at about 35 dp, so it must
still read when tiny).
**Composition:** a single round medallion filling the frame, with a little
transparent margin.

> Round gilded medallion icon, transparent background. A **thick polished gold
> outer ring** with a bright bevel encircles a **deep forest-green enamel
> disc**. In the centre, an **open Holy Bible** seen slightly from above, with
> cream pages, gilded edges, a red ribbon marker hanging below, and fine
> illegible lines on the pages. Above the book, a small **glowing gold cross**
> with a soft halo of short rays. A single **gold olive sprig** curves along
> the lower left inside the ring. Bold, simple shapes that stay readable at
> very small sizes, with high contrast between the gold and the green. Same
> style as a catechism medallion icon. No lettering. 1:1.

---

## 3 · `nav_bible.png`: the bottom-bar tab icon

A new tab in the bottom bar, in the **centre slot between Explore and
Prayers**. It must look like one set with the existing coins: `nav_home`
(church), `nav_explore` (compass rose), `nav_prayers` (rosary), `nav_settings`
(gear) and `nav_search` (magnifier). Those coins use the medallion style
described in `docs/image-prompts/prayers/category-icon-prompts.md`. This prompt
keeps that style exactly and only changes the emblem.

**Size:** square, **1:1, 1200×1200**, transparent PNG.
**Shown at:** about 36 dp, so the emblem must read as "a Bible" at thumbnail
size. Use one bold shape and no fine detail that would turn to mush when small.

> A circular gold-rimmed medallion coin icon, transparent background outside
> the coin. A **thick polished gold rim** with bright specular highlights and a
> beveled edge, matching a set of gold coin navigation icons. Inside the rim, a
> **deep emerald-green enamel disc** (#0B1A15 to #1F4034, glossy). Centred on
> the disc, a single polished, photorealistic **3D gold-metal emblem: an open
> Holy Bible** seen from the front and slightly above, its two pages curving
> upward like wings, gilded page edges visible beneath. A **small Latin cross**
> stands upright, centred just above the book's spine, in the same gold. A
> short **ribbon marker** hangs from the bottom centre of the book. The pages
> are plain gold with at most two or three faint engraved lines per page, and
> no lettering. Fine engraved highlights, soft reflections, a subtle drop
> shadow onto the enamel for real metallic depth. The emblem fills about 60%
> of the disc, the same visual weight as the compass rose and the church in the
> sibling icons. Not a flat vector icon. No text, no letters, no sparkles, no
> leaves, no extra ornament beyond the rim and the single emblem. 1:1, 1200×1200.

**Palette:** gold `#D4AF37`, with highlight gold `#DDBF5F`. The enamel is the
same dark green as the other coins, never a brighter green.

**Must stay distinct from the others:** the emblem should not look like the
church (`nav_home`) or the rosary (`nav_prayers`). The open-book silhouette has
to be the first thing you see.

**Negative prompt:** `text, letters, words, IHS, numbers, sparkles, stars,
leaves, candle, multiple objects, flat vector, cartoon, low detail, blurry,
silver, bronze, bright green, blue, frame outside the coin, background,
watermark, signature`

---

## Delivering

1. Keep the PNG masters under `content/hub/bible/`.
2. Convert to WebP with the same base name, **keeping the alpha channel**, and
   put the WebP files in `shared/src/commonMain/composeResources/drawable/`:
   - `explore_bible.webp`, about 1672 px wide (`cwebp -q 90 -alpha_q 100`)
   - `bible_icon.webp`, resized to 512×512 first (`sips -Z 512`), then `cwebp -q 90`
   - `nav_bible.webp`, kept at 1200×1200 like the other `nav_*` files (`cwebp -q 90`)
3. Ask Claude to switch the Explore card over. That's a two-line change in
   `ExploreScreen.kt` (the `item(key = "bible")` card): `artwork =
   Res.drawable.explore_bible`, `icon = Res.drawable.bible_icon`, plus their
   two imports. Then rebuild.
4. For the tab: Claude adds `BIBLE` to `Tab` in `ui/navigation/Destination.kt`
   between `EXPLORE` and `PRAYERS`, adds `nav_bible` to the icon list in
   `BottomNavBar.kt`, and adds the Bible destinations to the bar's tab
   tracking, so the Bible tab stays highlighted while reading.
