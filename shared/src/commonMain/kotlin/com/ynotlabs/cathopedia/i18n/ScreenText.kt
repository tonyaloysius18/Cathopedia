package com.ynotlabs.cathopedia.i18n

import androidx.compose.runtime.compositionLocalOf

/**
 * Text for the self-contained Kotlin screens (Altar, Monstrance, Procession,
 * Sacraments, Stations…), loaded once per language from the `screen.*` keys in
 * content/strings/screens.<lang>.json (and stations.<lang>.json). Lives in the
 * content pipeline rather than [Strings] so tools/translate covers it and a
 * missing key falls back to English per key, like every hub string.
 */
class ScreenText(private val values: Map<String, String>) {
    operator fun get(key: String): String = values[key].orEmpty()

    companion object {
        const val PREFIX = "screen."
        val Empty = ScreenText(emptyMap())
    }
}

val LocalScreenText = compositionLocalOf { ScreenText.Empty }
