# Cardinal portrait pipeline

Turns a Commons photo into the 320×400 portrait used in the app: cropped on the
face, the cardinal cut out (macOS Vision person segmentation) and placed on
the shared cardinal-red backdrop. Run from a work folder holding `raw/<id>`
(the downloaded photos) and `meta.json`:

1. `swift faces.swift raw faces.json` — face boxes (Vision).
2. `swift segment.swift raw masks` — fine person masks.
3. `swift instances.swift raw imasks` — one mask per person, to drop bystanders.
4. `python3 composite.py` — writes `red/<id>.webp` and contact sheets in `sheets_red/`.

Check every new portrait on the contact sheet. Per-photo fixes live at the top
of `composite.py`: `FACE_OVERRIDE` (wrong face picked), `TIGHT` (tighter crop),
and `VIGNETTE` (cut-out fails, e.g. white cassock on a white wall or a crowd:
fade the photo into the backdrop in an oval instead).

Copy the results to `remote/cardinals/photos/`, then bump `photosVersion` in
`build_cardinals.py` so installs fetch the new editions. Edited Commons
photos keep their licence (BY-SA stays BY-SA); the in-app credit says
"background replaced".
