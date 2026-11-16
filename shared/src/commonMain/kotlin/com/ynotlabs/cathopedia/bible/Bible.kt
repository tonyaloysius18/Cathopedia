package com.ynotlabs.cathopedia.bible

import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class BibleBook(
    /** The WEB file's code (GEN, ESG, JOH…): the same codes the Mass readings use. */
    val code: String,
    val name: String,
    val short: String,
    /** "ot" or "nt". */
    val testament: String,
    /** pentateuch, historical, wisdom, prophets, gospels, acts, pauline, catholic_letters, revelation. */
    val group: String,
    /** Number of verses in each chapter, chapter 1 first. */
    val verses: List<Int>,
) {
    val chapterCount: Int get() = verses.size
}

@Serializable
data class BibleIndex(val translation: String, val credit: String, val books: List<BibleBook>)

data class BibleVerse(val number: Int, val text: String)

data class BibleRef(val book: String, val chapter: Int, val verse: Int? = null) {
    /** "JOH 3" or "JOH 3:16", the stored form of bookmarks and the last-read place. */
    fun key(): String = if (verse == null) "$book $chapter" else "$book $chapter:$verse"

    companion object {
        fun parse(key: String): BibleRef? {
            val m = Regex("""^(\w{3}) (\d+)(?::(\d+))?$""").find(key.trim()) ?: return null
            return BibleRef(m.groupValues[1], m.groupValues[2].toInt(), m.groupValues[3].toIntOrNull())
        }
    }
}

data class BibleSearchHit(val book: BibleBook, val chapter: Int, val verse: BibleVerse)

@Serializable
private data class ChapterFile(
    val v: List<Int>,
    val t: List<String>,
    /** Verses that begin a new paragraph or poetry line (from the WEB's own markup); the first verse is implied. */
    val p: List<Int> = emptyList(),
)

@Serializable
private data class BookFile(val code: String, val chapters: List<ChapterFile>)

/**
 * The bundled Bible: the 73 books of the Catholic canon from the public-domain World English Bible,
 * built by `tools/bible/build_bible.py` into files/bible/web/. The text must never be edited
 * (the condition for using the name "World English Bible").
 */
object Bible {
    private const val DIR = "files/bible/web"
    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private var index: BibleIndex? = null
    private val books = mutableMapOf<String, BookFile>()

    suspend fun index(): BibleIndex = mutex.withLock {
        index ?: withContext(Dispatchers.Default) {
            json.decodeFromString<BibleIndex>(Res.readBytes("$DIR/index.json").decodeToString())
        }.also { index = it }
    }

    suspend fun book(code: String): BibleBook? = index().books.find { it.code == code }

    suspend fun chapter(code: String, chapter: Int): List<BibleVerse> {
        val file = bookFile(code) ?: return emptyList()
        val ch = file.chapters.getOrNull(chapter - 1) ?: return emptyList()
        return ch.v.zip(ch.t) { n, t -> BibleVerse(n, t) }
    }

    /** The chapter's verses split into paragraphs, as the WEB lays them out. */
    suspend fun paragraphs(code: String, chapter: Int): List<List<BibleVerse>> {
        val file = bookFile(code) ?: return emptyList()
        val ch = file.chapters.getOrNull(chapter - 1) ?: return emptyList()
        val starts = ch.p.toSet()
        val out = mutableListOf<MutableList<BibleVerse>>()
        ch.v.zip(ch.t) { n, t -> BibleVerse(n, t) }.forEach { verse ->
            if (out.isEmpty() || verse.number in starts) out += mutableListOf<BibleVerse>()
            out.last() += verse
        }
        return out
    }

    /**
     * Every verse containing all the words of [query] (case-insensitive), in canonical order,
     * stopping at [limit]. Searches the whole Bible, loading books as it goes.
     */
    suspend fun search(query: String, limit: Int = 200): List<BibleSearchHit> = withContext(Dispatchers.Default) {
        val words = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (words.isEmpty() || query.trim().length < 2) return@withContext emptyList()
        val hits = mutableListOf<BibleSearchHit>()
        for (book in index().books) {
            val file = bookFile(book.code) ?: continue
            file.chapters.forEachIndexed { i, ch ->
                ch.t.forEachIndexed { j, text ->
                    val lower = text.lowercase()
                    if (words.all { lower.contains(it) }) {
                        hits += BibleSearchHit(book, i + 1, BibleVerse(ch.v[j], text))
                        if (hits.size >= limit) return@withContext hits
                    }
                }
            }
        }
        hits
    }

    /** The chapter after (or, with [step] = -1, before) this one, crossing into the next book. */
    suspend fun neighbour(code: String, chapter: Int, step: Int): BibleRef? {
        val all = index().books
        val i = all.indexOfFirst { it.code == code }.takeIf { it >= 0 } ?: return null
        val target = chapter + step
        if (target in 1..all[i].chapterCount) return BibleRef(code, target)
        val next = all.getOrNull(i + step) ?: return null
        return BibleRef(next.code, if (step > 0) 1 else next.chapterCount)
    }

    private suspend fun bookFile(code: String): BookFile? {
        mutex.withLock { books[code] }?.let { return it }
        val loaded = withContext(Dispatchers.Default) {
            runCatching {
                json.decodeFromString<BookFile>(Res.readBytes("$DIR/$code.json").decodeToString())
            }.getOrNull()
        } ?: return null
        mutex.withLock { books[code] = loaded }
        return loaded
    }
}

/** Where the reader stopped, bookmarks and text size, kept in the app's preferences table. */
class BibleStore(private val repository: CathopediaRepository) {
    fun lastRead(): BibleRef? = repository.getPreference(LAST_READ)?.let(BibleRef::parse)

    fun setLastRead(ref: BibleRef) = repository.setPreference(LAST_READ, BibleRef(ref.book, ref.chapter).key())

    fun bookmarks(): List<BibleRef> =
        repository.getPreference(BOOKMARKS).orEmpty().split('\n').mapNotNull(BibleRef::parse)

    fun isBookmarked(ref: BibleRef): Boolean = bookmarks().any { it == ref }

    /** Adds the verse to the top of the list, or removes it; returns whether it is now bookmarked. */
    fun toggleBookmark(ref: BibleRef): Boolean {
        val current = bookmarks()
        val now = if (ref in current) current - ref else listOf(ref) + current
        repository.setPreference(BOOKMARKS, now.joinToString("\n") { it.key() })
        return ref in now
    }

    /** Chapters of [book] the reader has finished (scrolled to the end of). */
    fun readChapters(book: String): Set<Int> =
        repository.getPreference(READ).orEmpty().split('\n').mapNotNull(BibleRef::parse)
            .filter { it.book == book }.map { it.chapter }.toSet()

    /** Every finished chapter, by book code. */
    fun allRead(): Map<String, Set<Int>> =
        repository.getPreference(READ).orEmpty().split('\n').mapNotNull(BibleRef::parse)
            .groupBy({ it.book }, { it.chapter }).mapValues { it.value.toSet() }

    fun markRead(ref: BibleRef) {
        val key = BibleRef(ref.book, ref.chapter).key()
        val current = repository.getPreference(READ).orEmpty().split('\n').filter { it.isNotBlank() }
        if (key !in current) repository.setPreference(READ, (current + key).joinToString("\n"))
    }

    fun fontScale(): Float = repository.getPreference(FONT_SCALE)?.toFloatOrNull() ?: 1f

    fun setFontScale(scale: Float) = repository.setPreference(FONT_SCALE, scale.toString())

    private companion object {
        const val LAST_READ = "bible_last_read"
        const val BOOKMARKS = "bible_bookmarks"
        const val FONT_SCALE = "bible_font_scale"
        const val READ = "bible_read"
    }
}
