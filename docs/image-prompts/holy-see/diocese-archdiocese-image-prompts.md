# Diocese and Archdiocese — image generation brief

Images for "Diocese and Archdiocese" (`art.holy_see.diocese_vs_archdiocese`,
Holy See hub, section "Diocese and Archdiocese"). It uses the side-by-side
comparison layout of "Patriarch and Pope": two portraits at the top, then a
table comparing the two, with a small symbol icon in the "Sign" row.

| # | Asset | Role | Status |
| --- | --- | --- | --- |
| 1 | `hierarchy_bishop.png` | Diocese — diocesan bishop (left portrait) | **reused**, already in the app |
| 2 | `archbishop_portrait.png` | Archdiocese — archbishop (right portrait) | pending |
| 3 | `mitre_bishop.png` | Diocese sign icon | **reused** |
| 4 | `pallium_archbishop.png` | Archdiocese sign icon | **reused** |

Only one new image is needed. Until it lands, the right-hand portrait reads as
a name card. Export it as `archbishop_portrait.webp` (quality 82) into
`shared/src/commonMain/composeResources/drawable/`; it is picked up by name.

## Match its partner

The archbishop stands beside the existing **`hierarchy_bishop.png`**: open it
and match it closely — the same bust framing (head and mitre in the upper
third, cut at mid-chest, figure centred), the same three-quarter pose, the
same realistic painted finish and warm light from the upper left, the same
transparent background. The two must look like a pair.

**What must differ — and only this:** the archbishop wears the **pallium**,
the narrow white woollen band with black crosses that circles the shoulders,
with one pendant hanging down the front, over his chasuble. That is the one
sign that tells a metropolitan archbishop from a bishop. Keep everything
else the same rank of dress: a bishop's mitre, a gold pectoral cross.

**Common mistakes to avoid:**

- **No cardinal's red.** An archbishop dresses like a bishop; the source
  infographic showed a red cope, which is wrong for an archbishop as such.
- **No double-barred cross held as a staff, no papal ferula, no tiara.**
- The pallium is **white with black crosses**, not gold, not a stole.

**Negative prompt:** `text, letters, watermark, signature, frame, background
scenery, real person, celebrity likeness, cardinal red, red cope, tiara, pope,
white zucchetto, cartoon, flat vector, low detail`

---

### 2. `archbishop_portrait.png`

```
Create an original illustration, matching a companion portrait of a Catholic
bishop: a realistic painted bust of a Catholic metropolitan archbishop
(fictional, not resembling any real person), late fifties, dark hair greying
at the temples, clean-shaven, calm pastoral expression, three-quarter view,
cut at mid-chest. He wears a tall white-and-gold embroidered mitre with
lappets, a cream-and-gold chasuble, and over it the pallium: a narrow white
woollen band circling his shoulders with one pendant hanging straight down
the front, marked with small black crosses and fastened with three gold pins.
A gold pectoral cross on a gold chain. Warm soft light from the upper left,
classical oil-portrait finish, fine detail in the embroidery. Transparent
background. No text, no watermark, no signature, no frame. Square 1:1,
1024×1024.
```
