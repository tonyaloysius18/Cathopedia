package com.ynotlabs.cathopedia.liturgical

import com.ynotlabs.cathopedia.resources.Res
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** [stanza] counts the reference's comma-separated parts, e.g. a psalm's stanzas. */
data class ReadingVerse(val chapter: Int, val verse: Int, val text: String, val stanza: Int = 0)

data class ReadingText(
    val verses: List<ReadingVerse>,
    /** The reference begins or ends mid-verse; whole verses are shown. */
    val partial: Boolean,
    val translation: String,
)

@Serializable
private data class ReadingCitation(val r: List<String>, val p: Boolean = false)

@Serializable
private data class ReadingTextsFile(
    val translation: String,
    val verses: Map<String, String>,
    val citations: Map<String, ReadingCitation>,
)

/**
 * Bundled study text for every lectionary reference, from the public-domain World English Bible.
 * Built by `tools/liturgy/build_reading_texts.py`. A licensed Lectionary replaces it in Phase 2.
 */
object ReadingTexts {
    private const val PATH = "files/content/ordo/reading_texts_web.json"
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var file: ReadingTextsFile? = null

    suspend fun textFor(citation: String): ReadingText? {
        val data = load()
        val entry = data.citations[citation] ?: return null
        // Each range is "BOOK chapter:first-last", e.g. "MAT 22:15-21".
        // A verse split between stanzas ("2-3a, 3b-4") is shown once, in the first of them.
        val seen = mutableSetOf<Pair<Int, Int>>()
        val verses = entry.r.flatMapIndexed { stanza, range ->
            val (book, rest) = range.split(' ')
            val (chapterText, span) = rest.split(':')
            val chapter = chapterText.toInt()
            val (first, last) = span.split('-').map(String::toInt)
            (first..last).mapNotNull { v ->
                if (!seen.add(chapter to v)) return@mapNotNull null
                data.verses["$book $chapter:$v"]?.let { ReadingVerse(chapter, v, it, stanza) }
            }
        }
        return ReadingText(verses, entry.p, data.translation).takeIf { verses.isNotEmpty() }
    }

    private suspend fun load(): ReadingTextsFile = mutex.withLock {
        file ?: json.decodeFromString<ReadingTextsFile>(Res.readBytes(PATH).decodeToString())
            .also { file = it }
    }
}
