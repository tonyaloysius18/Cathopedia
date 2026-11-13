package com.ynotlabs.cathopedia.liturgical

import androidx.compose.runtime.compositionLocalOf
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import com.ynotlabs.cathopedia.data.RemoteCache
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.time.Clock

/** One reading from AELF, as plain text ready to show. */
data class AelfPassage(
    val reference: String,
    val intro: String,
    val title: String,
    val refrain: String,
    val text: String,
)

@Serializable
internal data class AelfLecture(
    val type: String = "",
    val ref: String? = null,
    val titre: String? = null,
    val contenu: String? = null,
    val intro_lue: String? = null,
    val refrain_psalmique: String? = null,
    val verset_evangile: String? = null,
    val ref_verset: String? = null,
)

@Serializable
internal data class AelfMassEntry(val nom: String = "", val lectures: List<AelfLecture> = emptyList())

@Serializable
private data class AelfMassesFile(val messes: List<AelfMassEntry> = emptyList())

/**
 * The AELF Mass that corresponds to the day's plan. [aligned] means its first reading and Gospel
 * both match ours, so its psalm and acclamation are the right ones even when the reference is
 * written or numbered differently.
 */
class AelfMass internal constructor(private val lectures: List<AelfLecture>, private val aligned: Boolean) {
    fun passage(kind: LectionaryKind, citation: String, short: Boolean): AelfPassage? = when (kind) {
        LectionaryKind.ACCLAMATION -> lectures.firstOrNull { it.type == "evangile" && !it.verset_evangile.isNullOrBlank() }
            ?.takeIf { aligned }
            ?.let { AelfPassage(cleanReference(it.ref_verset), "", "", "", aelfHtmlToText(it.verset_evangile)) }
        LectionaryKind.SEQUENCE -> lectures.firstOrNull { it.type == "sequence" && !it.contenu.isNullOrBlank() }?.let(::toPassage)
        LectionaryKind.PSALM -> {
            val psalms = lectures.filter { it.type == "psaume" || it.type == "cantique" }
            val key = citationKey(citation)
            psalms.firstOrNull { key in referenceKeys(it.ref, it.type) }?.let(::toPassage)
                ?: psalms.singleOrNull()?.takeIf { aligned }?.let(::toPassage)
        }
        LectionaryKind.GOSPEL, LectionaryKind.PROCESSION_GOSPEL -> pick(lectures.filter { it.type == "evangile" }, citation, short)
        else -> pick(lectures.filter { it.type.startsWith("lecture") || it.type == "epitre" }, citation, short)
    }

    /** Same book and chapter as ours; AELF lists a long and a short form as separate entries. */
    private fun pick(pool: List<AelfLecture>, citation: String, short: Boolean): AelfPassage? {
        val key = citationKey(citation)
        val same = pool.filter { key in referenceKeys(it.ref, it.type) }
        val chosen = if (short) same.minByOrNull { it.contenu.orEmpty().length } else same.maxByOrNull { it.contenu.orEmpty().length }
        return chosen?.let(::toPassage)
    }

    private fun toPassage(l: AelfLecture) = AelfPassage(
        reference = cleanReference(l.ref),
        intro = l.intro_lue.orEmpty(),
        title = aelfHtmlToText(l.titre),
        refrain = aelfHtmlToText(l.refrain_psalmique),
        text = aelfHtmlToText(l.contenu),
    )
}

/** The day's AELF Mass on the Mass screen; null outside French, offline or when it doesn't match. */
val LocalAelfMass = compositionLocalOf<AelfMass?> { null }

/** One titled block of an Hour. */
data class AelfHourSection(val heading: String, val reference: String, val text: String, val credit: String)

data class AelfHour(val dayName: String, val sections: List<AelfHourSection>)

/**
 * French readings and Liturgy of the Hours from AELF's public API (https://api.aelf.org), which
 * AELF allows for free, non-commercial use with the source credited (email to the publisher, 2026).
 * The texts are never bundled. AELF allows copies kept on the device for offline reading as long as
 * they are not kept more than a few days (email to the publisher, Nov 2026): each response is saved
 * with the time it was downloaded and deleted after [KEEP_DAYS], and [prefetch] fetches the coming
 * days while online. Callers fall back when this returns null.
 */
object AelfTexts {
    const val CREDIT = "Textes liturgiques © AELF, www.aelf.org"
    private const val API = "https://api.aelf.org/v1/"
    private const val ZONE = "france"
    private const val MAX_CACHED = 40
    /** AELF: "cette autorisation ne doit pas excéder quelques jours". */
    const val KEEP_DAYS = 3
    private const val KEEP_MILLIS = KEEP_DAYS * 24L * 60 * 60 * 1000
    private const val DIR = "aelf"
    private var prefetched: LocalDate? = null

    private val json = Json { ignoreUnknownKeys = true }
    private val mutex = Mutex()
    private val memory = LinkedHashMap<String, String>()

    fun isAvailable(language: String) = language == "fr"

    /** The AELF Mass matching [plan], or null if unavailable or if AELF's day differs from ours. */
    suspend fun massFor(plan: MassPlan, language: String): AelfMass? {
        if (!isAvailable(language)) return null
        val readings = plan.readings?.readings ?: return null
        // AELF files the Easter Vigil under Easter Sunday.
        val date = if (plan.celebration.key == "EasterVigil") plan.day.date.plus(1, DateTimeUnit.DAY) else plan.day.date
        val body = fetch("messes/$date/$ZONE") ?: return null
        val masses = runCatching { json.decodeFromString<AelfMassesFile>(body).messes }.getOrNull() ?: return null
        return matchMass(masses, readings)
    }

    /** One of [HOURS]' offices, or null offline or outside the dates AELF has published. */
    suspend fun hour(office: String, date: LocalDate): AelfHour? {
        val body = fetch("$office/$date/$ZONE") ?: return null
        return runCatching { parseHour(body, office) }.getOrNull()
    }

    /**
     * Downloads the Mass and every Hour for today and the next [KEEP_DAYS] - 1 days, so they can be
     * read offline; at most once a day. Old copies are deleted first.
     */
    suspend fun prefetch(today: LocalDate, language: String) {
        if (!isAvailable(language)) return
        if (mutex.withLock { prefetched == today.also { prefetched = it } }) return
        withContext(Dispatchers.IO) { purgeExpired() }
        for (offset in 0 until KEEP_DAYS) {
            val date = today.plus(offset, DateTimeUnit.DAY)
            if (fetch("messes/$date/$ZONE") == null) return // offline or past AELF's published dates
            for ((office, _) in HOURS) fetch("$office/$date/$ZONE")
        }
    }

    private fun file(path: String): Path? = RemoteCache.directory?.let { Path(it, DIR, path.replace('/', '_') + ".json") }

    /** A saved copy is "<download time in ms>\n<body>"; null when missing or older than [KEEP_DAYS]. */
    private fun readSaved(path: String): String? = runCatching {
        val f = file(path)?.takeIf { SystemFileSystem.exists(it) } ?: return null
        val text = SystemFileSystem.source(f).buffered().use { it.readString() }
        val saved = text.substringBefore('\n').toLong()
        if (Clock.System.now().toEpochMilliseconds() - saved > KEEP_MILLIS) {
            SystemFileSystem.delete(f, mustExist = false)
            null
        } else text.substringAfter('\n')
    }.getOrNull()

    private fun save(path: String, body: String) {
        runCatching {
            val f = file(path) ?: return
            f.parent?.let { SystemFileSystem.createDirectories(it) }
            val part = Path(f.toString() + ".part")
            SystemFileSystem.sink(part).buffered().use { it.writeString("${Clock.System.now().toEpochMilliseconds()}\n$body") }
            SystemFileSystem.atomicMove(part, f)
        }
    }

    private fun purgeExpired() {
        runCatching {
            val dir = RemoteCache.directory?.let { Path(it, DIR) }?.takeIf { SystemFileSystem.exists(it) } ?: return
            SystemFileSystem.list(dir).forEach { f -> readSaved(f.name.removeSuffix(".json").replace('_', '/')) }
        }
    }

    private suspend fun fetch(path: String): String? {
        mutex.withLock { memory[path] }?.let { return it }
        withContext(Dispatchers.IO) { readSaved(path) }?.let { saved ->
            mutex.withLock { memory[path] = saved }
            return saved
        }
        val body = withContext(Dispatchers.IO) {
            runCatching {
                val client = HttpClient { install(HttpTimeout) { requestTimeoutMillis = 15_000 } }
                try {
                    val response = client.get(API + path)
                    if (response.status.isSuccess()) response.bodyAsText() else null
                } finally {
                    client.close()
                }
            }.getOrNull()
        } ?: return null
        withContext(Dispatchers.IO) { save(path, body) }
        mutex.withLock {
            memory[path] = body
            if (memory.size > MAX_CACHED) memory.remove(memory.keys.first())
        }
        return body
    }

    internal fun parseHour(body: String, office: String): AelfHour {
        val root = json.parseToJsonElement(body).jsonObject
        val day = root["informations"]?.jsonObject?.get("jour_liturgique_nom")?.jsonPrimitive?.contentOrNull.orEmpty()
        val sections = root[office]?.jsonObject.orEmpty().mapNotNull { (key, value) ->
            when (value) {
                is JsonPrimitive -> {
                    val text = aelfHtmlToText(value.contentOrNull)
                    // "titre_patristique" is the heading of the text that follows; "notre_pere" just names the prayer.
                    if (text.isBlank() || key == "titre_patristique") null
                    else if (key == "notre_pere") AelfHourSection("Notre Père", "", "", "")
                    else if (key == "texte_patristique") AelfHourSection(
                        aelfHtmlToText(root[office]?.jsonObject?.get("titre_patristique")?.jsonPrimitive?.contentOrNull)
                            .ifBlank { "Lecture patristique" }, "", text, "",
                    )
                    else AelfHourSection(hourHeading(key), "", text, "")
                }
                is JsonObject -> {
                    val field = { name: String -> value[name]?.let { (it as? JsonPrimitive)?.contentOrNull }.orEmpty() }
                    val text = aelfHtmlToText(field("texte"))
                    if (text.isBlank()) null else AelfHourSection(
                        heading = aelfHtmlToText(field("titre")).ifBlank { hourHeading(key) },
                        reference = cleanReference(field("reference")),
                        text = text,
                        credit = listOf(field("auteur"), field("editeur")).filter(String::isNotBlank).joinToString(" · "),
                    )
                }
                is JsonArray -> null
            }
        }
        return AelfHour(day, sections)
    }

    private fun hourHeading(key: String): String = when {
        key == "introduction" -> "Introduction"
        key == "antienne_invitatoire" -> "Antienne invitatoire"
        key == "psaume_invitatoire" -> "Psaume invitatoire"
        key == "hymne" -> "Hymne"
        key == "hymne_mariale" -> "Hymne à la Vierge"
        key.startsWith("antienne") -> "Antienne"
        key.startsWith("psaume") -> "Psaume"
        key == "pericope" -> "Parole de Dieu"
        key == "lecture" -> "Lecture"
        key.startsWith("repons") -> "Répons"
        key == "verset_psaume" -> "Verset"
        key == "te_deum" -> "Te Deum"
        key.startsWith("cantique") -> "Cantique"
        key == "intercession" -> "Intercession"
        key == "oraison" -> "Oraison"
        key == "benediction" -> "Bénédiction"
        else -> key.replace('_', ' ').replaceFirstChar { it.uppercase() }
    }
}

/** office id in the API to its French name, in the order of the day. */
val HOURS = listOf(
    "lectures" to "Lectures",
    "laudes" to "Laudes",
    "tierce" to "Tierce",
    "sexte" to "Sexte",
    "none" to "None",
    "vepres" to "Vêpres",
    "complies" to "Complies",
)

/** The Mass whose first reading and Gospel match [readings]; failing that, whose Gospel does. */
internal fun matchMass(masses: List<AelfMassEntry>, readings: List<LectionaryReading>): AelfMass? {
    fun keys(kind: LectionaryKind) = readings.filter { it.kind == kind }.flatMap { r -> r.options.map { citationKey(it.citation) } }
    val gospels = keys(LectionaryKind.GOSPEL)
    val firsts = keys(LectionaryKind.FIRST_READING)
    fun matches(mass: AelfMassEntry, wanted: List<Pair<String, Int>>, gospel: Boolean) = mass.lectures.any { l ->
        val isGospel = l.type == "evangile"
        val isReading = l.type.startsWith("lecture") || l.type == "epitre"
        (if (gospel) isGospel else isReading) && referenceKeys(l.ref, l.type).any { it in wanted }
    }
    val withGospel = masses.filter { matches(it, gospels, gospel = true) }
    val both = withGospel.firstOrNull { matches(it, firsts, gospel = false) }
    return when {
        both != null -> AelfMass(both.lectures, aligned = true)
        withGospel.isNotEmpty() -> AelfMass(withGospel.first().lectures, aligned = false)
        else -> null
    }
}

private val OUR_BOOK_TO_AELF = mapOf(
    "Nm" to "Nb", "Jgs" to "Jg", "Ru" to "Rt", "1 Sm" to "1 S", "2 Sm" to "2 S", "1 Kgs" to "1 R", "2 Kgs" to "2 R",
    "1 Chr" to "1 Ch", "2 Chr" to "2 Ch", "Ezr" to "Esd", "Neh" to "Ne", "1 Mc" to "1 M", "2 Mc" to "2 M",
    "Prv" to "Pr", "Eccl" to "Qo", "Sg" to "Ct", "Wis" to "Sg", "Sir" to "Si", "Jer" to "Jr", "Lam" to "Lm", "Bar" to "Ba",
    "Hos" to "Os", "Ob" to "Ab", "Hb" to "Ha", "Zep" to "So", "Hg" to "Ag", "Zec" to "Za", "Mal" to "Ml",
    "Mk" to "Mc", "Lk" to "Lc", "Acts" to "Ac", "Rom" to "Rm", "1 Cor" to "1 Co", "2 Cor" to "2 Co", "Gal" to "Ga",
    "Eph" to "Ep", "Phil" to "Ph", "1 Thes" to "1 Th", "2 Thes" to "2 Th", "Ti" to "Tt", "Phlm" to "Phm", "Heb" to "He",
    "Jas" to "Jc", "1 Pt" to "1 P", "2 Pt" to "2 P", "Rv" to "Ap",
)
private val AELF_FULL_NAMES = mapOf("Isaïe" to "Is", "Philémon" to "Phm")
/** One-chapter books: ours cite "2 Jn 4-9", AELF "2 Jn 1a. 4-9", so the chapter isn't compared. */
private val ONE_CHAPTER = setOf("Ab", "Phm", "2 Jn", "3 Jn", "Jude")

private val OUR_CITATION = Regex("""^((?:\d )?[A-Za-z]+) (\d+)""")
private val AELF_PSALM_BOTH = Regex("""^(?:Ps ?)?(\d+) ?[A-Za-z]? ?\((\d+) ?[A-Za-z]?\)""")
private val AELF_PSALM_ONE = Regex("""^(?:Ps ?)?(\d+)\b""")
private val AELF_BOOK = Regex("""^(\d ?)?([A-Za-zÀ-ÿ]+) ?(\d+)""")

private fun bookKey(book: String, chapter: Int) = book to if (book in ONE_CHAPTER) 0 else chapter

/** Our citation ("Wis 6:12-16", "Ps 63:1-2") as AELF's book and chapter ("Sg", 6). */
internal fun citationKey(citation: String): Pair<String, Int> {
    val m = OUR_CITATION.find(citation.removePrefix("Cf. ").trim()) ?: return "" to -1
    val book = m.groupValues[1]
    return bookKey(OUR_BOOK_TO_AELF[book] ?: book, m.groupValues[2].toInt())
}

/**
 * AELF writes references many ways: "Ps 62 (63), 2", "97(98), 1", "18B (19), 8", "2Co 8, 7", "Isaïe 12, 2".
 * A psalm in brackets gives the number ours use; alone it is usually the older numbering, hence two keys.
 */
internal fun referenceKeys(reference: String?, type: String): List<Pair<String, Int>> {
    val r = (reference ?: return emptyList()).replace(' ', ' ').replace(Regex("""\s+"""), " ").trim()
        .removePrefix("[CANTIQUE]").trim().replace(Regex("""^[Cc]f\.? ?"""), "")
    val psalm = type == "psaume" || r.startsWith("Ps")
    if (psalm) {
        AELF_PSALM_BOTH.find(r)?.let { return listOf("Ps" to it.groupValues[2].toInt()) }
        AELF_PSALM_ONE.find(r)?.let { val n = it.groupValues[1].toInt(); return listOf("Ps" to n, "Ps" to n + 1) }
    }
    val m = AELF_BOOK.find(r) ?: return emptyList()
    val number = m.groupValues[1].trim()
    val name = AELF_FULL_NAMES[m.groupValues[2]] ?: m.groupValues[2]
    val book = if (number.isEmpty()) name else "$number $name"
    return listOf(bookKey(book, m.groupValues[3].toInt()))
}

private fun cleanReference(reference: String?): String =
    (reference ?: "").replace(' ', ' ').replace(Regex("""\s+"""), " ").trim().removePrefix("[CANTIQUE]").trim()

private val ENTITIES = mapOf(
    "nbsp" to " ", "amp" to "&", "lt" to "<", "gt" to ">", "quot" to "\"", "apos" to "'", "rsquo" to "’", "lsquo" to "‘",
    "laquo" to "«", "raquo" to "»", "ldquo" to "“", "rdquo" to "”", "hellip" to "…", "ndash" to "–", "mdash" to "—",
    "oelig" to "œ", "OElig" to "Œ", "eacute" to "é", "egrave" to "è", "ecirc" to "ê", "agrave" to "à", "acirc" to "â",
    "ccedil" to "ç", "icirc" to "î", "ocirc" to "ô", "ucirc" to "û", "ugrave" to "ù", "Eacute" to "É", "Agrave" to "À",
)

/** AELF's HTML as plain text: lines and paragraphs kept, tags and verse numbers dropped. */
internal fun aelfHtmlToText(html: String?): String {
    if (html.isNullOrBlank()) return ""
    val withBreaks = html
        .replace(Regex("""\s*\n\s*"""), " ")
        .replace(Regex("""<span class="verse_number">.*?</span>"""), "")
        .replace(Regex("""(?i)<br\s*/?>"""), "\n")
        .replace(Regex("""(?i)</p>"""), "\n\n")
    val noTags = withBreaks.replace(Regex("""<[^>]+>"""), "")
    val decoded = Regex("""&(#x?[0-9a-fA-F]+|[A-Za-z]+);""").replace(noTags) { m ->
        val e = m.groupValues[1]
        when {
            e.startsWith("#x") || e.startsWith("#X") -> e.drop(2).toIntOrNull(16)?.toChar()?.toString() ?: ""
            e.startsWith("#") -> e.drop(1).toIntOrNull()?.toChar()?.toString() ?: ""
            else -> ENTITIES[e] ?: m.value
        }
    }
    return decoded.replace(' ', ' ').split('\n').joinToString("\n") { it.trim().replace(Regex(""" {2,}"""), " ") }
        .replace(Regex("""\n{3,}"""), "\n\n").trim()
}
