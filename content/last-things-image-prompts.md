# The Last Things — image generation brief

Four images for **The Last Things** (Explore → Catechism → What We Believe →
The Last Things). Nothing in the existing symbol set fits these, so all four are
new. The page works without them — the cards simply show no picture.

House style: classical academic sacred oil painting, fine detail, rich earthy
palette, reverent and still. **No text, no labels, no watermark, no signature.**

These are the gravest subjects in the app. Keep every one of them **sober and
doctrinal, never lurid or sentimental** — the tone of a devotional holy card, not
of horror art or fantasy illustration.

## Rules for all four

- Square **1:1, 1024×1024**.
- **Transparent background not required here** — unlike the symbol set, these are
  small scenes and are drawn inside a rounded 72dp tile, cropped to fill. Compose
  each so the **centre of the square carries the subject**; the corners may be
  trimmed.
- **No text of any kind**, including on books, scrolls or signs.
- Faces must be anatomically clean. No visible distress that reads as gore.

**Negative prompt (all four):** `text, letters, words, labels, watermark,
signature, frame, border, cartoon, flat vector, low detail, distorted faces,
extra limbs, gore, blood, demons, monsters, skulls, horror, grotesque`

---

## `cat_destiny_earth.png` — Earth, our earthly life

Classical academic sacred oil painting, warm daylight — a **young pilgrim seen
from behind, kneeling on a grassy hillside path**, hands joined in prayer, a
pack on his back. Before him the path winds down a green valley toward a distant
church on the far hill, under a wide sky at golden hour. A **wooden cross stands
at the roadside** partway along the path. Hopeful, open, full of air. The time of
trial and choice. 1:1.

## `cat_destiny_purgatory.png` — Purgatory, final purification

Classical academic sacred oil painting, warm amber light — **souls in prayerful
attitudes, faces lifted upward with longing and hope**, rising through cleansing
golden flame toward a radiance above. The fire is **luminous and refining, not
punishing**: gold and warm orange, no charring, no agony, no chains. Faces calm
and yearning rather than tormented. A few figures, half-length, close together.
These souls are already assured of heaven — the image must look like hope, not
like hell. 1:1.

## `cat_destiny_heaven.png` — Heaven, eternal communion with God

Classical academic sacred oil painting, luminous white and gold — **Christ seated
in glory amid soft cloud and radiant light**, a hand extended in welcome, with
haloed saints and angels indistinct in the brightness behind him, and a golden
city faint on the horizon. Serene, spacious, filled with light. Supreme and
definitive happiness. 1:1.

## `cat_destiny_hell.png` — Hell, eternal separation from God

Classical academic sacred oil painting, dark with deep red light — **a single
solitary figure seated on bare rock, seen at a distance and in silhouette, head
bowed into their hands, turned away from a faint light behind them**, surrounded
by dim red glow and shadow. The subject is **isolation and loss, not torture**:
no devils, no monsters, no chains, no visible suffering of the body, no faces.
Restrained and sorrowful — the definitive self-exclusion from communion with God,
freely chosen. 1:1.

---

## After generating

Drop all four into `shared/src/commonMain/composeResources/drawable/` under those
exact names and rebuild. The asset paths are already in
`content/hubs/catechism.json`; no content edit and no `CONTENT_VERSION` bump is
needed.
