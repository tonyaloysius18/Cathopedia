# Pending image prompts

**No image is blocking the app.** Every image the code uses is in
`shared/src/commonMain/composeResources/drawable/` (checked 16 October 2026; the cardinal portraits and
candle holders that used to be listed here are done).

## Optional: Sistine ceiling, panel 9

Panel 9 of the ceiling diagram should be the **Drunkenness of Noah**, but the generated art shows Noah under a
rainbow. Tapping it opens the right caption over the wrong scene. The full brief is
`holy-see/sistine-ceiling-image-prompts.md`.

Make one image, **`sistine_panel_09.png`**, about **1.15 : 1** (for example 1150×1000). It is pasted into the
existing frame of `content/hub/holy_see/sistine_ceiling.png` at x 0.369, y 0.770, w 0.262, h 0.075 of the
1024×3103 canvas (268×233 px), then re-exported as `sistine_ceiling.webp`. The hotspots stay as they are.

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
