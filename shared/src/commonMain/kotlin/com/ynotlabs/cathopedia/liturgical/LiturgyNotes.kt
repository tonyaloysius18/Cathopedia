package com.ynotlabs.cathopedia.liturgical

import com.ynotlabs.cathopedia.resources.Res
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PrayerOfTheFaithful(
    /** Said by the celebrant to open the prayer. */
    val invitation: String,
    val response: String,
    val intentions: List<String>,
    /** The celebrant's concluding prayer. */
    val conclusion: String,
)

@Serializable
data class LiturgyReflection(val title: String, val body: String, val author: String = "Cathopedia")

/**
 * Cathopedia's own writing for one day's Mass: an introduction, the Prayer of the Faithful and a
 * reflection on the readings. Every note is reviewed by a priest before it is shown to users;
 * until [status] is "approved" it appears only in development builds.
 */
@Serializable
data class LiturgyNote(
    val celebration: String,
    val cycle: String,
    val status: String,
    val reviewedBy: String? = null,
    val reviewedOn: String? = null,
    val introduction: String,
    val prayerOfTheFaithful: PrayerOfTheFaithful,
    val reflection: LiturgyReflection,
) {
    val approved: Boolean get() = status == "approved"
}

object LiturgyNotes {
    private val json = Json { ignoreUnknownKeys = true }
    private val cache = mutableMapOf<String, LiturgyNote?>()

    /**
     * The note for [celebrationKey] in this day's cycle: Sunday cycle (A/B/C), then weekday cycle
     * (I/II), then one note for every year ("all"). Files live at
     * files/content/ordo/notes/en/<celebrationKey>_<cycle>.json.
     */
    suspend fun noteFor(day: OrdoDay, celebrationKey: String): LiturgyNote? {
        val cycles = listOf(day.sundayCycle.toString(), if (day.weekdayCycle == 1) "I" else "II", "all")
        return cycles.firstNotNullOfOrNull { load("${celebrationKey}_$it") }
    }

    private suspend fun load(name: String): LiturgyNote? = cache.getOrPut(name) {
        runCatching {
            json.decodeFromString<LiturgyNote>(Res.readBytes("files/content/ordo/notes/en/$name.json").decodeToString())
        }.getOrNull()
    }
}
