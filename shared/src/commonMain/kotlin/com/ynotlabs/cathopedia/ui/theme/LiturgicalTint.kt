package com.ynotlabs.cathopedia.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.ynotlabs.cathopedia.liturgical.LiturgicalColor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Tints the app's green base (the "sanctuary" background, surfaces and bar) into the colour of the
 * day's Mass, matching the chasubles drawn in the app: a deep shade of the vestment's own fabric. Gold, text and artwork are untouched:
 * only greens (hue 80–190°) move, so semantic colours are never shifted by accident.
 *
 * [color] is snapshot state, so every colour read through [shift] (in composition, layout or
 * drawing) updates by itself when the day changes.
 */
object LiturgicalTint {
    var color by mutableStateOf(LiturgicalColor.GREEN)

    private class Target(val hue: Float, val saturation: Float, val value: Float)

    // Hues sampled from the fabric of the app's own chasuble artwork (drawable/*_chausable):
    // green #084C12, violet #520658, rose #E15661, red #A2060B, white #E0D8D0, gold #D18D1D.
    // Saturation and value lift the dark base towards each fabric while keeping it dark enough
    // for the gold text.
    private fun target(c: LiturgicalColor): Target = when (c) {
        LiturgicalColor.GREEN -> Target(hue = 129f, saturation = 1.3f, value = 1.0f)
        LiturgicalColor.VIOLET -> Target(hue = 296f, saturation = 1.4f, value = 1.15f)
        LiturgicalColor.ROSE -> Target(hue = 350f, saturation = 0.72f, value = 2.1f)
        LiturgicalColor.RED -> Target(hue = 358f, saturation = 1.45f, value = 1.15f)
        LiturgicalColor.WHITE -> Target(hue = 30f, saturation = 0.12f, value = 1.35f)
        LiturgicalColor.GOLD -> Target(hue = 37f, saturation = 1.3f, value = 1.2f)
    }

    fun shift(c: Color): Color {
        val t = target(color)
        val r = c.red; val g = c.green; val b = c.blue
        val mx = max(r, max(g, b)); val mn = min(r, min(g, b)); val d = mx - mn
        if (d < 1e-4f || mx <= 0f) return c
        val hue = when (mx) {
            r -> 60f * (((g - b) / d) % 6f)
            g -> 60f * ((b - r) / d + 2f)
            else -> 60f * ((r - g) / d + 4f)
        }.let { if (it < 0f) it + 360f else it }
        val sat = d / mx
        if (hue !in 80f..190f || sat < 0.08f) return c
        return hsv(t.hue, (sat * t.saturation).coerceIn(0f, 1f), (mx * t.value).coerceIn(0f, 1f), c.alpha)
    }

    private fun hsv(h: Float, s: Float, v: Float, alpha: Float): Color {
        val c = v * s
        val x = c * (1 - abs((h / 60f) % 2f - 1))
        val m = v - c
        val (r, g, b) = when ((h / 60f).toInt() % 6) {
            0 -> Triple(c, x, 0f)
            1 -> Triple(x, c, 0f)
            2 -> Triple(0f, c, x)
            3 -> Triple(0f, x, c)
            4 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }
        return Color(r + m, g + m, b + m, alpha)
    }
}

/** This colour in the day's liturgical tint (unchanged unless it is one of the app's greens). */
fun Color.liturgical(): Color = LiturgicalTint.shift(this)
