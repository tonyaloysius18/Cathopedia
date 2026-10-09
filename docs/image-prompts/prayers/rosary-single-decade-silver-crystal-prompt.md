# Silver-chain Marian rosary with crystal-blue crucifix

Created with the built-in image generator in edit mode, using the previous ten-bead centered oval as the edit target.

## Exact prompt

```text
Use case: precise-object-edit.
Asset type: transparent photographic rosary artwork for Cathopedia.
Input image: edit target, the supplied centered upright single-decade Marian rosary.
Change only the chain metal finish and the crucifix's blue inset material.

1. Replace every GOLD CHAIN LINK and connecting jump ring throughout the oval and pendant with polished STERLING SILVER. Bright neutral silver reflections, clean white highlights and darker metallic recesses. Preserve each link's shape, size, spacing and physical connections. The caps on the single blue pendant bead should also have a matching silver finish. Preserve the ornate Marian medal itself, its portrait, blue field and existing frame design.
2. Replace the flat blue enamel within the crucifix with SHINING BLUE CRYSTAL inlays. Keep the exact same cross silhouette, silver rim, silver corpus of Jesus and INRI plaque. The blue cross panels should look like real transparent sapphire-blue cut crystal with beveled facets, depth, subtle internal refraction, rich cobalt-blue shadows, lighter crystal-blue facets and crisp realistic white specular reflections from studio lights. Luxurious luminous crystal material, visually distinct from the current opaque enamel. Highlights must stay on the crystal surfaces; no surrounding glow, artificial starbursts or floating sparkles.

INVARIANTS: retain the existing upright centered oval and complete product framing. Exactly TEN white porcelain floral beads in the loop: two at the top, four down the left, four down the right. Exactly ONE faceted blue pendant bead between the Marian medal and crucifix. Preserve all bead sizes, floral patterns, color, placement, the medal, the straight centered pendant and the new crucifix shape. Do not add, remove, duplicate or rearrange any element.
Preserve a genuinely transparent alpha background around the object, inside the oval and between chain links. No colored or black backdrop, no checkerboard pixels, no cast shadow or halo. Clean cutout edges, full object visible without cropping. No text or watermark. Photorealistic jewelry detail with soft studio lighting.
```

Input generation: `/Users/tonyaloysius/.codex/generated_images/01a11df4-be64-7a01-a28b-89d1a0f656b2/exec-5dcc9d14-36eb-4e37-ba99-86939aef6071.png`.

Final generation: `/Users/tonyaloysius/.codex/generated_images/01a11df4-be64-7a01-a28b-89d1a0f656b2/exec-9f7f56b6-98ea-413e-b82f-a1390d6ab973.png`.

Final saved asset: `shared/src/commonMain/composeResources/drawable/rosary_landing_single_decade_marian.png`.

Visual verification: all chain links and pendant bead caps are silver; the crucifix's blue panels have faceted crystal reflections. Ten floral beads remain in the oval, and exactly one blue bead remains between the Marian medal and crucifix. The upright shape, silver corpus and complete product framing are retained. The generated alpha channel is preserved.

Integration verification: Android debug build and iOS simulator Kotlin compilation passed. The new build was installed and its Rosary landing screen checked on the Android emulator, with French language and dark appearance preserved. Silver links, blue crystal facets and transparent cutout edges render cleanly. Native preview: `design-concepts/holy-rosary/implemented-v2/landing-silver-crystal-rosary.png`.

The build exposed an unresolved language reference in `PrayerReadingContent`; the existing app language is now forwarded from `PrayerDetailScreen` to that helper. This preserves its intended choice of the app language, English and Latin.
