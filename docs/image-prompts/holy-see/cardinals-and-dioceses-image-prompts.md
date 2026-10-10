# Image prompts — College of Cardinals and Diocese and Archdiocese

**Status: generated — 2026-10-10.** All eight transparent RGBA PNGs are
saved beside this brief at 1024×1024. Matching WebP copies (quality 82)
are in `shared/src/commonMain/composeResources/drawable/`. The exports
preserve the PNG alpha channels exactly. Final prompts are recorded in
[cardinals-and-dioceses-generation.json](cardinals-and-dioceses-generation.json);
light/dark checks are shown in
[cardinals-and-dioceses-preview.jpg](cardinals-and-dioceses-preview.jpg).

Eight images for two Holy See topics, ready to copy and paste: seven cardinal portraits and one archbishop portrait. Name each file exactly as its heading and put the finished PNGs in `docs/image-prompts/holy-see/`.

**If your tool can't make transparent backgrounds,** replace “Transparent background” with “plain flat light grey background”; the backgrounds can be cut out afterwards.

All eight are square 1:1, 1024×1024, with fictional faces that resemble no real person.

## 1. Offices in the College of Cardinals (7 portraits)

Square 1:1, 1024×1024. Fictional faces only — none may resemble a real cardinal.

**Negative prompt (all seven):**

```
text, letters, watermark, signature, frame, background scenery, real person, celebrity likeness, pope, white cassock, cartoon, flat vector, low detail
```

### 1.1 `cardinal_office_dean.png` — Dean of the College

```
Realistic painted devotional portrait, waist-up, of an elderly Catholic cardinal (fictional, not resembling any real person), white hair, rimless glasses, in choir dress: scarlet cassock, scarlet mozzetta with small buttons, scarlet biretta, gold pectoral cross on a red-and-gold cord. He holds a closed red leather-bound liturgical book against his chest with both hands; calm, dignified expression; three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no watermark, no signature, no frame. Square 1:1.
```

### 1.2 `cardinal_office_vice_dean.png` — Vice-Dean of the College

```
Realistic painted devotional portrait, waist-up, of an older Catholic cardinal (fictional), grey hair, in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. His hands are joined in prayer at chest height and his head is slightly bowed, eyes lowered; three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no watermark, no signature, no frame. Square 1:1.
```

### 1.3 `cardinal_office_secretary_state.png` — Cardinal Secretary of State

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal in his sixties (fictional), short dark-grey hair, alert expression, in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. He holds a dark brown leather diplomatic portfolio under his left arm; three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no letters on the portfolio, no watermark, no signature, no frame. Square 1:1.
```

### 1.4 `cardinal_office_camerlengo.png` — Cardinal Camerlengo

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal (fictional), balding with grey temples, serious composed expression, in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. In his right hand he holds up a large iron ring of old ornate keys; three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no watermark, no signature, no frame. Square 1:1.
```

### 1.5 `cardinal_office_prefect.png` — Cardinal Prefect

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal (fictional), wearing round glasses, salt-and-pepper hair, in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. He holds a small sheaf of cream parchment documents with a red wax seal in front of him with both hands; three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No readable text on the documents, no watermark, no signature, no frame. Square 1:1.
```

### 1.6 `cardinal_office_vicar_rome.png` — Cardinal Vicar of Rome

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal (fictional), kind face, grey beard trimmed short, in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. His right hand is raised in a gesture of blessing (first two fingers extended); three-quarter view. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no watermark, no signature, no frame. Square 1:1.
```

### 1.7 `cardinal_office_archbishop.png` — Cardinal Archbishop

```
Realistic painted devotional portrait, waist-up, of a Catholic cardinal archbishop (fictional), in choir dress: scarlet cassock, scarlet mozzetta, scarlet biretta, gold pectoral cross on a red-and-gold cord. He holds a gold crozier (pastoral staff) in his left hand, its ornate curved crook rising beside his head; three-quarter view, confident pastoral expression. Warm soft light from the upper left, classical oil-portrait finish. Transparent background. No text, no watermark, no signature, no frame. Square 1:1.
```

## 2. Diocese and Archdiocese (1 portrait)

Square 1:1, 1024×1024, transparent. Must match the existing `hierarchy_bishop` portrait it stands beside. Put it in `docs/image-prompts/holy-see/`.

**Negative prompt:**

```
text, letters, watermark, signature, frame, background scenery, real person, celebrity likeness, cardinal red, red cope, tiara, pope, white zucchetto, cartoon, flat vector, low detail
```

### 2.1 `archbishop_portrait.png` — Metropolitan archbishop (with pallium)

```
Create an original illustration, matching a companion portrait of a Catholic bishop: a realistic painted bust of a Catholic metropolitan archbishop (fictional, not resembling any real person), late fifties, dark hair greying at the temples, clean-shaven, calm pastoral expression, three-quarter view, cut at mid-chest. He wears a tall white-and-gold embroidered mitre with lappets, a cream-and-gold chasuble, and over it the pallium: a narrow white woollen band circling his shoulders with one pendant hanging straight down the front, marked with small black crosses and fastened with three gold pins. A gold pectoral cross on a gold chain. Warm soft light from the upper left, classical oil-portrait finish, fine detail in the embroidery. Transparent background. No text, no watermark, no signature, no frame. Square 1:1, 1024×1024.
```
