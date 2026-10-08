package com.ynotlabs.cathopedia.liturgical

import com.ynotlabs.cathopedia.resources.Res
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** The role a Scripture reference has in the Mass lectionary. */
@Serializable
enum class ReadingKind {
    FIRST_READING,
    SECOND_READING,
    GOSPEL,
}

/**
 * A citation-first Bible target. The display citation is deliberately kept intact so alternative
 * passages remain accurate; it can become the input to the Bible router when that screen lands.
 */
@Serializable
data class ScriptureReference(
    val kind: ReadingKind,
    val citation: String,
)

@Serializable
data class DailyMassReadings(
    val date: String,
    val title: String,
    val readings: List<ScriptureReference>,
    val featuredVerse: DailyFeaturedVerse? = null,
)

@Serializable
data class DailyFeaturedVerse(
    val citation: String,
    /** Blank when no public-domain Bible exists in the requested language; show the citation alone. */
    val text: String,
    val translation: String,
    /** Public-domain renderings keyed by language code, added by `tools/localize_daily_readings.py`. */
    val localized: Map<String, LocalizedVerse> = emptyMap(),
)

@Serializable
data class LocalizedVerse(
    val text: String,
    val translation: String,
)

@Serializable
private data class DailyReadingsIndex(
    val calendar: String,
    val year: Int,
    val source: String,
    val days: List<DailyMassReadings>,
    /** `titleTranslations[lang][englishTitle]`, added by `tools/localize_daily_readings.py`. */
    val titleTranslations: Map<String, Map<String, String>> = emptyMap(),
)

/**
 * Offline daily Mass references for the calendar currently bundled with the app.
 *
 * The annual JSON is generated from the official USCCB calendar by
 * `tools/generate_usccb_readings.py`. It contains citations only, not copyrighted lectionary text.
 */
object DailyReadingsCalendar {
    private const val BUNDLED_YEAR = 2026
    private const val RESOURCE_PATH = "files/content/daily_readings_2026.json"

    private val json = Json { ignoreUnknownKeys = true }
    private var cachedIndex: DailyReadingsIndex? = null
    private var cachedDays: Map<String, DailyMassReadings>? = null

    /**
     * The day's readings with the title and featured verse in [language]. English Scripture is
     * never shown in place of a missing translation: the verse text is left blank instead.
     */
    suspend fun readingsFor(date: LocalDate, language: String = "en"): DailyMassReadings? {
        if (date.year != BUNDLED_YEAR) return null

        val index = cachedIndex ?: json.decodeFromString<DailyReadingsIndex>(
            Res.readBytes(RESOURCE_PATH).decodeToString(),
        ).also { cachedIndex = it }
        val days = cachedDays ?: index.days.associateBy(DailyMassReadings::date).also { cachedDays = it }
        val day = days[date.toString()] ?: return null
        if (language == "en") return day

        val verse = day.featuredVerse?.let { verse ->
            val localized = verse.localized[language]
            verse.copy(text = localized?.text.orEmpty(), translation = localized?.translation.orEmpty())
        }
        return day.copy(
            title = index.titleTranslations[language]?.get(day.title) ?: day.title,
            featuredVerse = verse,
        )
    }
}
