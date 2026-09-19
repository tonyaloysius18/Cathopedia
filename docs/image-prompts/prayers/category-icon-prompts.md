# Prayer category icon prompts

Reference: the existing medallion icons already in
`shared/src/commonMain/composeResources/drawable/` —
`saints_icon.png`, `churches_shrines_icon.png`, `marian_apparitions_icon.png`,
`eucharistic_miracles_icon.png`, `liturgical_feasts_icon.png`, `popes_icon.png`,
`apostles_icon.png`, and the bottom-bar icons `nav_explore.png` / `nav_search.png`
/ `nav_settings.png`. All of these share one house icon language:

> A circular gold-rimmed medallion/coin. Deep emerald-green enamel disc fills
> the face. A single polished, photorealistic 3D gold-metal emblem sits
> centered on the disc, with fine engraved highlights, soft specular
> reflections, and a subtle bevel/drop-shadow giving it real metallic depth
> (not a flat vector icon). Transparent background outside the coin. No text,
> no watermark, no signature, no extra ornamentation beyond the rim and the
> single central emblem.

Palette to match exactly (from `ui/theme/Color.kt`):
- Gold: `#D4AF37` (rim/emblem base) with brighter highlight gold `#DDBF5F`
- Enamel disc: deep green, matching `#0B1A15`–`#1F4034` (the app's dark
  green background range) — same tone as the existing coins above, not a
  brighter/different green.

**Format**: square, 1200×1200px, transparent PNG background (matches
`nav_explore.png`/`nav_search.png`/`nav_settings.png`'s actual resolution —
the smaller category coins like `saints_icon.png` were baked around 350–400px
but 1200px scales down cleanly, so generate large).

Each prompt below is the full house-style clause plus the one emblem clause
specific to that category — keep every other element (rim, disc, lighting,
transparency) identical across all of them so the set reads as one family.

---

## The 8 prayer categories (`PrayerCategory`)

- **everyday** — Emblem: a pair of hands clasped together in prayer, fingers
  interlaced, rendered as polished gold metal, angled slightly upward, resting
  just above the disc's center.
- **marian** — Emblem: a single rosary loop with a small crucifix pendant
  hanging from the bottom of the loop, gold beads and chain, the crucifix
  slightly larger and more detailed than the beads.
- **holy-spirit** — Emblem: a dove with wings spread wide in descent, head
  angled slightly downward, a few fine rays of light engraved radiating from
  behind it toward the rim.
- **eucharistic** — Emblem: a crossed sheaf of wheat and a grape vine with one
  small cluster of grapes, tied at the center where they cross, gold metal
  with fine engraved grain lines and grape texture.
- **saints** — Emblem: a single tall votive candle with a lit flame, a faint
  halo-like glow engraved around the flame's tip.
- **penitential** — Emblem: a plain wooden-textured gold cross with a woven
  crown of thorns draped diagonally across the crossbar.
- **sequences** — Emblem: a rosary chain coiled into a tight spiral (like a
  coiled rope of beads viewed from above), with a small plain cross at the
  spiral's center.
- **occasional** — Emblem: a ship's anchor with a thin vine wrapped around its
  shaft, gold metal, classic anchor-of-hope silhouette.

---

## Bottom navigation bar — Prayers tab icon

This replaces the current placeholder vector at
`shared/src/commonMain/composeResources/drawable/nav_prayers.xml` (a plain
gold rosary-loop line drawing standing in until real baked art exists) with
one that matches the finished raster style of `nav_home` / `nav_explore.png`
/ `nav_search.png` / `nav_settings.png`.

- **nav_prayers** — Same house medallion style, coin format, 1200×1200px
  transparent PNG. Emblem: a rosary looped into a circle running just inside
  the coin's rim, with a small crucifix pendant hanging down from the bottom
  of the loop into the center of the disc — polished 3D gold metal, same
  weight and finish as the compass rose in `nav_explore.png` and the
  magnifying glass in `nav_search.png`.

---

## Once you have the images

Save each as `prayer_category_<slug>.png` (slug = the 8 category ids above,
e.g. `prayer_category_everyday.png`, `prayer_category_holy-spirit.png` →
`prayer_category_holy_spirit.png` since filenames can't have hyphens the same
way — I'll normalize whatever you send back), and the nav icon as
`nav_prayers.png` (same basename as the placeholder it replaces, just a raster
instead of the vector). Hand them back in a batch and I'll:

1. Drop them into `shared/src/commonMain/composeResources/drawable/`.
2. Wire the 8 category icons into the Prayers home screen's category chips/
   sections (wherever `PrayerCategory` is currently rendered).
3. Replace `nav_prayers.xml` with the new `nav_prayers.png` in the bottom bar,
   matching how `nav_explore.png` etc. are already referenced.
