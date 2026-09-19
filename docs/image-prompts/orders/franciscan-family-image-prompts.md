# The Franciscan Family: image generation brief

**Status: done (2026-09-19).** All five images are in the app. Masters are in
`content/hub/orders/`, and they ship as 1024×1024 WebP with alpha in
`shared/src/commonMain/composeResources/drawable/`. This brief is kept for
reference in case any image is regenerated.

Five images for **The Franciscan Family** (Explore → Religious Orders → The
Franciscan Family). The article already points at these paths, so each image
appears as soon as its file exists. Until then the page just skips it.

| Slot | Asset | Status |
| --- | --- | --- |
| Opening image | `symbol_franciscan_arms.png` | ✅ reused (the Franciscan arms) |
| 1 · Friars Minor (OFM) | `franciscan_ofm.png` | ⬅ generate |
| 2 · Conventuals (OFM Conv.) | `franciscan_conventual.png` | ⬅ generate |
| 3 · Capuchins (OFM Cap.) | `franciscan_capuchin.png` | ⬅ generate |
| 4 · Poor Clares (OSC) | `order_poor_clares_founder.png` | ✅ reused (St Clare) |
| 5 · Third Order Regular (TOR) | `franciscan_tor.png` | ⬅ generate |
| 6 · Secular Franciscans (OFS) | `franciscan_secular.png` | ⬅ generate |

**Why new art, when the orders hub already has Franciscan portraits.**
`order_franciscans_founder`, `order_capuchins_founder` and
`order_conventual_franciscans_founder` all show **St Francis himself**, which is
right for their founder cards. This page is about how the branches **differ**, and
what tells them apart at a glance is the habit. So each of the five new images is
an **ordinary member of that branch**, not a saint: **no halo, no stigmata, no
birds**. Otherwise the reader sees Francis three times over.

## Where the art departs from the source infographic

- The source titles these "Franciscan monks". Friars are not monks (the article
  explains the difference), so the figures appear on the road or at work, never in a cloister.
- The Conventual in the source is in grey. The article says their habit is
  **black or grey**. The art uses **black**, so it can't be mistaken for the OFM
  brown at thumbnail size.

## House style (all five)

Match the existing `order_*_founder` portraits:

> Classical academic sacred oil painting, **half-length**, warm golden light from
> the upper left, rich earthy palette, fine brushwork, reverent and still.
> **Transparent background**, no scenery, no frame.

- **512 × 512 (or 1024 × 1024), transparent PNG with a real alpha channel.**
  The article shows these as square tiles at about half the screen width, so the
  figure should fill the frame from the waist up, with a little headroom.
- **Generate them as a matched set:** the same light, scale, painterly finish
  and camera height. Faces are dignified and quietly joyful, not stern.
- **The habit is the subject.** Paint the cut, colour, cord and hood
  accurately. They are the only way to tell the branches apart.
- **The white cord** of the three First Order friars and the TOR friar has **three
  knots** hanging at the side, for poverty, chastity and obedience. Make them visible.
- **No text, no labels, no watermark, no signature.**

**Negative prompt (all five):** `halo, nimbus, stigmata, wounds, birds, animals,
text, labels, watermark, signature, frame, background scenery, monastery,
cloister, modern clothing on the friars, cartoon, low detail, extra limbs,
distorted hands`

---

## 1 · `franciscan_ofm.png`: a Friar Minor

Classical academic sacred oil painting, half-length, warm golden light,
transparent background, no scenery. A **Franciscan friar of the Order of Friars
Minor** in his thirties, clean-shaven or with a short beard, in a **plain
brown wool habit** with a **short rounded hood** lying on the shoulders and a
**white cord with three knots** at the waist. He holds a small wooden **tau cross** on a
cord in one hand and a worn book of the Gospels in the other. Warm, open
expression, as if about to speak. 1:1.

## 2 · `franciscan_conventual.png`: a Conventual friar

Classical academic sacred oil painting, half-length, warm golden light,
transparent background, no scenery. A **Conventual Franciscan friar** in his
forties in a **black wool habit** with a **short rounded hood**, a **short black
shoulder cape** over it, and a **white cord with three knots**. Clean-shaven,
short dark hair. He holds a leather-bound book against his chest, and a rosary
hangs from the cord. Scholarly, calm and pastoral. 1:1.

*Notes:* the habit must read as **black, not brown**. It is the only thing that
separates this card from the OFM one. Light it enough that the folds show.

## 3 · `franciscan_capuchin.png`: a Capuchin friar

Classical academic sacred oil painting, half-length, warm golden light,
transparent background, no scenery. An older **Capuchin friar** with a **long,
full grey beard**, in a **coarse brown habit** whose **long pointed hood**, the
cappuccio, is sewn to the habit and raised over his head, its point clearly
visible. A **white cord with three knots** hangs at the waist. He wears a
simple wooden rosary and holds a plain wooden cross. Weathered, kind and humble. 1:1.

*Notes:* the **pointed hood and the beard** are the whole identification. The
hood must be long and pointed, not a rounded cowl, and should frame the face.

## 5 · `franciscan_tor.png`: a friar of the Third Order Regular

Classical academic sacred oil painting, half-length, warm golden light,
transparent background, no scenery. A **friar of the Third Order Regular** in his
thirties in a **dark brown habit** with a **rounded hood** and a **white cord with
three knots**. Over his shoulders he wears a narrow **priest's stole** in green,
hanging loose, for a pastor at work. He holds an open breviary in one hand, the
other raised slightly in greeting. Approachable, a parish priest among his
people. 1:1.

*Notes:* the stole is what tells this card apart from the OFM friar, who is shown with
the tau cross and Gospels. Keep the stole plain, with no embroidered text.

## 6 · `franciscan_secular.png`: Secular Franciscans

Classical academic sacred oil painting, half-length, warm golden light,
transparent background, no scenery. **A married lay couple**, a man and a woman
in their thirties, side by side, in **simple, timeless everyday clothes** in
earthy browns, creams and muted greens. There are no habits and no veils as religious
dress. Each wears a **small wooden tau cross on a cord** around the neck, the
Secular Franciscan sign. She holds a small book of prayers; his hand rests on
her shoulder. Warm, ordinary and devout, a family at prayer rather than a
portrait of saints. 1:1.

*Notes:* it must read at a glance as **lay people**, not a friar and a nun.
Avoid anything that looks like a habit, cowl or veil. The tau crosses are the
only religious sign.

---

## After generating

1. Keep the masters in `content/hub/orders/` if you want them versioned.
2. Ship them as **WebP (q90, alpha preserved)** with the same base names in
   `shared/src/commonMain/composeResources/drawable/`.
3. Rebuild. No content change is needed: `content/hubs/orders.json` already
   points at `hub/orders/franciscan_*.png`, and `hubAssetPainter` resolves
   drawables by file name.
