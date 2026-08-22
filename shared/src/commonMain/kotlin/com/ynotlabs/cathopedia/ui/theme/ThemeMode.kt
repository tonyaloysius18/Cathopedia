package com.ynotlabs.cathopedia.ui.theme

import com.ynotlabs.cathopedia.i18n.Strings

enum class ThemeMode(val storageKey: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    ;

    fun next(): ThemeMode = entries[(ordinal + 1) % entries.size]

    companion object {
        fun fromStorageKey(key: String?): ThemeMode = entries.find { it.storageKey == key } ?: SYSTEM
    }
}

fun ThemeMode.label(s: Strings): String = when (this) {
    ThemeMode.SYSTEM -> s.themeSystem
    ThemeMode.LIGHT -> s.themeLight
    ThemeMode.DARK -> s.themeDark
}
