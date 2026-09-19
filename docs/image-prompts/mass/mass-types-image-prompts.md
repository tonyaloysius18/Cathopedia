# Types of Mass — image generation brief

Two images for the **Types of Mass** section (Explore → The Holy Mass → Types of
Mass). Everything else on that page reuses existing Sacred Symbols art.

| Slot | Asset | Replaces |
| --- | --- | --- |
| Page hero | `mass_celebrant_altar.png` | the walking `mass_priest.png` |
| Entry 11 · The Christmas Masses | `symbol_christmas_nativity.png` | `symbol_ihs.png` |

House style: classical academic sacred oil painting, fine detail, warm golden
light from the upper left, rich earthy palette, reverent and still — matching the
Holy Mass hub's existing art. **No text, no labels, no watermark, no signature.**

## Rules for both

- **Square 1:1.** Hero at **1024×1024**; the Christmas symbol at **512×512** to
  match the other `symbol_*` files.
- **Transparent background is required** — a PNG with a real alpha channel. No
  church interior, no walls, no floor, no frame.
- Keep the subject centred with a little margin on all sides.

**Negative prompt (both):** `text, letters, words, labels, watermark, signature,
frame, border, background scenery, church interior, walls, floor, pews, sky,
cartoon, flat vector, low detail, distorted hands, extra fingers`

---

## `mass_celebrant_altar.png` — the page hero

Replaces the current hero, which is the sixth walking figure of the Entrance
Procession and reads as someone on his way to Mass rather than offering it.
**Do not overwrite `mass_priest.png`** — the procession still needs it.

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery — a **Catholic priest celebrating Mass at the altar, seen
from the front**. He stands behind a stone altar draped in a white linen cloth,
vested in an **alb and a green chasuble**, his **arms raised and open in the
orans posture**, head slightly bowed, eyes lowered in prayer. On the altar before
him: a **gold chalice, a paten with a host, an open missal**, and a **lit candle
at either end**. The altar runs across the lower third of the frame; the priest
fills the upper two thirds. Reverent, still, the moment of the Eucharistic
Prayer. 1:1.

*Notes:* the whole group — priest and altar together — is the subject and sits on
transparency. Face and hands must be anatomically clean; hands are raised and
open, palms inward, not joined and not clasped.

## `symbol_christmas_nativity.png` — entry 11

The current stand-in is `symbol_ihs`, the monogram of the Holy Name, which stands
for Christ rather than for Christmas. This replaces it with something proper to
the Nativity, rendered to match the rest of the `symbol_*` set: **a single gilded
emblem, centred, upright, on transparency**, warm gold with fine engraving and a
soft sheen, jewel-like, with a generous margin.

Gilded sacred emblem, warm gold with fine engraving, transparent background,
centred, no scene — a **Nativity star above a simple manger**: an eight-pointed
star with a long tapering ray descending, and beneath it a **plain wooden crib of
crossed beams holding swaddling straw**, the straw catching the light. Symbolic
and compact rather than a full Nativity scene — no figures, no stable, no
animals. Jewel-like and reverent. 1:1.

---

## After generating

Drop both PNGs into `shared/src/commonMain/composeResources/drawable/` and
rebuild. The hero path is already set in `MassTypesScreen.kt`. For the Christmas
symbol, also change entry 11's asset in `content/hubs/mass.json` from
`hub/symbols/symbol_ihs.png` to `hub/symbols/symbol_christmas_nativity.png`, and
bump `CONTENT_VERSION` in `ContentLoader.kt`.
