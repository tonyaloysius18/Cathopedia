# The Stigmata — image generation brief (v2, four saints)

Four images for "The Stigmata" article (Catechism hub, Creed section), one
per saint under "Some who bore the wounds":

1. **stigmata_francis** — St Francis of Assisi, Mount La Verna, 1224.
2. **stigmata_catherine** — St Catherine of Siena, Pisa, 1375.
3. **stigmata_pio** — St Padre Pio of Pietrelcina, 1918–1968.
4. **stigmata_gemma** — St Gemma Galgani, early 20th century.

This replaces the first brief (one generic "visible stigmata" scene plus a
symbolic Five Wounds emblem for the invisible form) — the article now shows
each named saint individually, bearing the marks, in the traditional
devotional-art style used for this subject for centuries (this is exactly
how St Catherine of Siena is normally depicted too, even though her own
stigmata were invisible in life by her own request — the article's caption
says so explicitly, so the image can follow the established iconography).

Same house style as the rest of the Catechism hub's devotional images:
classical academic sacred oil painting, warm golden light, reverent and
still. **No text, no labels, no watermark, no signature.**

## A note on taste — read before generating

This subject is easy to render badly — gory, sensational, or morbid. Every
prompt below is written to keep it restrained: the wounds are always a
**small, faint, gold-tinged mark** at the centre of the palm (or foot),
never an open injury, never dripping, never graphic. No anguished
expressions — every saint is shown peaceful, prayerful, eyes calm or
gently lifted. Think of a traditional stained-glass or devotional-painting
treatment, not a realistic or medical one.

## Rules for all four

- **Transparent background is required** — a PNG with a real alpha
  channel. Each saint is a half-length or kneeling figure, isolated on
  transparency, no room, no landscape, no floor — matching the app's other
  portrait-style images (e.g. the Priesthood and Nun/Sister portraits).
- Square **1:1, 1024×1024**.
- Each figure is centred, upright or kneeling, with at least one hand
  visible and turned so the small mark on the palm can be seen.
- Warm golden light, fine detail, reverent and calm — no gore, no visible
  blood beyond the single small mark, no pained expression.

**Negative prompt (all four):** `text, letters, words, labels, watermark,
signature, blood dripping, graphic wound, gore, horror, disturbing,
grimacing, anguished expression, realistic medical detail, modern clothing`

---

## 1. stigmata_francis — St Francis of Assisi

```
Create an original illustration in a classical academic sacred oil painting
style: Saint Francis of Assisi, kneeling half-length, wearing a brown
Franciscan habit with a knotted cord belt, hands raised open before his
chest in prayer, palms turned toward the viewer. A small, faint reddish-
gold mark at the centre of each palm — not a graphic wound, just a subtle
scar-like mark catching the light. His face is peaceful, eyes lifted
gently upward, a look of quiet wonder rather than pain. Warm golden light
falling from above. No landscape, no rocky ledge, no sky — just the figure,
isolated on a plain transparent background. No text, no labels, no
watermark, no signature, no blood, no graphic wound. Square 1:1, export
with a transparent alpha channel.
```

## 2. stigmata_catherine — St Catherine of Siena

```
Create an original illustration in a classical academic sacred oil painting
style: Saint Catherine of Siena, half-length, wearing the black-and-white
habit of a Dominican tertiary with a white veil, one hand held open and
turned toward the viewer at chest height, a small, faint reddish-gold mark
at the centre of the palm — subtle, not a graphic wound. Her other hand
rests over her heart. Her expression is serene and contemplative, eyes
gently lowered or lifted, not pained. Warm golden light. No background
scene — just the figure, isolated on a plain transparent background. No
text, no labels, no watermark, no signature, no blood, no graphic wound.
Square 1:1, export with a transparent alpha channel.
```

## 3. stigmata_pio — St Padre Pio of Pietrelcina

```
Create an original illustration in a classical academic sacred oil painting
style: Saint Padre Pio, half-length, an elderly Capuchin friar with a full
grey beard, wearing a brown Capuchin habit with a hood, one hand raised
in blessing or open in prayer at chest height, palm turned toward the
viewer, with a small fingerless brown prayer-mitten commonly associated
with him, and a faint reddish-gold mark visible at the centre of the palm
— subtle, not a graphic wound. Calm, kindly expression, eyes gently closed
or lowered in prayer. Warm golden light. No background scene — just the
figure, isolated on a plain transparent background. No text, no labels, no
watermark, no signature, no blood, no graphic wound. Square 1:1, export
with a transparent alpha channel.
```

## 4. stigmata_gemma — St Gemma Galgani

```
Create an original illustration in a classical academic sacred oil painting
style: Saint Gemma Galgani, a young Italian laywoman of the early 20th
century, half-length, wearing simple modest dark clothing with her hair
covered by a plain dark veil, hands joined in prayer at her chest with one
palm turned slightly toward the viewer, a small, faint reddish-gold mark at
its centre — subtle, not a graphic wound. Her expression is youthful,
peaceful and devout, eyes gently closed or lifted in prayer. Warm golden
light. No background scene — just the figure, isolated on a plain
transparent background. No text, no labels, no watermark, no signature, no
blood, no graphic wound. Square 1:1, export with a transparent alpha
channel.
```

---

## Deliverable

4 **transparent PNG** files, square 1:1 (1024×1024), named exactly:
`stigmata_francis.png`, `stigmata_catherine.png`, `stigmata_pio.png`,
`stigmata_gemma.png`. Hand them back as a single batch, then drop them into
`docs/image-prompts/catechism/` (or send them in chat) and tell me — I'll
convert them to WebP into
`shared/src/commonMain/composeResources/drawable/` and the content already
references them (`content/hubs/catechism.json`, article `art.cat.stigmata`).

If any figure's face turns out inaccurate to convention or a mark reads as
too graphic, ask for that one image again on its own rather than the whole
batch.
