# The Three Holy Oils — image generation brief

Three images for the new "The Three Holy Oils" section (Holy Mass hub,
section 15), one per oil covered in the article:

1. **mass_oil_catechumens** — the Oil of the Catechumens.
2. **mass_oil_chrism** — Sacred Chrism.
3. **mass_oil_sick** — the Oil of the Sick.

Same house style as the rest of the Mass hub's sacred-object images
(`mass_monstrance`, `mass_thurible`, the five `mass_bell_*` images): classical
academic sacred-object rendering, warm golden light, rich earthy palette,
reverent and still. **No text, no labels, no lettering of any kind, no
watermark, no signature** — unlike the source infographic, which prints "OC" /
"SC" / "OI" on the bottles, these must carry no letters at all, since the app
supplies every name and description as text and image generators garble
lettering. The three are told apart by their vessel and setting alone, not by
any printed tag.

## Rules for every image

- **Transparent background is required** — export a PNG with a real alpha
  channel. No church interior, no floor, no altar, no frame: just the vessel
  (with a small sprig of olive leaves if it helps read as "holy oil", as in
  the reference), isolated on transparency, so it drops onto the app's own
  surface.
- **One subject per image.** Never a row, grid, or composite infographic —
  each oil gets its own image.
- Square **1:1, 1024×1024**.
- Materials: a glass cruet/ampulla of golden oil, with warm brass or silver
  fittings — matching the jewel-like, reverent finish of the existing
  monstrance, thurible and bell images.
- **No text anywhere on the bottle or label** — a plain, blank glass or metal
  tag is fine; a lettered one is not.

---

## 1. mass_oil_catechumens — Oil of the Catechumens

A simple glass cruet of golden oil with a plain stopper, a small blank oval
tag (no lettering) hanging from its neck, and a sprig of olive leaves
resting against it.

```
Create an original illustration: a single glass cruet of golden olive oil,
centered and upright, on a plain transparent background. A rounded glass
bottle with a simple silver or pewter stopper and handle, a small plain
blank oval tag on a cord around its neck (no lettering, no text at all on
the tag), and a sprig of olive leaves and a couple of olives resting against
its base. Warm golden light catching the oil inside, soft reverent lighting,
fine detail, classical sacred-object rendering style. No exploded parts, no
labels, no text, no watermark, no signature, no background at all — export
with a transparent alpha channel. Square 1:1.
```

---

## 2. mass_oil_chrism — Sacred Chrism

A more richly ornamented glass cruet of golden oil, with a gilded,
cross-topped stopper, and a small dish of fragrant balsam resin beside it.

```
Create an original illustration: a single glass cruet of golden chrism oil,
centered and upright, on a plain transparent background. A rounded glass
bottle with an ornate gilded stopper topped by a small cross, a small plain
blank oval tag on a cord around its neck (no lettering, no text at all on
the tag), with sprigs of white balsam blossom and a small dish of amber
balsam resin beads resting beside it. Warm golden light catching the oil
inside, soft reverent lighting, fine detail, classical sacred-object
rendering style, slightly richer and more ornate than a plain oil cruet. No
exploded parts, no labels, no text, no watermark, no signature, no
background at all — export with a transparent alpha channel. Square 1:1.
```

---

## 3. mass_oil_sick — Oil of the Sick

A simple, unadorned glass cruet of golden oil, plainer than the other two,
evoking quiet comfort rather than ceremony.

```
Create an original illustration: a single glass cruet of golden olive oil,
centered and upright, on a plain transparent background. A simple rounded
glass bottle with a plain silver stopper and handle, a small plain blank
oval tag on a cord around its neck (no lettering, no text at all on the
tag), and a small sprig of olive leaves resting against its base — plainer
and quieter than a chrism vessel, evoking comfort rather than ceremony.
Warm golden light catching the oil inside, soft reverent lighting, fine
detail, classical sacred-object rendering style. No exploded parts, no
labels, no text, no watermark, no signature, no background at all — export
with a transparent alpha channel. Square 1:1.
```

---

## Deliverable

3 **transparent PNG** files, square 1:1 (1024×1024), named exactly:
`mass_oil_catechumens.png`, `mass_oil_chrism.png`, `mass_oil_sick.png`. Hand
them back as a single batch, then drop them into
`docs/image-prompts/mass/` (or send them in chat) and tell me — I'll
convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` and the content already
references them (`content/hubs/mass.json`, section `mass.holy_oils`,
`CONTENT_VERSION` already bumped).
