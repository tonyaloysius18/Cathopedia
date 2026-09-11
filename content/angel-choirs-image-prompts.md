# The Nine Choirs of Angels — image generation brief

Nine images for **The Nine Choirs of Angels** (Explore → Catechism → What We
Believe → The Nine Choirs of Angels). Nothing in the `symbol_*` set fits — those
are emblems, and these need figures — so all nine are new. Until they land each
card shows its number instead.

House style: classical academic sacred oil painting, fine detail, warm golden
light, reverent and still. **No text, no labels, no watermark, no signature.**

## Rules for all nine

- Square **1:1, 1024×1024**.
- **Transparent background not required** — these are small scenes drawn inside a
  rounded 72dp tile and cropped to fill, so **keep the subject in the centre of
  the square**; the corners may be trimmed.
- **Read as one set.** They are seen in a single column one after another, so
  hold the scale, palette and light consistent: each is a **single angelic figure
  (or pair) seen half-length to three-quarter length, centred, against sky,
  cloud or radiance**, never a full landscape.
- Faces and hands must be anatomically clean. Wings are feathered and plausible.
- No haloed figures of Christ, Mary or named saints — these are angels.

**Negative prompt (all nine):** `text, letters, words, labels, watermark,
signature, frame, border, cartoon, flat vector, low detail, distorted faces,
extra limbs, extra fingers, deformed wings, horror, grotesque, modern clothing`

---

## First hierarchy — closest to God

**1 · `cat_angel_seraphim.png`** — A **seraph wreathed in fire**, with **six
wings** — two raised above, two spread, two folded below — the face serene at the
centre of blazing red and gold plumage, radiant light behind. Burning love and
adoration.

**2 · `cat_angel_cherubim.png`** — A **cherub of deep blue and gold with many
eyes set among its wings**, hands crossed over the breast, gazing steadily
forward, set against luminous cloud. Knowledge and contemplation — solemn and
still, **not a baby or a putto**.

**3 · `cat_angel_thrones.png`** — A **great fiery wheel rimmed with eyes**,
turning within a burst of golden light, flanked by pale wings. Majesty and
judgement — an emblematic figure rather than a human one.

## Second hierarchy — the governance of creation

**4 · `cat_angel_dominions.png`** — A **crowned angel in blue and gold holding an
orb and a slender sceptre**, calm and commanding, wings spread behind. Authority
and ordering.

**5 · `cat_angel_virtues.png`** — An **angel in white and blue with light
streaming from its cupped hands**, head inclined, wings lifted. Divine strength
and the working of God's power.

**6 · `cat_angel_powers.png`** — An **armoured angel with a sword and shield**,
standing watchful against dark cloud edged with gold, wings raised. Protection
against evil — vigilant and composed, **no visible enemy, no combat, no demons**.

## Third hierarchy — ministers of God's providence

**7 · `cat_angel_principalities.png`** — An **angel in gold and white holding an
open book and a small crown**, a distant walled city faint below. The governance
of peoples and communities.

**8 · `cat_angel_archangels.png`** — A **warrior archangel in red and blue with a
raised sword and a shield**, wings wide, in the manner of St Michael. Messages
and missions of particular importance. **No dragon or defeated figure beneath.**

**9 · `cat_angel_angels.png`** — A **gentle angel in white bending toward a
kneeling person**, one hand extended in blessing or guidance. The choir nearest
to us — warm and protective.

---

## After generating

Drop all nine into `shared/src/commonMain/composeResources/drawable/` under those
exact names and rebuild. The asset paths are already in
`content/hubs/catechism.json`; no content edit and no `CONTENT_VERSION` bump is
needed.
