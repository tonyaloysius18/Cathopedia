package com.ynotlabs.cathopedia.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.allDrawableResources
import org.jetbrains.compose.resources.painterResource

/**
 * Resolves a hub content `asset` path (e.g. `"hub/holy_see/vatican_city_plan.svg"`, see
 * content/hubs/holy_see.json) to a bundled drawable by basename, via Compose Resources'
 * generated [Res.allDrawableResources] lookup table rather than a per-hub `when` — the hub is
 * data, not code (docs/briefs/topic-hubs.md), so nothing here may name a specific hub's assets.
 * Returns null (never crashes) for a path with no matching bundled drawable.
 */
@Composable
fun hubAssetPainter(assetPath: String?): Painter? {
    if (assetPath.isNullOrBlank()) return null
    val key = assetPath.substringAfterLast('/').substringBeforeLast('.')
    val resource = Res.allDrawableResources[key] ?: return null
    return painterResource(resource)
}
