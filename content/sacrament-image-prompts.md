# The Seven Sacraments — carousel image prompts

Reference: the same house style used for the Stations of the Cross carousel
and the saints/apparitions/prayer backgrounds — classical academic sacred oil
painting, warm golden light, rich earthy palette, fine brushwork, reverent and
still, no text, no watermark, no signature.

**Same deliberate treatment as the Stations set**: borderless, **transparent
background** — no painted environment, no architecture, no ground, no sky, no
frame. Each image is the central sacramental action and its figure(s) rendered
in full, isolated against pure transparency, so it drops straight onto the
carousel card's own gradient in the app with nothing to composite or crop.
Keep the figure group roughly **centered with breathing room on all sides**
(don't bleed a figure off the canvas edge) — the card has rounded corners and a
label/badge area.

**Format**: portrait, roughly 3:4 aspect (900×1200px), **transparent PNG**
background, high resolution.

**Show the moment of the sacrament itself** — the visible sign in action
(the water poured, the hands laid on, the ring given) rather than a static
portrait — so each card reads instantly as *that* sacrament. Vest clergy
appropriately (white/gold), keep faces reverent, and give each a distinct
composition from the others.

---

## The 7 prompts

- **baptism** — A priest in white and gold vestments pouring water from a
  scallop shell over the head of an infant cradled in a parent's arms, the
  stream of water caught mid-fall and glistening on the child's brow; the small
  group isolated, no font or church architecture behind them.
- **confirmation** — A bishop in a mitre anointing the forehead of a kneeling
  young person with holy chrism, his thumb tracing a small cross on the brow,
  the confirmand's eyes lowered in reverence; the two figures isolated, nothing
  behind them.
- **eucharist** — A priest in vestments placing a round white Host into the
  open, upraised hands of a kneeling communicant, a golden chalice resting on a
  white cloth beside them, both figures still and reverent; isolated, no altar
  or nave behind them.
- **penance** — A priest seated with one hand raised in the gesture of
  absolution over a penitent who kneels with bowed head and folded hands, an
  air of quiet mercy between them; the two figures alone, no confessional
  architecture — just the pair.
- **anointing_of_the_sick** — A priest gently anointing the forehead of an
  elderly, frail person resting propped in bed, his oil-marked thumb tracing a
  cross on the brow, a small vessel of holy oil in his other hand, tender and
  peaceful; the figures isolated, no room around them.
- **holy_orders** — A bishop in a mitre laying both hands upon the head of a
  young man kneeling before him in ordination, the ordinand's head bowed and
  hands joined, solemn and still; the two figures isolated, no cathedral behind
  them.
- **matrimony** — A bride and groom facing each other and joining their right
  hands as the groom slips a ring onto the bride's finger, a priest's hand
  resting in blessing over their joined hands; the three figures isolated
  together, no church background.

---

## Once you have the images

Save each as `sacrament_<slug>.png` (e.g. `sacrament_baptism.png`,
`sacrament_confirmation.png`, `sacrament_eucharist.png`, `sacrament_penance.png`,
`sacrament_anointing_of_the_sick.png`, `sacrament_holy_orders.png`,
`sacrament_matrimony.png`) and hand them back — I'll drop them into
`shared/src/commonMain/composeResources/drawable/` and wire each onto its
sacrament card/section, behind the title at a moderate opacity so it reads
clearly against the card background without fighting the text.
