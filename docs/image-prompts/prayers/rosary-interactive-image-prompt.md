# Rosary screen image prompt (transparent, elongated — replaces the drawn rosary)

## What this replaces

The Rosary screen (`RosaryCanvas.kt`) currently draws every bead itself with
Compose `Canvas` — no bitmap at all. This prompt is for a real photographed
rosary, cut out on a transparent background, laid out **vertically** (tall
and narrow, since the phone screen is portrait) so it can be dropped in as
the visual in place of the procedural drawing.

**One implementation note so there's no surprise later**: the current canvas
version can be tapped bead-by-bead because every bead's position is known in
code. A photo doesn't carry that information — once you hand the image back,
I'll open it, measure where each bead actually sits in the final artwork,
and hand-calibrate the tap regions to match (the same kind of pixel-to-point
calibration I did earlier getting the iOS Simulator taps to land correctly).
That's normal, doable work on my end — just flagging it's a second step
after the image exists, not something the prompt itself handles.

## Correct bead order — exact sequence, second attempt

The second generation got the sequence wrong (blue beads landed in the
wrong spots relative to the white floral beads). This is the exact order,
walking from the cross upward to the medal, then around the loop and back
to the medal — spelled out step by step so nothing can drift:

1. **Cross** (bottom pendant — gold filigree, pearl-set, no corpus).
2. **1 dark cobalt-blue faceted glass bead** (directly above the cross).
3. **3 white porcelain beads** with the blue floral sprig pattern (in a
   row, above that blue bead).
4. **1 dark cobalt-blue faceted glass bead** (directly below the medal).
5. **Center medal** — small oval gold medal, image of the Virgin Mary,
   bordered in tiny seed pearls. This is the tail's endpoint and the
   loop's starting point.
6. **The loop**, starting and ending at the medal: a repeating unit of
   **10 white floral porcelain beads followed immediately by 1 dark
   cobalt-blue faceted glass bead**. This 10-white-then-1-blue unit
   repeats **exactly 5 times, unbroken**, all the way around the loop,
   closing back at the same medal where it started.

**On the third attempt, the tail came out correct but the loop still didn't
hold the count** — it had roughly the right number of blue beads (5) but
the white-bead runs between them were shorter than 10 and uneven from
segment to segment. Precise counting is a known weak spot for image
generators, so push on it explicitly and repeatedly rather than stating it
once.

**Fourth attempt: still off — 6 beads in one segment, 8 in another,
instead of 10 in every segment.** The generator is treating "10" as a
rough target rather than an exact one. The prompt below now names each of
the five segments individually with its own "exactly 10, count them"
instruction, rather than describing the pattern once and letting it repeat
— repetition of a single instruction seems to be where the count drifts,
so each segment gets called out on its own.

So, tail = cross, blue, white×3, blue, medal (5 beads total on the tail).
Loop = five repeats of (white×10, blue×1) = 55 beads total, closing at the
medal. Gold caps sit between every single bead throughout, on both the
tail and the loop.

## The prompt

> A rosary hanging naturally by gravity from a single point at the top (as
> if held up by one finger or hung from a hook), draping into one long
> elongated tall oval loop with a tail and cross hanging straight down
> below the medal. Exact bead sequence, walking from the cross upward: the
> cross (gold filigree, pearl-set, no corpus) is followed by 1 dark
> cobalt-blue faceted glass bead, then 3 white porcelain beads hand-painted
> with a cobalt-blue floral sprig pattern, then 1 more dark cobalt-blue
> faceted glass bead, then the center medal — a small oval gold medal
> stamped with an image of the Virgin Mary and bordered in tiny seed
> pearls. From the medal, the main loop begins and runs all the way around
> back to the same medal. The loop is built from five named segments,
> each one independently exactly ten white floral porcelain beads long,
> each followed by one dark cobalt-blue faceted glass bead:
> Segment 1 (starting at the medal): exactly 10 white beads — count them,
> 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 — then 1 blue bead.
> Segment 2: exactly 10 white beads — count them, 1, 2, 3, 4, 5, 6, 7, 8,
> 9, 10 — then 1 blue bead.
> Segment 3 (at the top of the loop): exactly 10 white beads — count them,
> 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 — then 1 blue bead.
> Segment 4: exactly 10 white beads — count them, 1, 2, 3, 4, 5, 6, 7, 8,
> 9, 10 — then 1 blue bead.
> Segment 5 (closing back at the medal): exactly 10 white beads — count
> them, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 — then 1 blue bead, then the medal.
> No segment may be shorter or longer than ten white beads — not six, not
> eight, not nine, not twelve — every single one of the five segments has
> precisely ten, and the five segments must look equal in length to each
> other, evenly filling the loop with no segment visually compressed or
> stretched relative to the others. Fine gold metal caps between every bead throughout,
> both on the tail and the loop. The loop drapes as a tall narrow oval,
> slightly off-center and not perfectly symmetrical, the way a real
> rosary falls when hung. Soft even studio lighting from the front, gentle
> natural shadow and highlight on each bead's curvature so the porcelain
> and glass read as dimensional, but the object fully isolated with
> nothing behind it — pure transparency outside the rosary's silhouette,
> no table, no fabric, no drop shadow baked into the image, no hand or
> hook visible. Sharp focus throughout the full length, every bead clearly
> countable, distinct, and individually visible — no two adjacent beads
> merged or overlapping. Photorealistic, no text, no watermark.

**Format**: tall portrait, transparent PNG, roughly 3:8 to 1:3 aspect ratio
(e.g. 900×2400px or 1000×2800px) so it reads as one continuous vertical
strand filling most of the screen height without the loop looking squashed.
No background, no shadow layer — the app's own dark green background will
show through, so it should look correct sitting directly on `#0B1A15`-range
green without any white/grey matte edge left over from the cutout.

## Once you have the image

Save it as `rosary_interactive.png` and hand it back — next I'll:

1. Drop it into `shared/src/commonMain/composeResources/drawable/`.
2. Measure the actual bead positions in the artwork and rebuild the tap
   geometry in `RosaryCanvas.kt` (or a new composable next to it) to match,
   so tapping a bead in the image still opens the right prayer.
3. Keep the existing highlight/progress behavior (recoloring the
   currently-prayed bead) working against the new art — likely via a subtle
   overlay glow drawn at the calibrated position rather than redrawing the
   bead itself, since the bead art now lives in the bitmap.

## If the count still won't hold after another try or two

Precise bead-counting is genuinely a weak spot for image generators —
worth knowing before spending too many more attempts chasing it in one
shot. An alternative that sidesteps the problem entirely: generate just a
handful of individual close-up bead/element photos on transparent
backgrounds — one white floral porcelain bead, one blue glass bead, one
gold cap, the medal, the cross — instead of the whole assembled rosary.
I can then place and repeat those at the exact positions the code already
computes (the same geometry `RosaryCanvas.kt` uses now), which guarantees
correct counts and decade spacing by construction, still gets you real
photographed jewelry textures instead of a flat drawing, and *also* solves
the tap-target calibration problem for free, since the positions are
already known in code rather than needing to be measured after the fact.
Worth considering if another full-assembly generation doesn't land it.
