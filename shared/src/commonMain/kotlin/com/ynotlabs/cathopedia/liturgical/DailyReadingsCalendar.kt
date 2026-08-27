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
    val text: String,
    val translation: String,
)

@Serializable
private data class DailyReadingsIndex(
    val calendar: String,
    val year: Int,
    val source: String,
    val days: List<DailyMassReadings>,
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
    private var cachedDays: Map<String, DailyMassReadings>? = null

    suspend fun readingsFor(date: LocalDate): DailyMassReadings? {
        if (date.year != BUNDLED_YEAR) return null

        val days = cachedDays ?: run {
            val index = json.decodeFromString<DailyReadingsIndex>(
                Res.readBytes(RESOURCE_PATH).decodeToString(),
            )
            index.days.associateBy(DailyMassReadings::date).also { cachedDays = it }
        }
        return days[date.toString()]
    }
}
