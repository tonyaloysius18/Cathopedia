# Idols and Sacred Images — image generation brief

One image, for the **Idols and Sacred Images** article (Explore → Catechism →
What We Believe). The second entry reuses `symbol_crucifix.png` from the Sacred
Symbols set, which is exactly the sacred image the page is defending.

| Slot | Asset |
| --- | --- |
| Entry 1 · An idol | `idol_golden_calf.png` |

## Why the golden calf

The source infographic draws the idol as a seated, garlanded figure that reads as
a Buddha. **Do not reproduce that.** A page teaching Catholics what idolatry is
should not illustrate it with the sacred image of a living religion, and the
Church's own texts do not: they reach for the golden calf, Israel's own sin at
the foot of Sinai (Exodus 32). It is the Bible's example, it accuses no one else,
and it is what the article's Scripture list already sets up.

House style: classical academic sacred oil painting, still life, fine detail,
warm golden light from the upper left, reverent and quiet — matching the rest of
the Catechism hub. **No text, no labels, no watermark, no signature.**

## `idol_golden_calf.png`

- **Square 1:1 at 512×512**, transparent PNG with a real alpha channel.
- One object, centred, filling about 75% of the frame; the app crops these to a
  72dp rounded square, so the silhouette must read small.
- No altar, no landscape, no crowd, no shadow plane, no frame.

Classical academic sacred oil painting, still life, warm golden light from the
upper left, transparent background, no scenery. A **cast golden calf standing on
a low stone pedestal** — a young bull of burnished, slightly tarnished gold, seen
from the side with its head turned towards the viewer, standing stiffly, with
incised decoration on its flanks. On the pedestal in front of its hooves sits a
**small shallow bowl with a thin thread of incense smoke** rising and fading.
Heavy, lifeless, opulent, faintly oppressive despite the gold. 1:1.

*Notes:* the calf must look like a **made object, not a living animal** — hard
metal, fixed pose, dead eyes. The smoke is the tell that it is being worshipped,
so keep it visible but thin; let it fade out well before the top edge rather than
running off the frame. No figures, no hands, no fire, no landscape.

**Negative prompt:** `text, letters, words, labels, watermark, signature, frame,
border, background scenery, landscape, desert, mountain, altar steps, people,
hands, worshippers, fire, flames, live animal, cattle in a field, cartoon, flat
vector, low detail, Buddha, Hindu deity, Egyptian god, any deity of a living
religion`

---

## After generating

Drop the PNG into `shared/src/commonMain/composeResources/drawable/` and rebuild.
No content change is needed: `content/hubs/catechism.json` already points at
`hub/catechism/idol_golden_calf.png`, and `hubAssetPainter` resolves drawables by
file name. Until it lands the entry shows a numbered placeholder.
