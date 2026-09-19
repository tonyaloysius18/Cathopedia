# Catholic Scapulars: image generation brief

**Status: done (2026-09-14).** All fourteen faces were generated and are in the
app. Masters are in `content/hub/symbols/`, and they ship as 1024×1024 WebP
(alpha preserved) in `shared/src/commonMain/composeResources/drawable/`. This
brief is kept for reference in case any face is regenerated.

Fourteen images for **Catholic Scapulars** (Explore → Sacred Symbols →
Scapulars). The page uses the same two-up card as Catholic Medals: each scapular
is one card with its **front** and **back** side by side. The article already
points at these paths, so each face appears as soon as its file exists. Until
then the slot shows an empty frame.

| Scapular | Front | Back |
| --- | --- | --- |
| 1 · Brown (Our Lady of Mount Carmel) | `scapular_brown_front.png` | `scapular_brown_back.png` |
| 2 · White (Most Holy Trinity) | `scapular_white_front.png` | `scapular_white_back.png` |
| 3 · Red (Passion) | `scapular_red_front.png` | `scapular_red_back.png` |
| 4 · Blue (Immaculate Conception) | `scapular_blue_front.png` | `scapular_blue_back.png` |
| 5 · Black (Seven Sorrows) | `scapular_black_front.png` | `scapular_black_back.png` |
| 6 · Green (Immaculate Heart) | `scapular_green_front.png` | `scapular_green_back.png` |
| 7 · Passionist | `scapular_passionist_front.png` | `scapular_passionist_back.png` |

None of the existing art can stand in. `symbol_carmelite_shield`,
`order_trinitarians_emblem` and `order_passionists_emblem` are the right
**emblems**, but they are flat heraldic art and the cards need **cloth**. Use
them as design references for the brown back, white front and Passionist front,
so the emblems agree across the app.

## Where the art departs from the source infographic

These changes are deliberate. The text on the page already follows them.

- **White:** the source shows a triquetra and an IHS. The real scapular of the
  Most Holy Trinity bears the Trinitarian **red-and-blue cross**.
- **Red:** the source shows an Ecce Homo head and a lone Sacred Heart. The
  approved design (1847) has **Christ crucified with the instruments of the
  Passion** on one side and the **two Hearts of Jesus and Mary** on the other.
- **Blue back:** the source repeats the Marian monogram, which is also the black
  scapular's back. It is briefed here as the **twelve stars and moon** of
  Revelation 12, so the two cards don't look the same.
- **Black front:** the source shows Our Lady with no swords. The Seven Sorrows
  are the whole point, so she gets **seven swords**.
- **Green:** the source draws two joined panels. The Green Scapular is really **one
  piece of cloth on a single cord**, with a picture on each side. Its "front"
  and "back" are the two sides of that one piece.

## House style (all fourteen)

> A single small **devotional scapular panel of felted wool**, seen face-on,
> upright and centred. The panel is a rectangle about 3:4 with softly rounded
> corners and a neat blanket-stitched edge. A **cream cloth oval or rectangle is
> sewn onto its centre**, printed or embroidered with the image in rich,
> reverent colour, like a traditional holy card. Two short lengths of **cord in
> the same colour as the wool** rise from the top corners and are cut off just
> above the frame. Visible wool texture and stitching. Soft studio light from the
> upper left, gentle depth. Handmade and devotional, not a product photo.

- **1024 × 1024, transparent background (PNG with alpha).** The panel should
  fill about 80% of the height. No backdrop, no table, no cast shadow, no hand,
  no neck or body.
- **Front and back of the same scapular must match:** the same wool colour,
  cord, stitching, size and light. They sit side by side on one card and must
  read as one object.
- **No text, no letters, no numerals, no watermark**, except where an entry
  below says the letters *are* the subject.
- The card shows each face at about 150dp. Keep the central image **bold and
  simple**. Fine detail at the edges will be lost.

**Negative prompt (all):** `text, words, letters, caption, watermark, signature,
background scene, table, shadow plane, hands, person wearing it, necklace chain,
metal medal, photoreal product shot, plastic, cartoon, flat vector, blurry,
duplicated panels, two panels in one image`

---

## 1 · Brown Scapular: Our Lady of Mount Carmel

**`scapular_brown_front.png`**: dark chocolate-brown wool, brown cords. On the
cream patch: **Our Lady of Mount Carmel**, crowned, in a brown Carmelite habit
with a cream mantle, holding the **Child Jesus** on her left arm. Both of them
hold out a small brown scapular. Warm, tender, gold halos.

**`scapular_brown_back.png`**: same brown wool and cords. On the cream patch:
the **Carmelite shield**, a pointed brown mount rising into a peak with a small
cross on the summit, one six-pointed star on the mount and two in the upper
field, under a gold crown. Match `symbol_carmelite_shield` for the heraldry.

## 2 · White Scapular: Most Holy Trinity

**`scapular_white_front.png`**: undyed **white wool**, white cords. No cream
patch here: the design is stitched straight onto the white cloth. A bold **cross
with a red upright and a blue crossbar**, the Trinitarian cross, centred and
filling about half the panel. Plain, clean, striking. Match the cross in
`order_trinitarians_emblem`.

**`scapular_white_back.png`**: same white wool and cords. A **small red-and-blue
cross** centred in a plain white field, with a thin single-line stitched border
round the panel. Deliberately quiet.

*Notes:* keep the white slightly warm (ivory), not pure #FFFFFF. It must stay
visible on the app's dark card surface, and the stitched edge should give it a
clear outline.

## 3 · Red Scapular of the Passion

**`scapular_red_front.png`**: deep crimson wool, red cords. On the cream patch:
**Christ crucified**, head bowed. At the foot of the cross lie the **instruments
of the Passion**: the hammer, the pincers, three nails, the lance and the sponge
on a reed. Sorrowful, not graphic, with no visible blood beyond a trace at the
wounds.

**`scapular_red_back.png`**: same crimson wool and cords. On the cream patch:
**two hearts side by side beneath a small cross**. On the left is the **Sacred
Heart of Jesus**, ringed with thorns and topped by a flame. On the right is the
**Immaculate Heart of Mary**, pierced by a sword and wreathed in small white
roses.

## 4 · Blue Scapular: Immaculate Conception

**`scapular_blue_front.png`**: sky-to-royal **blue wool**, blue cords. On the
cream patch: **Our Lady of the Immaculate Conception**, standing, in a white robe
and blue mantle, **hands joined in prayer**, eyes lowered. There is a gentle
radiance behind her and a crescent moon at her feet.

**`scapular_blue_back.png`**: same blue wool and cords. On the cream patch: a
**circle of twelve gold six-pointed stars** above a slim **silver crescent moon**,
set on a soft blue field.

## 5 · Black Scapular of the Seven Sorrows

**`scapular_black_front.png`**: **black wool**, black cords. On the cream patch:
**Our Lady of Sorrows**, veiled in black over a deep blue robe, her face grieving
but serene. **Seven silver swords pierce the heart** on her breast, fanned three
and four. There are no tears of blood.

**`scapular_black_back.png`**: same black wool and cords. No patch: the design
is **embroidered in silver-white thread** straight onto the black cloth. The
**crowned monogram of Mary**, an interlaced **A** and **M** under a small crown.

*Notes:* the A and M are the subject, so letters are allowed here, but only
those two, interlaced. Keep the black as a very dark charcoal with visible weave,
so it shows up against the app's dark card.

## 6 · Green Scapular: Immaculate Heart of Mary

This is **one piece of cloth on a single cord**. On both faces, draw **one cord
looping up from the centre of the top edge**, not two corner cords.

**`scapular_green_front.png`**: **emerald-green wool**. On the cream patch:
**Our Lady** at half length, in a long white robe with a blue mantle, her head
uncovered, **holding her own heart in her right hand**. Rays of light fall from
her hands. Tender and inviting.

**`scapular_green_back.png`**: same green wool and single cord. On the cream
patch: a **heart pierced by a sword**, with flames rising from the top and rays
streaming out from it, beneath a small **cross**.

*Notes:* real Green Scapulars carry an oval prayer inscription round the heart.
**Leave it out.** The page quotes the prayer in full beneath the card.

## 7 · Passionist Scapular

**`scapular_passionist_front.png`**: **black wool**, black cords. No patch: the
**Passionist sign** is stitched straight onto the cloth. It is a **white heart**
outlined in white, with a white **cross** rising from the top. Inside the heart
are the words **JESU / XPI / PASSIO** on three lines, and **three nails**
converge beneath. Match `order_passionists_emblem`.

*Notes:* the letters **are** this emblem, so text is allowed here and nowhere
else. Image models often garble lettering. If it comes out wrong, generate the
heart with a blank interior and set the three words in afterwards with a clean
serif. Don't ship misspelled lettering.

**`scapular_passionist_back.png`**: same black wool and cords. Embroidered in
white thread straight onto the cloth: a **crown of thorns** circling **three
nails** crossed at the centre. Simple, stark, no colour.

---

## After generating

1. Keep the 1024×1024 PNG masters in `content/hub/symbols/`.
2. Ship them as **WebP (q90, alpha preserved)** in
   `shared/src/commonMain/composeResources/drawable/`, with the same base names,
   as was done for the medals (see the WebP conversion notes).
3. Rebuild. No content change is needed: `content/hubs/symbols.json` already
   points at `hub/symbols/scapular_*.png`, and `hubAssetPainter` resolves
   drawables by file name.
4. Check both cards side by side on a device. The front and back of each
   scapular should look like the same object.
