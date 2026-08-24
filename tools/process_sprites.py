#!/usr/bin/env python3
"""Process raw rosary bead renders into centred, alpha-keyed sprites.

Standalone. Dependencies: Pillow + numpy only.

For each source render this script:
  1. Estimates the flat background colour from the four corners and keys every
     pixel within tolerance to alpha, leaving a 1px feathered edge so no grey
     fringe survives.
  2. Trims to the object's alpha bounding box.
  3. Uniformly scales so the object's *longest side* occupies 92% of the target
     canvas (normalises size drift between generations).
  4. Re-pads to the exact target size with the object's bbox centre on the
     canvas centre -- the canvas centre is the carousel rotation pivot.
  5. Exports PNG-32 to the compose drawable/rosary folder.

It then writes sprite_report.md next to the output listing, per sprite: final
size, alpha coverage, specular-highlight centroid (as a fraction of the object
box) and mean blue coverage. A warning is printed for any sprite whose highlight
centroid is more than 0.06 from the set median in x or y -- the flaw that makes
a set look assembled rather than photographed.

Missing sources are reported and skipped, never invented. Run from the repo root.
"""

from __future__ import annotations

import os
import statistics
from dataclasses import dataclass

import numpy as np
from PIL import Image, ImageFilter

SRC_DIR = "/Users/tonyaloysius/Documents/Cathopedia Images/Rosary/Rosary Beads"
# Compose Multiplatform resources are flat: every drawable lives directly in
# drawable/ (no subfolders — the resource-accessor generator treats any nested
# directory as an error). Sprites are namespaced with a rosary_ prefix, matching
# the existing rosary_background / rosary_marian assets.
OUT_DIR = "shared/src/commonMain/composeResources/drawable"
# The report is a review artifact, not app content, so it must NOT sit inside
# composeResources (which would bundle it / trip the resource generator).
REPORT_PATH = "tools/rosary_sprite_report.md"

# Fraction of the target canvas the object's longest side should occupy.
FILL_FRACTION = 0.92

# How far (0-255 euclidean RGB) a pixel may sit from the background colour and
# still be treated as fully transparent, plus the width of the feather band
# above it across which alpha ramps from 0 to fully opaque.
KEY_TOLERANCE = 42.0
KEY_FEATHER = 22.0


@dataclass(frozen=True)
class Target:
    source: str  # source filename in SRC_DIR
    out: str  # output filename in OUT_DIR
    size: tuple[int, int]  # (w, h)
    family: bool = False  # part of the repeated bead set checked for consistency


# Expected final sprite set. Order matters only for the report.
TARGETS: list[Target] = [
    Target("bead_hm_01.png", "rosary_bead_hm_01.png", (256, 256), family=True),
    Target("bead_hm_02.png", "rosary_bead_hm_02.png", (256, 256), family=True),
    Target("bead_hm_03.png", "rosary_bead_hm_03.png", (256, 256), family=True),
    Target("bead_hm_04.png", "rosary_bead_hm_04.png", (256, 256), family=True),
    Target("bead_hm_05.png", "rosary_bead_hm_05.png", (256, 256), family=True),
    Target("bead_hm_06.png", "rosary_bead_hm_06.png", (256, 256), family=True),
    Target("bead_of_01.png", "rosary_bead_of_01.png", (288, 288)),
    Target("spacer_gold.png", "rosary_spacer_gold.png", (128, 128)),
    Target("centerpiece_medal.png", "rosary_centerpiece_medal.png", (384, 384)),
    Target("cross_pearl.png", "rosary_cross_pearl.png", (384, 512)),
]


def estimate_background(rgb: np.ndarray) -> np.ndarray:
    """Mean colour of four 24x24 corner patches."""
    h, w = rgb.shape[:2]
    p = 24
    patches = [
        rgb[:p, :p],
        rgb[:p, w - p :],
        rgb[h - p :, :p],
        rgb[h - p :, w - p :],
    ]
    stacked = np.concatenate([q.reshape(-1, 3) for q in patches], axis=0)
    return stacked.mean(axis=0)


def key_to_alpha(rgb: np.ndarray) -> np.ndarray:
    """Return an RGBA float array with the flat background keyed to alpha.

    Alpha ramps linearly from 0 (within KEY_TOLERANCE of the bg colour) to 255
    (KEY_TOLERANCE + KEY_FEATHER away), giving a 1px-ish feathered edge with no
    grey halo.
    """
    bg = estimate_background(rgb)
    dist = np.sqrt(((rgb.astype(np.float32) - bg) ** 2).sum(axis=2))
    alpha = (dist - KEY_TOLERANCE) / KEY_FEATHER
    alpha = np.clip(alpha, 0.0, 1.0) * 255.0

    rgba = np.dstack([rgb.astype(np.float32), alpha]).astype(np.uint8)
    img = Image.fromarray(rgba, "RGBA")
    # A single-pixel blur softens the feathered boundary without eroding the
    # object, killing any residual one-pixel grey fringe.
    smoothed_alpha = img.split()[3].filter(ImageFilter.GaussianBlur(radius=1.0))
    img.putalpha(smoothed_alpha)
    return np.array(img)


def alpha_bbox(alpha: np.ndarray, thresh: int = 8) -> tuple[int, int, int, int]:
    """(left, top, right, bottom) of pixels with alpha above thresh."""
    ys, xs = np.where(alpha > thresh)
    if len(xs) == 0:
        raise ValueError("object fully transparent after keying")
    return int(xs.min()), int(ys.min()), int(xs.max()) + 1, int(ys.max()) + 1


def highlight_centroid(rgb: np.ndarray, alpha: np.ndarray) -> tuple[float, float]:
    """Specular-highlight centroid as a fraction (0-1) of the object bbox.

    Weights the brightest in-object pixels (top luminance percentile) by how far
    above the threshold they sit.
    """
    lum = rgb.astype(np.float32).mean(axis=2)
    inside = alpha > 128
    if not inside.any():
        return (0.5, 0.5)
    vals = lum[inside]
    cutoff = np.percentile(vals, 99.0)
    mask = inside & (lum >= cutoff)
    if not mask.any():
        mask = inside & (lum >= np.percentile(vals, 95.0))
    ys, xs = np.where(mask)
    weights = (lum[mask] - cutoff)
    weights = np.clip(weights, 1e-3, None)
    cx = np.average(xs, weights=weights)
    cy = np.average(ys, weights=weights)
    l, t, r, b = alpha_bbox(alpha)
    fx = (cx - l) / max(1, (r - l))
    fy = (cy - t) / max(1, (b - t))
    return (float(fx), float(fy))


def blue_coverage(rgb: np.ndarray, alpha: np.ndarray) -> float:
    """Mean blue coverage: fraction of object pixels that read as blue."""
    inside = alpha > 128
    if not inside.any():
        return 0.0
    r = rgb[:, :, 0].astype(np.int32)
    g = rgb[:, :, 1].astype(np.int32)
    b = rgb[:, :, 2].astype(np.int32)
    blue = (b - np.maximum(r, g) > 18) & (b > 70)
    return float((blue & inside).sum()) / float(inside.sum())


@dataclass
class Result:
    target: Target
    final_size: tuple[int, int]
    alpha_coverage: float
    highlight: tuple[float, float]
    blue: float


def process_one(t: Target) -> Result:
    src = os.path.join(SRC_DIR, t.source)
    rgb = np.array(Image.open(src).convert("RGB"))

    rgba = key_to_alpha(rgb)
    l, top, r, b = alpha_bbox(rgba[:, :, 3])
    cropped = rgba[top:b, l:r]

    obj_rgb = cropped[:, :, :3]
    obj_alpha = cropped[:, :, 3]
    highlight = highlight_centroid(obj_rgb, obj_alpha)
    blue = blue_coverage(obj_rgb, obj_alpha)

    ch, cw = cropped.shape[:2]
    tw, th = t.size
    longest = max(cw, ch)
    target_longest = FILL_FRACTION * max(tw, th)
    scale = target_longest / longest
    new_w = max(1, round(cw * scale))
    new_h = max(1, round(ch * scale))

    obj = Image.fromarray(cropped, "RGBA").resize((new_w, new_h), Image.LANCZOS)
    canvas = Image.new("RGBA", (tw, th), (0, 0, 0, 0))
    ox = round((tw - new_w) / 2)
    oy = round((th - new_h) / 2)
    canvas.paste(obj, (ox, oy), obj)

    os.makedirs(OUT_DIR, exist_ok=True)
    canvas.save(os.path.join(OUT_DIR, t.out))

    final_alpha = np.array(canvas)[:, :, 3]
    coverage = float((final_alpha > 128).sum()) / float(tw * th)
    return Result(t, t.size, coverage, highlight, blue)


def main() -> None:
    results: list[Result] = []
    missing: list[Target] = []

    for t in TARGETS:
        if not os.path.exists(os.path.join(SRC_DIR, t.source)):
            missing.append(t)
            print(f"MISSING  {t.source} -> skipped (not inventing artwork)")
            continue
        res = process_one(t)
        results.append(res)
        print(
            f"OK       {t.out:22s} {res.final_size[0]}x{res.final_size[1]:<4} "
            f"alpha={res.alpha_coverage:5.1%} "
            f"hl=({res.highlight[0]:.3f},{res.highlight[1]:.3f}) "
            f"blue={res.blue:5.1%}"
        )

    # Highlight-centroid consistency check across the repeated bead family only.
    # One-off objects (cross, medal, spacer, OF glass) each have a naturally
    # different highlight position and are not part of this comparison.
    fam = [r for r in results if r.target.family]
    warnings: list[str] = []
    if len(fam) >= 3:
        med_x = statistics.median(r.highlight[0] for r in fam)
        med_y = statistics.median(r.highlight[1] for r in fam)
        for r in fam:
            dx = abs(r.highlight[0] - med_x)
            dy = abs(r.highlight[1] - med_y)
            if dx > 0.06 or dy > 0.06:
                msg = (
                    f"WARN highlight centroid off: {r.target.out} "
                    f"dx={dx:.3f} dy={dy:.3f} (family median {med_x:.3f},{med_y:.3f})"
                )
                warnings.append(msg)
                print(msg)
    else:
        med_x = med_y = float("nan")

    write_report(results, missing, warnings, med_x, med_y)


def write_report(
    results: list[Result],
    missing: list[Target],
    warnings: list[str],
    med_x: float,
    med_y: float,
) -> None:
    lines: list[str] = []
    lines.append("# Rosary sprite report")
    lines.append("")
    lines.append(f"Generated by `tools/process_sprites.py`. Output: `{OUT_DIR}`.")
    lines.append("")
    lines.append(
        f"Hail Mary bead-family highlight-centroid median: **x={med_x:.3f}, "
        f"y={med_y:.3f}** (fraction of object box). Warn threshold: 0.06. "
        "Only the repeated bead family is checked for consistency; one-off "
        "objects each have a naturally distinct highlight and are reported "
        "as n/a."
    )
    lines.append("")
    lines.append("| Sprite | Final size | Alpha coverage | Highlight centroid (x,y) | Mean blue | Flag |")
    lines.append("|---|---|---|---|---|---|")
    for r in results:
        if r.target.family:
            dx = abs(r.highlight[0] - med_x)
            dy = abs(r.highlight[1] - med_y)
            flag = "⚠︎ off" if (dx > 0.06 or dy > 0.06) else "ok"
        else:
            flag = "n/a"
        lines.append(
            f"| `{r.target.out}` | {r.final_size[0]}×{r.final_size[1]} | "
            f"{r.alpha_coverage:.1%} | ({r.highlight[0]:.3f}, {r.highlight[1]:.3f}) | "
            f"{r.blue:.1%} | {flag} |"
        )
    lines.append("")
    if missing:
        lines.append("## Missing sources (skipped, not invented)")
        lines.append("")
        for t in missing:
            lines.append(f"- `{t.source}` → would export `{t.out}` at {t.size[0]}×{t.size[1]}")
        lines.append("")
    if warnings:
        lines.append("## Warnings")
        lines.append("")
        for w in warnings:
            lines.append(f"- {w}")
        lines.append("")

    os.makedirs(os.path.dirname(REPORT_PATH), exist_ok=True)
    with open(REPORT_PATH, "w") as f:
        f.write("\n".join(lines) + "\n")
    print(f"\nwrote {REPORT_PATH}")


if __name__ == "__main__":
    main()
