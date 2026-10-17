# Pending image prompts

**No image is blocking the app.** Every image the code uses is in
`shared/src/commonMain/composeResources/drawable/` (checked 16 October 2026; the cardinal portraits and
candle holders that used to be listed here are done).

## Completed: Positions Given to Priests in a Diocese (17 October 2026)

All 16 `diocese_position_*` portraits are done. The PNG masters are in
`content/hub/holy_see/` and the WebP files in drawable/. See
[`holy-see/diocese-positions-image-prompts.md`](holy-see/diocese-positions-image-prompts.md).

## Completed: Sistine ceiling, panel 9 (16 October 2026)

Panel 9 now shows the **Drunkenness of Noah**, replacing the incorrect rainbow scene.
The generated source is `content/hub/holy_see/sistine_panel_09.png`; the master and bundled WebP
have been updated without changing the hotspots. Generation notes and exact pixel checks are in
`design-concepts/sistine-panel-09/`. The full brief is
`holy-see/sistine-ceiling-image-prompts.md`.

The source image, **`sistine_panel_09.png`**, is **1344×1170** (about **1.15 : 1**).
The existing hotspot is x 0.369, y 0.770, w 0.262, h 0.075 of the 1024×3103 canvas
(268×233 px at 378, 2389). Since this box includes the painted border, the scene was fitted
inside it at **(386, 2399), size 252×208**, preserving that border. The master was then
re-exported as `sistine_ceiling.webp` at quality 90. The hotspots are unchanged.

```
A small fresco panel in the style of Michelangelo's Sistine Chapel ceiling, matching soft faded fresco colours
(muted ochres, rose, sage green, pale blue) and the same painted look as the neighbouring Genesis panels.
The Drunkenness of Noah, shown modestly: on the right, the elderly white-bearded Noah lies asleep on the ground
beside a wooden wine vat, his body fully covered by a loose rose-coloured cloak. In the centre, two of his sons
walk backwards towards him with their faces turned away, holding a cloak between them to cover their father.
On the left, the third son, Ham, points towards Noah. In the background, Noah tends a grapevine with a spade.
Fully clothed figures, no nudity. Painted frame-free scene filling the whole canvas. No text, no letters,
no numerals, no watermark, no signature, no border.
```

## Briefs that are not wired into the app

These briefs exist but no screen uses them; make them only if the feature is built:

- `mass/monstrance-parts-image-prompts-v2.md`: eleven part images for monstrance part cards.
- `prayers/rosary-hero-image-prompt.md` and `prayers/rosary-interactive-image-prompt.md`: alternative Rosary art.
