# Offices in the College of Cardinals — image generation brief

Images for "Offices in the College of Cardinals" (`art.cardinals.offices`,
Holy See hub → Cardinals, second article after the College overview). It
uses the card layout of Church Towers and Sacred Symbols: each office is a
card with its name, a short text and the portrait shown **small, to one
side**.

| # | Asset | Office | Status |
| --- | --- | --- | --- |
| 1 | `cardinal_office_dean.png` | Dean of the College | pending |
| 2 | `cardinal_office_vice_dean.png` | Vice-Dean of the College | pending |
| 3 | `cardinal_office_secretary_state.png` | Cardinal Secretary of State | pending |
| 4 | `cardinal_office_camerlengo.png` | Cardinal Camerlengo | pending |
| 5 | `cardinal_office_prefect.png` | Cardinal Prefect | pending |
| 6 | `cardinal_office_vicar_rome.png` | Cardinal Vicar of Rome | pending |
| 7 | `cardinal_office_archbishop.png` | Cardinal Archbishop | pending |

Until an image lands, its card shows without a picture. Export each as
`<asset>.webp` (quality 82) into
`shared/src/commonMain/composeResources/drawable/`; it is picked up by name.

## House style (all seven)

Match the existing **`hierarchy_cardinal.png`** (Holy See → hierarchy): a
realistic painted bust of a cardinal in choir dress — scarlet cassock,
scarlet mozzetta with small buttons, scarlet biretta, gold pectoral cross on
a red-and-gold cord — like a classical oil portrait.

- **Transparent background is required** (real alpha channel). No room,
  no window, no Vatican scenery.
- **Square 1:1, 1024×1024.** Waist-up, three-quarter view, the head in the
  upper third, the figure filling about 90% of the frame height.
- Warm soft light from the upper left; fine detail in the fabric folds.
- **Fictional faces only.** These are offices, not people: none of the
  seven may resemble a real cardinal, past or present (especially not the
  current holders of these offices). Vary age, build and features so they
  read as seven different men.
- **No text, no letters, no coats of arms with writing, no watermark, no
  signature.**

**Negative prompt (all):** `text, letters, watermark, signature, frame,
background scenery, real person, celebrity likeness, pope, white cassock,
mitre (except where stated), cartoon, flat vector, low detail`

**Keep them distinct at thumbnail size.** Every cardinal wears the same
choir dress, so each portrait has **one clear prop or pose**:

| Office | Prop or pose |
| --- | --- |
| Dean | closed red book of the conclave rite held to the chest; white hair, oldest of the set |
| Vice-Dean | hands joined in prayer, slightly bowed head; grey hair |
| Secretary of State | dark leather diplomatic portfolio under the arm; younger, alert |
| Camerlengo | a ring of old keys in one hand (the vacant See's keys) |
| Prefect | a sheaf of documents with a red wax seal, held in front |
| Vicar of Rome | a gesture of blessing with the right hand |
| Archbishop | holding a gold crozier (pastoral staff), whose crook rises beside his head |

---

### 1. `cardinal_office_dean.png`

```
Realistic painted devotional portrait, waist-up, of an elderly Catholic
cardinal (fictional, not resembling any real person), white hair, rimless
glasses, in choir dress: scarlet cassock, scarlet mozzetta with small
buttons, scarlet biretta, gold pectoral cross on a red-and-gold cord. He
holds a closed red leather-bound liturgical book against his chest with both
hands; calm, dignified expression; three-quarter view. Warm soft light from
the upper left, classical oil-portrait finish. Transparent background. No
text, no watermark, no signature, no frame. Square 1:1.
```

### 2. `cardinal_office_vice_dean.png`

```
Realistic painted devotional portrait, waist-up, of an older Catholic
cardinal (fictional), grey hair, in choir dress: scarlet cassock, scarlet
mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. His
hands are joined in prayer at chest height and his head is slightly bowed,
eyes lowered; three-quarter view. Warm soft light from the upper left,
classical oil-portrait finish. Transparent background. No text, no
watermark, no signature, no frame. Square 1:1.
```

### 3. `cardinal_office_secretary_state.png`

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal in
his sixties (fictional), short dark-grey hair, alert expression, in choir
dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral
cross on a red-and-gold cord. He holds a dark brown leather diplomatic
portfolio under his left arm; three-quarter view. Warm soft light from the
upper left, classical oil-portrait finish. Transparent background. No text,
no letters on the portfolio, no watermark, no signature, no frame.
Square 1:1.
```

### 4. `cardinal_office_camerlengo.png`

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal
(fictional), balding with grey temples, serious composed expression, in
choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold
pectoral cross on a red-and-gold cord. In his right hand he holds up a large
iron ring of old ornate keys; three-quarter view. Warm soft light from the
upper left, classical oil-portrait finish. Transparent background. No text,
no watermark, no signature, no frame. Square 1:1.
```

### 5. `cardinal_office_prefect.png`

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal
(fictional), wearing round glasses, salt-and-pepper hair, in choir dress:
scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a
red-and-gold cord. He holds a small sheaf of cream parchment documents with a
red wax seal in front of him with both hands; three-quarter view. Warm soft
light from the upper left, classical oil-portrait finish. Transparent
background. No readable text on the documents, no watermark, no signature,
no frame. Square 1:1.
```

### 6. `cardinal_office_vicar_rome.png`

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal
(fictional), kind face, grey beard trimmed short, in choir dress: scarlet
cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a
red-and-gold cord. His right hand is raised in a gesture of blessing (first
two fingers extended); three-quarter view. Warm soft light from the upper
left, classical oil-portrait finish. Transparent background. No text, no
watermark, no signature, no frame. Square 1:1.
```

### 7. `cardinal_office_archbishop.png`

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal
archbishop (fictional), in choir dress: scarlet cassock, scarlet mozzetta,
scarlet biretta, gold pectoral cross on a red-and-gold cord. He holds a gold
crozier (pastoral staff) in his left hand, its ornate curved crook rising
beside his head; three-quarter view, confident pastoral expression. Warm
soft light from the upper left, classical oil-portrait finish. Transparent
background. No text, no watermark, no signature, no frame. Square 1:1.
```
