package com.ynotlabs.cathopedia

import com.ynotlabs.cathopedia.profile.OccasionKind
import com.ynotlabs.cathopedia.profile.ProfileGreetings
import com.ynotlabs.cathopedia.profile.StateOfLife
import com.ynotlabs.cathopedia.profile.UserProfile
import com.ynotlabs.cathopedia.profile.yearsLabel
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProfileGreetingsTest {
    private fun kinds(p: UserProfile, day: LocalDate) = ProfileGreetings.occasionsOn(p, day).map { it.kind }

    @Test
    fun birthdayWithYears() {
        val o = ProfileGreetings.occasionsOn(UserProfile(birthDate = "1994-05-21"), LocalDate(2026, 5, 21))
        assertEquals(listOf(OccasionKind.BIRTHDAY), o.map { it.kind })
        assertEquals(32, o.single().years)
    }

    @Test
    fun leapDayFallsOn28thInCommonYears() {
        val p = UserProfile(birthDate = "2000-02-29")
        assertEquals(listOf(OccasionKind.BIRTHDAY), kinds(p, LocalDate(2027, 2, 28)))
        assertTrue(kinds(p, LocalDate(2028, 2, 28)).isEmpty())
        assertEquals(listOf(OccasionKind.BIRTHDAY), kinds(p, LocalDate(2028, 2, 29)))
        assertTrue(kinds(p, LocalDate(2027, 3, 1)).isEmpty())
    }

    @Test
    fun sameYearOrFutureDatesHaveNoAnniversary() {
        assertTrue(kinds(UserProfile(baptism = "2026-05-21"), LocalDate(2026, 5, 21)).isEmpty())
        assertTrue(kinds(UserProfile(baptism = "2030-05-21"), LocalDate(2026, 5, 21)).isEmpty())
    }

    @Test
    fun stateOfLifeHidesOtherDates() {
        val day = LocalDate(2026, 6, 29)
        val p = UserProfile(wedding = "2010-06-29", ordination = "2012-06-29", state = StateOfLife.MARRIED)
        assertEquals(listOf(OccasionKind.WEDDING), kinds(p, day))
        assertEquals(listOf(OccasionKind.ORDINATION), kinds(p.copy(state = StateOfLife.PRIEST), day))
    }

    @Test
    fun severalOnOneDayInOrderAndFeastHasNoYears() {
        val p = UserProfile(birthDate = "1990-10-04", baptism = "1990-10-04".replace("1990", "1991"), feastDay = "10-04")
        val o = ProfileGreetings.occasionsOn(p, LocalDate(2026, 10, 4))
        assertEquals(listOf(OccasionKind.BIRTHDAY, OccasionKind.FEAST_DAY, OccasionKind.BAPTISM), o.map { it.kind })
        assertEquals(null, o[1].years)
    }

    @Test
    fun versesRotateByYear() {
        val p = UserProfile(feastDay = "03-19")
        val a = ProfileGreetings.occasionsOn(p, LocalDate(2026, 3, 19)).single().verse
        val b = ProfileGreetings.occasionsOn(p, LocalDate(2027, 3, 19)).single().verse
        assertTrue(a != b)
    }

    @Test
    fun polishPlurals() {
        val text = { k: String -> mapOf("screen.profile.years_one" to "1 rok", "screen.profile.years_few" to "{n} lata", "screen.profile.years_other" to "{n} lat")[k]!! }
        assertEquals("1 rok", yearsLabel(1, "pl", text))
        assertEquals("3 lata", yearsLabel(3, "pl", text))
        assertEquals("12 lat", yearsLabel(12, "pl", text))
        assertEquals("22 lata", yearsLabel(22, "pl", text))
        assertEquals("5 lat", yearsLabel(5, "pl", text))
        assertEquals("3 lat", yearsLabel(3, "en", text))
    }

    @Test
    fun invalidMonthDayIgnored() {
        assertEquals(null, ProfileGreetings.parseMonthDay("02-30"))
        assertEquals(null, ProfileGreetings.parseMonthDay("13-01"))
        assertEquals(2 to 29, ProfileGreetings.parseMonthDay("02-29"))
    }
}
