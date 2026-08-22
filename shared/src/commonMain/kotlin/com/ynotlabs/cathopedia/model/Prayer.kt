package com.ynotlabs.cathopedia.model

/**
 * ContentSearch.entityType tag for prayers. Not a [ContentType] member — the
 * search FTS table's entityType column is a plain TEXT tag, not FK'd to that
 * enum, so prayers can share the table without joining the encyclopedia's
 * seven-type taxonomy (see the search-reuse note in CathopediaRepository).
 */
const val PRAYER_SEARCH_ENTITY_TYPE = "prayer"

/**
 * The eight prayer categories from the Prayers brief. A fixed taxonomy, so —
 * like [ContentType] — this is a Kotlin enum rather than a database table;
 * [tag] is the exact string stored in PrayerEntity.category. Display names
 * are chrome text and live in Strings.kt, not here.
 */
enum class PrayerCategory(val tag: String) {
    EVERYDAY("everyday"),
    MARIAN("marian"),
    HOLY_SPIRIT("holy-spirit"),
    EUCHARISTIC("eucharistic"),
    SAINTS("saints"),
    PENITENTIAL("penitential"),
    SEQUENCES("sequences"),
    OCCASIONAL("occasional");

    companion object {
        fun fromTag(tag: String): PrayerCategory = entries.first { it.tag == tag }
    }
}

/** List-row shape for the prayer library and prayer search results. */
data class PrayerSummary(
    val id: String,
    val title: String,
    val subtitle: String?,
    val category: PrayerCategory,
    val isSequence: Boolean,
)

data class PrayerDetail(
    val id: String,
    val category: PrayerCategory,
    val isSequence: Boolean,
    val title: String,
    val subtitle: String?,
    val bodyMd: String,
    val attribution: String?,
    val source: String,
    /** Every language this prayer has a row for — drives the detail screen's language toggle. */
    val availableLanguages: List<String>,
)

data class PrayerUserState(
    val prayerId: String,
    val isFavorite: Boolean,
    val lastOpenedAt: Long?,
    val timesPrayed: Long,
)
