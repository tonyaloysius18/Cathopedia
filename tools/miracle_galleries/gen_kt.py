#!/usr/bin/env python3
"""Generate MiracleGalleries.kt from galleries.json."""
import json, os

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.abspath(os.path.join(HERE, "..", ".."))
DEST = os.path.join(REPO, "shared/src/commonMain/kotlin/com/ynotlabs/cathopedia/ui/MiracleGalleries.kt")

def esc(s):
    return s.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$")

g = json.load(open(os.path.join(HERE, "galleries.json")))
names = sorted({i["res"] for v in g.values() for i in v})

L = []
L.append("package com.ynotlabs.cathopedia.ui")
L.append("")
L.append("import com.ynotlabs.cathopedia.resources.Res")
for n in names:
    L.append(f"import com.ynotlabs.cathopedia.resources.{n}")
L.append("import org.jetbrains.compose.resources.DrawableResource")
L.append("")
L.append('''/**
 * Photographs of the miracles themselves — the relics, reliquaries, churches and
 * documents — as distinct from the devotional hero art in [Portraits].
 *
 * GENERATED FILE. Rebuild with tools/miracle_galleries/gen_kt.py rather than editing by hand;
 * the per-image WebP assets are produced by the same pipeline.
 *
 * Captions are English at the source. [GalleryImage.captionFor] falls back to the
 * English caption until a French pass fills [GalleryImage.captionFr] in.
 */''')
L.append("data class GalleryImage(")
L.append("    val image: DrawableResource,")
L.append("    val caption: String,")
L.append("    val captionFr: String? = null,")
L.append("    val sourceUrl: String,")
L.append(") {")
L.append("    fun captionFor(language: String): String =")
L.append('        if (language == "fr") captionFr ?: caption else caption')
L.append("}")
L.append("")
L.append("object MiracleGalleries {")
L.append("    fun forMiracle(id: String): List<GalleryImage> = galleries[id].orEmpty()")
L.append("")
L.append("    private val galleries: Map<String, List<GalleryImage>> = mapOf(")
for mid in sorted(g):
    L.append(f'        "{mid}" to listOf(')
    for im in g[mid]:
        cap = im["caption"] or im["alt"]
        L.append("            GalleryImage(")
        L.append(f'                image = Res.drawable.{im["res"]},')
        L.append(f'                caption = "{esc(cap)}",')
        L.append(f'                sourceUrl = "{im["source"]}",')
        L.append("            ),")
    L.append("        ),")
L.append("    )")
L.append("}")

open(DEST, "w").write("\n".join(L) + "\n")
print(f"wrote {DEST}: {len(L)} lines, {len(names)} images, {len(g)} galleries")
