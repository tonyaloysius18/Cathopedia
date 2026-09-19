# Catholic Medals — front & back image brief

**Status: done.** All six were generated and are in the app. Masters live in
`content/hub/symbols/`, shipped as 1024x1024 WebP (q90, alpha preserved) in
`shared/src/commonMain/composeResources/drawable/`. Kept for reference in case
any face is ever regenerated.

For the two-up medal cards in **Explore → Sacred Symbols → Catholic Medals**,
each medal shows both faces side by side under a FRONT / BACK label.

**Only six new images are needed.** Every medal already has one face drawn — five
of the existing assets are fronts, and `symbol_benedictine_medal.png` happens to
be a back. The table says what exists and what is missing.

| Medal | Front | Back |
| --- | --- | --- |
| 1 · Miraculous | `symbol_miraculous_medal.png` ✅ | `symbol_miraculous_medal_back.png` ⬅ generate |
| 2 · St Benedict | `symbol_benedict_medal_front.png` ⬅ generate | `symbol_benedictine_medal.png` ✅ |
| 3 · St Christopher | `symbol_christopher_medal.png` ✅ | `symbol_christopher_medal_back.png` ⬅ generate |
| 4 · Sacred Heart | `symbol_sacred_heart_medal.png` ✅ | `symbol_sacred_heart_medal_back.png` ⬅ generate |
| 5 · St Joseph | `symbol_joseph_medal.png` ✅ | `symbol_joseph_medal_back.png` ⬅ generate |
| 6 · Holy Family | `symbol_holy_family_medal.png` ✅ | `symbol_holy_family_medal_back.png` ⬅ generate |

`symbol_benedictine_medal.png` stays where it is — the Religious Orders section
references it too, so it must not be renamed.

## House style (applies to all six)

Identical to the existing medal art, so the two faces read as one object:

> A single **oval** medal struck in **polished gold relief**, shown face-on and
> filling the frame, a small ribbed suspension loop at the top, a beaded rim
> running round the edge. Soft studio light from the upper left, warm highlights,
> deep shadow in the recesses. Shallow bas-relief — the design raised out of the
> metal, not painted onto it. 1024 × 1024, transparent background (PNG with
> alpha). No backdrop, no chain, no cast shadow, no hand. **No text, no letters,
> no numerals, no watermark.** Reverent and devotional, not photoreal
> jewellery-catalogue.

**St Benedict's front is the one exception** — see its entry below.

---

## 1 · `symbol_miraculous_medal_back.png`

The reverse of the Miraculous Medal. A large letter **M** with a bar across its
centre, a cross rising from the bar. Beneath the M, two hearts side by side: on
the left the Sacred Heart of Jesus encircled by the crown of thorns, on the right
the Immaculate Heart of Mary pierced by a sword. Twelve six-pointed stars ringing
the whole design just inside the beaded rim.

This one **may keep the M and the cross** — they are letterforms as ornament, not
an inscription. Nothing else lettered.

## 2 · `symbol_benedict_medal_front.png`

**Style exception:** match `symbol_benedictine_medal.png`, not the oval gold set —
so **round**, gold with **deep blue enamel** infill, same rim treatment.

St Benedict standing full-length in a Benedictine habit, bearded, a cross raised
in his right hand and the Book of the Rule held against his chest in his left. At
his feet, a cup with a small serpent rising out of it on one side, and a raven
carrying a loaf of bread on the other. Rays engraved behind him.

Latin rim lettering is authentic to this medal and the existing back carries it,
so it is allowed here — but **check every letter before shipping**, since
generators mangle Latin. If the lettering comes out wrong, regenerate with a
plain beaded rim instead; a clean unlettered rim beats garbled Latin.

## 3 · `symbol_christopher_medal_back.png`

A tall plain Latin cross rising out of stylised water — the same engraved
wave-lines used on the front — with fine rays radiating behind it to the rim.
Simple and uncluttered; this reverse carries no figures.

## 4 · `symbol_sacred_heart_medal_back.png`

The **Immaculate Heart of Mary**, the traditional companion to the Sacred Heart
on the front: a single heart encircled by a wreath of roses, pierced through by
a straight sword running diagonally, flame rising from the top. Rays radiating
out to the rim behind it, struck as fine engraved lines.

## 5 · `symbol_joseph_medal_back.png`

A single spray of **lilies** — three open blooms and a bud on one stem, leaves
low around the base — centred and filling the field, rays engraved behind. The
lily is St Joseph's emblem of purity, so it stands alone here with no figure.

## 6 · `symbol_holy_family_medal_back.png`

**Three joined hearts**, the emblem of the Holy Family: the Sacred Heart of Jesus
crowned with thorns at the centre and slightly forward, the Immaculate Heart of
Mary pierced by a sword on the left, and the Chaste Heart of Joseph crowned with
a lily on the right. Flame rising from each. Rays behind, out to the beaded rim.

> **Alternative, if you want the source infographic's version instead:** that
> shows the Holy Family reverse as a cross with the letter M, hearts and stars —
> which is the *same* reverse as the Miraculous Medal. In that case generate
> nothing here and point this slot at `symbol_miraculous_medal_back.png`; the
> article text already says it is the shared Marian reverse. The three-hearts
> design above is briefed instead so the two cards don't show an identical
> picture.
