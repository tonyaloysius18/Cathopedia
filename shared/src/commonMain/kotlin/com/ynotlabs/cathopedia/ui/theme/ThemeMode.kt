package com.ynotlabs.cathopedia.ui.theme

enum class ThemeMode(val storageKey: String, val label: String) {
    SYSTEM("system", "System"),
    LIGHT("light", "Light"),
    DARK("dark", "Dark"),
    ;

    fun next(): ThemeMode = entries[(ordinal + 1) % entries.size]

    companion object {
        fun fromStorageKey(key: String?): ThemeMode = entries.find { it.storageKey == key } ?: SYSTEM
    }
}
