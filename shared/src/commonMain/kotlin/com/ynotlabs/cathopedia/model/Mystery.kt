package com.ynotlabs.cathopedia.model

/**
 * The four sets of Rosary mysteries. A fixed taxonomy, like [PrayerCategory] —
 * [tag] is the exact string stored in MysteryEntity.mysterySet.
 */
enum class MysterySet(val tag: String) {
    JOYFUL("joyful"),
    SORROWFUL("sorrowful"),
    GLORIOUS("glorious"),
    LUMINOUS("luminous");

    companion object {
        fun fromTag(tag: String): MysterySet = entries.first { it.tag == tag }
    }
}

/** List-row shape — a decade's mystery as shown while picking/announcing it. */
data class MysterySummary(
    val id: String,
    val set: MysterySet,
    val sortOrder: Long,
    val title: String,
    val scriptureRef: String?,
    val fruit: String,
)

data class MysteryDetail(
    val id: String,
    val set: MysterySet,
    val sortOrder: Long,
    val scriptureRef: String?,
    val title: String,
    val fruit: String,
    /** Null until supplied separately (see content/README.md) — the UI shows the announcement and scripture reference alone when absent. */
    val meditation: String?,
)
