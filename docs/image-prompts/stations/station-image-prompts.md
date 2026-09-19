# Stations of the Cross — carousel image prompts

Reference: the same house style already used for saints/apparitions/prayer
backgrounds — classical academic sacred oil painting, warm golden light,
rich earthy palette, fine brushwork, reverent and still, no text, no
watermark, no signature.

**This set is different in one deliberate way**: borderless, transparent
background — no painted environment, no ground, no sky, no frame. Each
image is the figure(s)/central action of that station rendered in full,
isolated against pure transparency, so it drops directly onto the carousel
card's own blood-red-to-gold gradient in the app with nothing to composite
or crop. Keep the figure group roughly centered with breathing room on all
sides (don't bleed a figure off the canvas edge) since the card has rounded
corners and the number badge sits in the top-right.

**Format**: portrait, roughly 3:4 aspect (900×1200px), transparent PNG
background, high resolution.

**Cross geometry fix (revision 2)** — the first batch of generated images
got the cross shape wrong: the crossbar and upright were rendered crossing
each other diagonally, more like an X or two loose beams than an actual
cross. Every prompt below that includes the cross now spells out the
correct construction explicitly: **a proper Latin cross — a single long
vertical wooden beam with a distinctly shorter horizontal crossbar fixed
perpendicular to it, joined near the upright's top third, forming a clean
"†" shape.** The crossbar must read as clearly perpendicular to the
upright, not tilted or crossing it at a diagonal angle, regardless of how
the whole cross is angled across Christ's shoulder or body.

**INRI titulus (revision 2)** — stations 12 and 13 (the crucifixion and
the descent from the cross) now call for the small wooden plaque
traditionally nailed above Christ's head reading "INRI", per the Gospel
account and standard iconography.

---

## The 14 prompts

- **01-jesus-condemned-to-death** — Christ standing bound before Pilate's
  judgment seat, head slightly bowed but dignified, Pilate seated to one
  side gesturing dismissively with a basin of water nearby; isolated group,
  no architecture behind them.
- **02-jesus-carries-his-cross** — Christ shouldering the full weight of a
  rough-hewn wooden cross — a proper Latin cross, one long vertical beam
  with a clearly shorter crossbar fixed perpendicular near its top, not
  crossing diagonally — robe in disarray, one hand steadying the upright
  beam, captured mid-stride in a moment of strained effort.
- **03-jesus-falls-the-first-time** — Christ collapsed on one knee, the
  Latin cross (long vertical beam, short perpendicular crossbar near the
  top, not diagonal) resting across His shoulder and back under its own
  weight, arms braced against the ground, face strained, crown of thorns
  visible.
- **04-jesus-meets-his-mother** — Christ and Mary facing each other for a
  brief instant amid the procession, their eyes locked, both figures
  isolated mid-turn toward one another, the Latin cross (long vertical
  beam, short perpendicular crossbar near the top) still resting on
  Christ's shoulder behind Him.
- **05-simon-of-cyrene-helps-jesus-carry-the-cross** — Simon of Cyrene, a
  weathered older man in simple tunic, gripping the trailing end of the
  cross's vertical beam alongside Christ, sharing the load; the cross
  itself a proper Latin cross with a short perpendicular crossbar near the
  top of the long upright, not two beams crossing diagonally.
- **06-veronica-wipes-the-face-of-jesus** — A woman in a modest veil
  gently pressing a white cloth to Christ's face, His eyes closed in
  weary gratitude, the cloth just beginning to bear the imprint of His
  features; the Latin cross (long vertical beam, short perpendicular
  crossbar near the top) resting on Christ's far shoulder behind Him.
- **07-jesus-falls-the-second-time** — Christ fallen further this time,
  nearly prone, one hand pressed flat against the ground pushing to rise,
  the Latin cross (long vertical beam, short perpendicular crossbar near
  the top, not diagonal) askew across His back.
- **08-jesus-meets-the-women-of-jerusalem** — Christ turning toward a
  small group of grieving women reaching toward Him, His hand raised
  gently as if consoling them in return, the Latin cross (long vertical
  beam, short perpendicular crossbar near the top) on His shoulder behind
  Him.
- **09-jesus-falls-the-third-time** — Christ collapsed fully to the
  ground, utterly spent, face turned slightly upward, the Latin cross
  (long vertical beam, short perpendicular crossbar near the top, not
  diagonal) heavy across His shoulders — the most physically weighted and
  low composition of the three falls.
- **10-jesus-is-stripped-of-his-garments** — Two soldiers pulling Christ's
  outer robe from His shoulders, Christ standing passive and unresisting,
  arms slightly lifted as the cloth is drawn away.
- **11-jesus-is-nailed-to-the-cross** — Christ laid back against the
  cross on the ground — a proper Latin cross, long vertical beam with a
  clearly shorter crossbar fixed perpendicular near the top, already
  assembled — a soldier kneeling at His hand with hammer and nail raised,
  the moment just before impact rather than the act itself.
- **12-jesus-dies-on-the-cross** — Christ on the raised cross against open
  space: a proper Latin cross, one long vertical beam with a distinctly
  shorter crossbar fixed perpendicular to it near the top, forming a clean
  "†" shape — head fallen to one side, body still. A small wooden plaque
  is nailed to the upright just above His head, bearing the inscription
  "INRI" in simple carved lettering. No crowd — the figure alone in its
  final stillness.
- **13-jesus-is-taken-down-from-the-cross** — Christ's body being lowered
  gently by two figures, one supporting His shoulders, one His legs, the
  Latin cross still upright behind them (long vertical beam, short
  perpendicular crossbar near the top, not diagonal), the small "INRI"
  plaque still nailed to the upright above where His head had rested, a
  moment of careful tenderness.
- **14-jesus-laid-in-the-tomb** — Christ's body laid out in repose on a
  simple burial cloth, hands crossed over His chest, expression fully at
  peace, no tomb architecture — just the figure and the cloth.

---

## Once you have the images

Save each as `station_<number>.png` (e.g. `station_01.png` through
`station_14.png`, matching `content/stations/<number>-<slug>.json`'s
leading number) and hand them back — I'll drop them into
`shared/src/commonMain/composeResources/drawable/` and wire each into its
card on the Stations screen, behind the title/number, at a moderate
opacity so it reads clearly against the red/gold background without
fighting the text.
