package prayercontent

import kotlinx.serialization.Serializable

/**
 * Mirrors com.ynotlabs.cathopedia.content.ContentSchema's PrayerContent /
 * PrayerLocalizedText (shared/src/commonMain) field-for-field. Duplicated here
 * rather than shared because `shared` has no JVM target buildSrc can compile
 * against — keep the two in sync by hand if either changes.
 */
@Serializable
data class PrayerLocalizedTextFile(
    val title: String,
    val subtitle: String? = null,
    val bodyMd: String,
    val about: String = "",
    val attribution: String? = null,
    val source: String,
)

@Serializable
data class PrayerContentFile(
    val id: String,
    val category: String,
    val sortOrder: Long,
    val isSequence: Boolean = false,
    /** Empty map is the "not yet sourced" skeleton state — see PrayerContentValidator. */
    val text: Map<String, PrayerLocalizedTextFile> = emptyMap(),
)

/**
 * PrayerCategory's fixed taxonomy (model/Prayer.kt), duplicated for the same
 * cross-module reason as above. There is no `prayer_category` table to
 * validate against — category has always been an enum, not a DB table.
 */
val KNOWN_PRAYER_CATEGORIES: Set<String> = setOf(
    "everyday",
    "marian",
    "holy-spirit",
    "eucharistic",
    "saints",
    "penitential",
    "sequences",
    "occasional",
)

/** Every prayer currently allowed to opt into the sequence/novena catalogue. */
val KNOWN_SEQUENCE_SLUGS: Set<String> = setOf(
    "holy-rosary",
    "divine-mercy-chaplet",
    "seven-sorrows",
    "novena-to-the-holy-spirit",
    "st-andrew-christmas-novena",
    "novena-to-st-joseph",
    "novena-to-the-sacred-heart",
    "novena-to-our-lady-of-perpetual-help",
    "novena-to-the-immaculate-conception",
    "novena-to-st-jude",
    "novena-to-st-anthony",
    "divine-mercy-novena",
    "novena-to-st-therese",
    "novena-to-our-lady-of-the-miraculous-medal",
    "novena-to-st-rita",
    "novena-to-st-michael",
    "novena-to-our-lady-of-guadalupe",
)

/** A prayer must have both to ship; `la` is optional. */
val REQUIRED_LANGUAGES: List<String> = listOf("en", "fr")
