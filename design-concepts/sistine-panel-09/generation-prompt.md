# Sistine ceiling panel 9 — Drunkenness of Noah

Generated on 16 October 2026 with the built-in image_gen tool.

Source panel: `content/hub/holy_see/sistine_panel_09.png`.
Master diagram: `content/hub/holy_see/sistine_ceiling.png`.
Bundled image: `shared/src/commonMain/composeResources/drawable/sistine_ceiling.webp`.

## Final prompt

A small fresco panel in the style of Michelangelo's Sistine Chapel ceiling, matching soft faded fresco colours (muted ochres, rose, sage green, pale blue) and the same painted look as the neighbouring Genesis panels. The Drunkenness of Noah, shown modestly: on the right, the elderly white-bearded Noah lies asleep on the ground beside a wooden wine vat, his body fully covered by a loose rose-coloured cloak. In the centre, two of his sons walk backwards towards him with their faces turned away, holding a cloak between them to cover their father. On the left, the third son, Ham, points towards Noah. In the background, Noah tends a grapevine with a spade. Fully clothed figures, no nudity. Painted frame-free scene filling the whole canvas. No text, no letters, no numerals, no watermark, no signature, no border.

Asset type: replacement Genesis scene for Cathopedia's existing ceiling diagram. Canvas approximately 1.15:1 width to height, ideally 1150 by 1000 pixels. Use a soft classical academic sacred-painting interpretation with gently modeled, clearly readable faces, warm cream ground and aged fresco texture, calm atmospheric sky, muted terracotta and sage robes. Keep the main storytelling figures large and legible when this panel is reduced to 268 by 233 pixels. The two central sons must visibly face left, away from their father at the right, while stepping backward toward him and holding the covering cloth. Show the sleeping elder fully clothed/covered from neck to feet, not exposed. No rainbow, no ark, no animals, no decorative architecture or frame. Every person has natural, coherent anatomy.

## Assembly

Following the explicit paste instructions in `docs/image-prompts/pending-image-prompts.md`,
the existing hotspot rectangle is (378, 2389), size 268 × 233, on the 1024 × 3103 master.
That rectangle includes the painted border. To preserve it, the generated scene is fitted into
the inner picture window at (386, 2399), size 252 × 208. Only that inner rectangle changes.
The master is re-exported
as RGB WebP at quality 90; hotspot coordinates and targets are unchanged.

`asset-checks.json` records the exact changed bounds, dimensions and asset hashes.

## Verification

Android debug build and iOS simulator compilation passed. The APK contains the updated WebP;
all rebuilt resource copies match it. Taps on panels 4, 7, 8 and 9 were checked on the Android
emulator and opened the correct captions. `panel-09-native.png` shows the replacement scene
in the installed app.
