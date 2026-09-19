# Can a Priest Leave the Priesthood? — image generation brief

Two images for the third section of the Priesthood hub. Everything else on that
page reuses existing emblems: the scroll, the cathedra and the papal cross for
the three canonical routes, the anchor for the leave of absence, the Benedictine
medal for the religious note, the crucifix for absolution in danger of death,
and the Sacred Heart for the closing plea.

| Slot | Asset | Where |
| --- | --- | --- |
| Page hero | `priest_hard_season.png` | under the intro, above "Ordination cannot be undone" |
| Second illustration | `priest_brother_support.png` | under "Before anything else" |

**These two carry the pastoral weight of the page**, which is read by two very
different people: someone curious, and a priest in trouble. They must be
sympathetic, never accusing, and never melodramatic. No tears, no clutched
head in hands, no bottle, no shattered symbolism. A man tired and honest, and a
man being helped.

House style: classical academic sacred oil painting, fine detail, warm light,
rich earthy palette, quiet and reverent — matching the rest of the hub.
**No text, no labels, no watermark, no signature.**

## Rules for both

- **Landscape 3:2 at 1200×800.** These render full-width with rounded corners,
  so unlike the rest of the hub they are **not** transparent: paint the whole
  frame, background included.
- Keep faces turned partly away or lowered. The reader should be able to put
  himself in the picture, which a direct stare prevents.
- One or two figures only; no congregation, no crowd.

**Negative prompt (both):** `text, letters, words, labels, watermark, signature,
frame, border, cartoon, flat vector, low detail, distorted hands, extra fingers,
tears, crying, despair, alcohol, bottle, broken cross, torn vestment, dramatic
lighting, horror, woman`

---

## `priest_hard_season.png` — the page hero

Classical academic sacred oil painting, warm low light, full painted scene. **A
priest alone in an empty church at night**, seated sideways in the front pew in
a plain black cassock, elbows on his knees, hands loosely clasped, **head bowed
and face in shadow**. A **single sanctuary lamp** burns red in the darkness
behind him and a **candle stand** glows to one side; the rest of the nave falls
away into deep shadow. Still, quiet, weary — but not hopeless: the light in the
frame is small and steady, and it is not going out. 3:2.

*Notes:* the sanctuary lamp matters — it says the Blessed Sacrament is present
and he is not as alone as he feels. Keep him **seated upright, not collapsed**.
No visible face detail is needed.

## `priest_brother_support.png` — under "Before anything else"

Classical academic sacred oil painting, warm daylight from a window, full
painted scene. **Two priests in conversation**: an **older priest with grey hair
resting one hand on the shoulder** of a **younger priest** who sits leaning
forward, listening, in a quiet room with a plain table, two cups and a window
letting in soft light. Both in black clerical dress. The older one is speaking
gently; the younger is not weeping, just tired and attentive. Ordinary,
undramatic, kind — the picture of a conversation that helps. 3:2.

*Notes:* it must read as **fraternity, not correction** — no finger pointing, no
documents on the table, no standing over him. The hand on the shoulder and the
level eye line do the work.

---

## After generating

Put both in `shared/src/commonMain/composeResources/drawable/`, convert to WebP
like the rest of the set, and rebuild. No content change is needed:
`content/hubs/priesthood.json` already points at them and marks them
`"hero": true`, which draws them full-width instead of as thumbnails. Until they
land the page simply skips them.
