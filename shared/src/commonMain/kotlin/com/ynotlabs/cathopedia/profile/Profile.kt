package com.ynotlabs.cathopedia.profile

import com.ynotlabs.cathopedia.bible.Bible
import com.ynotlabs.cathopedia.bible.BibleRef
import com.ynotlabs.cathopedia.data.CathopediaRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * The user's own profile: name, state of life and the dates of their sacraments.
 * It is stored only in the app's local preferences on this phone and is never
 * sent anywhere (no account, no server), so the store listing's "no data
 * collected" answer stays true. The phone number is deliberately not collected.
 */
@Serializable
enum class StateOfLife { SINGLE, MARRIED, RELIGIOUS, DEACON, PRIEST;

    val hasWedding get() = this == MARRIED
    val hasOrdination get() = this == DEACON || this == PRIEST
    val hasProfession get() = this == RELIGIOUS
}

@Serializable
data class UserProfile(
    val firstName: String = "",
    val surname: String = "",
    val state: StateOfLife = StateOfLife.SINGLE,
    /** ISO dates ("1994-05-21"). */
    val birthDate: String? = null,
    val baptism: String? = null,
    val firstCommunion: String? = null,
    val confirmation: String? = null,
    val wedding: String? = null,
    val ordination: String? = null,
    val profession: String? = null,
    /** Patron saint; [feastDay] is "MM-DD", either from the saint or set by hand. */
    val patronId: String? = null,
    val patronType: String? = null,
    val patronName: String? = null,
    val feastDay: String? = null,
) {
    val isEmpty: Boolean get() = this == UserProfile()

    fun date(kind: OccasionKind): String? = when (kind) {
        OccasionKind.BIRTHDAY -> birthDate
        OccasionKind.BAPTISM -> baptism
        OccasionKind.FIRST_COMMUNION -> firstCommunion
        OccasionKind.CONFIRMATION -> confirmation
        OccasionKind.WEDDING -> wedding.takeIf { state.hasWedding }
        OccasionKind.ORDINATION -> ordination.takeIf { state.hasOrdination }
        OccasionKind.PROFESSION -> profession.takeIf { state.hasProfession }
        OccasionKind.FEAST_DAY -> null
    }

    fun withDate(kind: OccasionKind, iso: String?): UserProfile = when (kind) {
        OccasionKind.BIRTHDAY -> copy(birthDate = iso)
        OccasionKind.BAPTISM -> copy(baptism = iso)
        OccasionKind.FIRST_COMMUNION -> copy(firstCommunion = iso)
        OccasionKind.CONFIRMATION -> copy(confirmation = iso)
        OccasionKind.WEDDING -> copy(wedding = iso)
        OccasionKind.ORDINATION -> copy(ordination = iso)
        OccasionKind.PROFESSION -> copy(profession = iso)
        OccasionKind.FEAST_DAY -> this
    }
}

/** Order here is the order greetings are shown when several fall on one day. */
enum class OccasionKind(val key: String) {
    BIRTHDAY("birthday"),
    FEAST_DAY("feast_day"),
    BAPTISM("baptism"),
    FIRST_COMMUNION("first_communion"),
    CONFIRMATION("confirmation"),
    WEDDING("wedding"),
    ORDINATION("ordination"),
    PROFESSION("profession"),
}

/** A verse range from the WEB Bible; the text is read from the bundled Bible, never retyped. */
data class VerseRef(val book: String, val chapter: Int, val from: Int, val to: Int = from) {
    val bibleRef: BibleRef get() = BibleRef(book, chapter, from)
}

data class Occasion(
    val kind: OccasionKind,
    /** Years since the original date; null for a feast day, or when it is the date itself. */
    val years: Int?,
    val verse: VerseRef,
)

object ProfileGreetings {
    /** Three verses per occasion, rotated by year so the card changes each anniversary. */
    val verses: Map<OccasionKind, List<VerseRef>> = mapOf(
        OccasionKind.BIRTHDAY to listOf(VerseRef("PSA", 139, 14), VerseRef("PSA", 118, 24), VerseRef("NUM", 6, 24, 26)),
        OccasionKind.BAPTISM to listOf(VerseRef("ROM", 6, 4), VerseRef("GAL", 3, 27), VerseRef("1JO", 3, 1)),
        OccasionKind.FIRST_COMMUNION to listOf(VerseRef("JOH", 6, 51), VerseRef("JOH", 6, 35), VerseRef("JOH", 6, 56)),
        OccasionKind.CONFIRMATION to listOf(VerseRef("ACT", 1, 8), VerseRef("GAL", 5, 22, 23), VerseRef("2TI", 1, 7)),
        OccasionKind.FEAST_DAY to listOf(VerseRef("WIS", 3, 1), VerseRef("PSA", 92, 12), VerseRef("PHI", 1, 6)),
        OccasionKind.WEDDING to listOf(VerseRef("ECC", 4, 12), VerseRef("SOL", 8, 7), VerseRef("COL", 3, 14)),
        OccasionKind.ORDINATION to listOf(VerseRef("JER", 1, 5), VerseRef("PSA", 110, 4), VerseRef("JOH", 15, 16)),
        OccasionKind.PROFESSION to listOf(VerseRef("PSA", 16, 5), VerseRef("PSA", 27, 4), VerseRef("PSA", 73, 26)),
    )

    /** Every greeting that falls on [day], in [OccasionKind] order. */
    fun occasionsOn(profile: UserProfile, day: LocalDate): List<Occasion> =
        OccasionKind.entries.mapNotNull { kind ->
            val (month, dayOfMonth, years) = if (kind == OccasionKind.FEAST_DAY) {
                val (m, d) = parseMonthDay(profile.feastDay) ?: return@mapNotNull null
                Triple(m, d, null)
            } else {
                val date = parseDate(profile.date(kind)) ?: return@mapNotNull null
                // A date later this year, or in the future, has no anniversary yet.
                if (date.year >= day.year) return@mapNotNull null
                Triple(date.month.ordinal + 1, date.day, day.year - date.year)
            }
            if (!fallsOn(month, dayOfMonth, day)) return@mapNotNull null
            val list = verses.getValue(kind)
            Occasion(kind, years?.takeIf { it > 0 }, list[day.year.mod(list.size)])
        }

    /**
     * Whether a month/day recurs on [day]. 29 February is kept on 28 February
     * in years that have no 29th, so a leap-day birthday is never skipped.
     */
    fun fallsOn(month: Int, dayOfMonth: Int, day: LocalDate): Boolean {
        val m = day.month.ordinal + 1
        if (m != month) return false
        if (dayOfMonth == day.day) return true
        return month == 2 && dayOfMonth == 29 && day.day == 28 && !isLeap(day.year)
    }

    fun isLeap(year: Int) = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0

    fun parseDate(iso: String?): LocalDate? = iso?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

    fun parseMonthDay(mmdd: String?): Pair<Int, Int>? {
        val m = Regex("""^(\d{1,2})-(\d{1,2})$""").find(mmdd?.trim() ?: return null) ?: return null
        val month = m.groupValues[1].toInt()
        val d = m.groupValues[2].toInt()
        if (month !in 1..12 || d < 1) return null
        val max = if (month == 2) 29 else Month.entries[month - 1].let { if (it in setOf(Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER)) 30 else 31 }
        return if (d <= max) month to d else null
    }

    /** The verse text from the bundled WEB Bible, verses joined with a space. */
    suspend fun verseText(ref: VerseRef): String =
        Bible.chapter(ref.book, ref.chapter).filter { it.number in ref.from..ref.to }.joinToString(" ") { it.text }

    /** "Psalms 139:14" or "Numbers 6:24–26", using the reader's (English) book names. */
    suspend fun citation(ref: VerseRef): String {
        val name = Bible.book(ref.book)?.name ?: ref.book
        val verses = if (ref.to > ref.from) "${ref.from}–${ref.to}" else "${ref.from}"
        return "$name ${ref.chapter}:$verses"
    }
}

class ProfileStore(private val repository: CathopediaRepository) {
    fun load(): UserProfile = repository.getPreference(KEY)
        ?.let { runCatching { json.decodeFromString(UserProfile.serializer(), it) }.getOrNull() }
        ?: UserProfile()

    fun save(profile: UserProfile) = repository.setPreference(KEY, json.encodeToString(UserProfile.serializer(), profile))

    /** "Delete my profile": the preference is overwritten with an empty profile. */
    fun clear() = repository.setPreference(KEY, "")

    companion object {
        const val KEY = "user_profile"
        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }
    }
}

/**
 * "1 year" / "5 years" with the language's plural forms. Polish (and the other
 * Slavic-style rule) uses `years_few` for 2–4, 22–24…; every other language
 * just sets `years_few` to the same text as `years_other`.
 */
fun yearsLabel(years: Int, language: String, text: (String) -> String): String {
    val key = when {
        years == 1 -> "screen.profile.years_one"
        language == "pl" && years % 10 in 2..4 && years % 100 !in 12..14 -> "screen.profile.years_few"
        else -> "screen.profile.years_other"
    }
    return text(key).replace("{n}", years.toString())
}

/** "Happy Birthday, Tony!" — or "Happy Birthday!" when no first name is set. */
fun greetingTitle(kind: OccasionKind, profile: UserProfile, text: (String) -> String): String {
    val greeting = text("screen.profile.greet.${kind.key}")
    val name = profile.firstName.trim()
    return if (name.isEmpty()) text("screen.profile.greet_no_name").replace("{greeting}", greeting)
    else text("screen.profile.greet_with_name").replace("{greeting}", greeting).replace("{name}", name)
}

/** The line under the title; a feast day names the patron when one was chosen. */
fun greetingMessage(kind: OccasionKind, profile: UserProfile, text: (String) -> String): String {
    val patron = profile.patronName?.takeIf { kind == OccasionKind.FEAST_DAY }
    return if (patron != null) text("screen.profile.message.feast_day_saint").replace("{saint}", patron)
    else text("screen.profile.message.${kind.key}")
}
