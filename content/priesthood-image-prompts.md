# The Priesthood hub — image generation brief

Four images for the new **Priesthood** hub (Explore → The Priesthood). The
formation path needs no art of its own: all eighteen of its stages reuse
existing `symbol_*` emblems and `hierarchy_deacon`.

| Slot | Asset | Needed for |
| --- | --- | --- |
| Comparison, left | `priest_religious.png` | the religious priest's portrait |
| Comparison, right | `priest_diocesan.png` | the diocesan priest's portrait |
| Explore card | `explore_priesthood.png` | the hub's card in Explore |
| Explore icon | `priesthood_icon.png` | the small medallion on that card |

House style throughout: classical academic sacred oil painting, fine detail,
warm golden light from the upper left, rich earthy palette, reverent and still.
**No text, no labels, no watermark, no signature.**

---

## The two portraits

These are drawn side by side, each about half the screen wide, above a "VS"
medallion. They must read as **a matched pair**: same height in frame, same eye
level, same light, same distance. If one is closer or larger, the page looks
like it is taking sides — and the whole point of the article is that neither
priest outranks the other.

- **Square 1:1 at 768×768**, transparent PNG with a real alpha channel.
- Three-quarter length — head to mid-thigh — centred, facing slightly inward
  towards the other portrait.
- No background: no church, no wall, no floor, no shadow.
- Calm, kind, ordinary faces. A man in his thirties or forties in each case,
  not idealised and not stern. Different faces from each other.
- Hands must be clean and plausible: at rest, or holding the one object named.

**Negative prompt (both):** `text, letters, words, labels, watermark, signature,
frame, border, background scenery, church interior, walls, floor, pews, altar,
shadow, cartoon, flat vector, low detail, distorted hands, extra fingers, mitre,
crozier, bishop, pope, woman`

### `priest_religious.png` — left

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery. A **Catholic religious priest in a monastic habit**: a
long **cream-white tunic with a black hooded cappa over it**, a **knotted cord
at the waist**, and a **simple wooden cross on a cord at his chest**. He holds a
**closed brown leather book** against his body with both hands. He faces
slightly to the right, head a little inclined, expression peaceful. Three-quarter
length, centred. 1:1.

*Notes:* the white-and-black habit reads instantly as "religious" beside a black
cassock, which is why it is specified. Keep it generic — **no order's emblem, no
scapular badge, no rosary at the belt**, so it does not claim to be Dominican or
Carmelite in particular.

### `priest_diocesan.png` — right

Classical academic sacred oil painting, warm golden light, transparent
background, no scenery. A **Catholic diocesan priest in a plain black cassock**
buttoned to the neck, with a **white Roman collar** showing at the throat. No
cape, no sash, no ornament of any kind. His hands are **folded quietly in front
of him**. He faces slightly to the left, calm and attentive. Three-quarter
length, centred. 1:1.

*Notes:* plainness is the point — the cassock should look worn and ordinary. **No
piping or coloured buttons**, which would make him a monsignor or a bishop.

---

## `explore_priesthood.png` — the Explore card

Matches the other `explore_*` hub cards: a wide painted group that sits to the
right of the card with the title over the empty left side.

- **4:3 landscape at 1024×768**, transparent PNG.
- The subject occupies the **right two-thirds**; the left third stays empty for
  the title.

Classical academic sacred oil painting, warm golden light from the upper left,
transparent background, no scenery. **An ordination**: a **bishop's two hands
laid on the bowed head of a kneeling young man in a white alb**, seen from the
side, with a **second priest waiting behind** and a **gold chalice and a stole
resting on a cushion** in the foreground. Solemn, hushed, the central moment of
the sacrament. 4:3.

*Notes:* the bishop can be cropped at the shoulders — the hands and the bowed
head are the subject. No cathedral behind them; the group sits on transparency.

## `priesthood_icon.png` — the card medallion

Matches the other hub icons (`catechism_icon`, `holy_mass_icon`): a small round
gilded badge, drawn at 35dp, so it must read at thumbnail size.

- **Square 1:1 at 512×512**, transparent PNG.
- A **circular medallion with a rope-twist gold rim**, dark green enamel field.
- At its centre, **a gold stole crossed over a chalice** — the two signs proper
  to the priesthood. Bold, simple shapes; no fine detail, no lettering.

---

## After generating

Put the four PNGs in `shared/src/commonMain/composeResources/drawable/` and
rebuild. The two portraits need no code change — `content/hubs/priesthood.json`
already points at them. For the card, add the hub to the two `when` blocks in
`HubExploreCard` in `ui/screens/home/ExploreScreen.kt`:

```kotlin
val isPriesthood = hub.id == "priesthood"
// artwork: isPriesthood -> Res.drawable.explore_priesthood
// icon:    isPriesthood -> Res.drawable.priesthood_icon
```

Until then the card falls back to the generic `explore_bg` artwork with no icon,
which is what it shows today.
