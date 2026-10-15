package com.ynotlabs.cathopedia.liturgical

import com.ynotlabs.cathopedia.resources.Res
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MassPart(
    val id: String,
    val title: String,
    val who: String = "",
    val rubric: String,
    /** Replaces [rubric] during Lent (e.g. the Gospel acclamation without Alleluia). */
    val rubricLent: String? = null,
    /** Public-domain text said or sung at this point. */
    val text: String? = null,
    val textNote: String? = null,
    /** Names the Roman Missal text that belongs here; shown as a pending slot until it is licensed. */
    val missal: String? = null,
    /** The lectionary reading shown at this point, as a [LectionaryKind] name. */
    val reading: String? = null,
    /** "gloria", "creed", "second_reading" or "sequence": the part appears only when it applies. */
    val condition: String? = null,
    val homily: Boolean = false,
    val intentions: Boolean = false,
    val preface: Boolean = false,
    val eucharisticPrayer: Boolean = false,
)

@Serializable
data class MassSection(val id: String, val title: String, val parts: List<MassPart>)

@Serializable
data class OrderOfMassText(
    val language: String,
    val note: String,
    val missalPending: String,
    val readingPending: String,
    val readingStudyNote: String = "",
    val homilyPending: String,
    val sections: List<MassSection>,
    val goodFriday: String,
    val holySaturday: String,
)

/** One part of today's Mass, with what the day supplies for it. */
data class PlannedPart(
    val section: MassSection,
    val part: MassPart,
    val rubric: String,
    val reading: LectionaryReading?,
)

/** The Mass of one day, laid out part by part. */
data class MassPlan(
    val day: OrdoDay,
    val celebration: OrdoCelebration,
    val readings: MassReadingSet?,
    val season: LiturgicalSeason,
    val parts: List<PlannedPart>,
    /** Set instead of [parts] when no Mass is celebrated (Good Friday, Holy Saturday). */
    val noMassNote: String? = null,
)

/**
 * The Order of Mass applied to a day of the Ordo: which optional parts are said (Gloria, Creed,
 * second reading, Sequence) and which readings fill the Liturgy of the Word.
 */
object OrderOfMass {
    private val json = Json { ignoreUnknownKeys = true }
    private val cache = mutableMapOf<String, OrderOfMassText>()

    /**
     * Cathopedia's own guidance (part names, rubrics, notes) in [language], falling back to English.
     * The Missal's texts themselves arrive per language with their licences.
     */
    suspend fun text(language: String = "en"): OrderOfMassText = cache.getOrPut(language) {
        runCatching { load(language) }.getOrNull() ?: cache.getOrPut("en") { load("en") }
    }

    private suspend fun load(language: String): OrderOfMassText = json.decodeFromString<OrderOfMassText>(
        Res.readBytes("files/content/ordo/order_of_mass.$language.json").decodeToString(),
    )

    suspend fun plan(day: OrdoDay, celebrationKey: String? = null, language: String = "en"): MassPlan? {
        val text = text(language)
        val celebration = day.celebrations.firstOrNull { it.key == celebrationKey } ?: day.principal ?: return null
        val season = LiturgicalCalendar.liturgicalDayFor(day.date).season
        val readings = OrdoCalendar.readings(day, celebration.key)

        val noMass = when (celebration.key) {
            "GoodFri" -> text.goodFriday
            "HolySaturday" -> text.holySaturday
            else -> null
        }
        if (noMass != null) {
            val wordParts = text.sections.first { it.id == "word" }.let { section ->
                section.parts.filter { it.reading != null }.mapNotNull { part ->
                    val reading = readings?.readings?.firstOrNull { it.kind.name == part.reading } ?: return@mapNotNull null
                    PlannedPart(section, part, part.rubric, reading)
                }
            }
            return MassPlan(day, celebration, readings, season, wordParts, noMassNote = noMass)
        }

        val parts = text.sections.flatMap { section ->
            section.parts.mapNotNull { part ->
                val reading = part.reading?.let { kind -> readings?.readings?.firstOrNull { it.kind.name == kind } }
                val include = when (part.condition) {
                    "gloria" -> saysGloria(day, celebration, season)
                    "creed" -> saysCreed(day, celebration)
                    "second_reading", "sequence" -> reading != null
                    else -> true
                }
                if (!include) return@mapNotNull null
                val rubric = if (season == LiturgicalSeason.LENT) part.rubricLent ?: part.rubric else part.rubric
                PlannedPart(section, part, rubric, reading)
            }
        }
        return MassPlan(day, celebration, readings, season, parts)
    }

    /**
     * GIRM 53: Sundays outside Advent and Lent, solemnities and feasts, the octaves of Christmas
     * and Easter, the Evening Mass of the Lord's Supper and the Easter Vigil.
     */
    /**
     * The highest rank also covers privileged weekdays (Ash Wednesday, Holy Week), so for it only
     * these celebrations count as solemnities.
     */
    private val topSolemnities = setOf("Christmas", "Epiphany", "Easter", "Ascension", "Pentecost")

    /**
     * GIRM 53: Sundays outside Advent and Lent, solemnities and feasts, the octaves of Christmas
     * and Easter, the Evening Mass of the Lord's Supper and the Easter Vigil.
     */
    fun saysGloria(day: OrdoDay, celebration: OrdoCelebration, season: LiturgicalSeason): Boolean {
        val key = celebration.key
        if (key in topSolemnities || key == "HolyThurs" || key == "EasterVigil" || key.endsWith("OctaveEaster")) return true
        if (celebration.rank in OrdoRank.FEAST..OrdoRank.SOLEMNITY) return true
        val d = day.date
        if ((d.month.ordinal == 11 && d.day >= 25) || (d.month.ordinal == 0 && d.day == 1)) return true
        return d.dayOfWeek == DayOfWeek.SUNDAY && season != LiturgicalSeason.ADVENT && season != LiturgicalSeason.LENT
    }

    /** GIRM 68: Sundays and solemnities; not on octave weekdays or at the Triduum Masses. */
    fun saysCreed(day: OrdoDay, celebration: OrdoCelebration): Boolean {
        val key = celebration.key
        if (key in setOf("HolyThurs", "HolyThursChrism", "EasterVigil")) return false
        return day.date.dayOfWeek == DayOfWeek.SUNDAY || key in topSolemnities ||
            celebration.rank == OrdoRank.SOLEMNITY
    }
}
