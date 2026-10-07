# Liturgical Roles — image generation brief

Images for "Altar Server, Acolyte and Sacristan" (`art.mass.roles`, Holy Mass
hub, section 20, "Liturgical Roles"). Each role is shown as a picture above
its own card, the same layout as the Stigmata and Crowned Images pages.

| Role | Asset | Status |
| --- | --- | --- |
| Altar Server | `mass_crossbearer.png` | **reused**, already in the app |
| Instituted Acolyte | `mass_role_acolyte.png` | **done** 2026-10-07 |
| Sacristan | `mass_role_sacristan.png` | **done** 2026-10-07 |

The altar server reuses the existing **cross-bearer** from the Entrance
Procession: a young man in black cassock and lace surplice carrying the
processional cross. The two new figures **must match it** so the three read
as one set. Open `mass_crossbearer.png` beside them before accepting:

- the same warm, classical illustration style, with soft golden light from
  the upper left;
- the same full-length figure, about the same scale in the frame, with feet
  visible;
- the same transparent cut-out, with no floor, room or shadow box.

Each figure must also **look like its role at a glance**, because the
article's point is that the three are easy to confuse:

- the **server** wears cassock and surplice (the existing art);
- the **acolyte** wears a plain **white alb with a cincture**, never a
  cassock and surplice;
- the **sacristan** wears ordinary dark clothing or a plain black cassock,
  never an alb or surplice, and is shown working, not processing.

## Rules for both

- **Transparent background is required**: a PNG with a real alpha channel.
- Square **1:1, 1024×1024**. Full-length figure, centred, filling about
  85% of the frame height.
- An adult layperson, not a priest: **no stole, no chasuble, no dalmatic,
  no clerical collar**.
- **No text, no labels, no watermark, no signature.**
- After generating, check the alpha channel. Earlier cut-outs arrived with
  the transparency checkerboard baked into the edges.

**Negative prompt (both):** `text, letters, words, labels, watermark,
signature, stole, chasuble, dalmatic, clerical collar, priest, bishop,
mitre, background, room, floor, church interior, cartoon, anime, flat
vector, low detail, modern casual clothing`

---

## `mass_role_acolyte.png`: the instituted acolyte

An adult man in a long, plain white alb tied at the waist with a white
cincture, standing in three-quarter view and walking slightly forward. He
carries a gold chalice with a white purificator folded over it, held
reverently at chest height with both hands. This is his distinctive task:
preparing the altar and the sacred vessels. He is calm and attentive.

```
Create an original illustration in a warm classical devotional style,
matching a companion image of a young altar server in cassock and lace
surplice: an adult layman serving as an instituted acolyte, full-length,
three-quarter view, walking slightly forward, on a plain transparent
background. He wears a long plain white alb reaching his ankles, tied at
the waist with a white rope cincture with tassels, and dark shoes. He holds
a polished gold chalice with a folded white purificator draped over it,
carried reverently at chest height in both hands. Short dark hair, calm
and attentive expression. No stole, no chasuble, no clerical collar. Soft
golden light from the upper left, fine detail in the folds of the alb.
No floor, no room, no shadow, no text, no labels, no watermark, no
signature. Export with a transparent alpha channel. Square 1:1.
```

## `mass_role_sacristan.png`: the sacristan

A man in a plain black cassock (or dark, modest everyday clothes), standing
and turned slightly to the side, laying out a folded green chasuble over
his forearm while holding a pair of brass altar cruets in his other hand.
He is clearly preparing for Mass, not taking part in a procession. It is a
quiet, careful, workmanlike pose.

```
Create an original illustration in a warm classical devotional style,
matching a companion image of a young altar server in cassock and lace
surplice: a parish sacristan, an adult layman, full-length, standing in
three-quarter view on a plain transparent background. He wears a plain
black cassock with no surplice and no collar. Over his left forearm a
neatly folded green chasuble with gold embroidery hangs ready; in his
right hand he holds a pair of small glass-and-brass altar cruets for water
and wine. He looks down at his work with a calm, careful, attentive
expression, clearly preparing things for Mass. Short greying hair. Soft
golden light from the upper left, fine detail in the fabric and brass. No
alb, no surplice, no stole, no room, no cupboard, no table, no floor, no
text, no labels, no watermark, no signature. Export with a transparent
alpha channel. Square 1:1.
```

---

## After generating

Drop both PNGs into `docs/image-prompts/mass/` and say so. They will be
converted to WebP in `shared/src/commonMain/composeResources/drawable/`. The
content already references them (`content/hubs/mass.json`, article
`art.mass.roles`). Until then the acolyte and sacristan cards show their
text without a picture.
