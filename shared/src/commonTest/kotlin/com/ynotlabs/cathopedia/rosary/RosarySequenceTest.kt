package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Locks the shape of the generated prayer sequence — the brief's A2 invariants
 * (step counts, decade boundaries, ordering) expressed against the sequence the
 * app actually recites. Counts are devotional facts a refactor must not silently
 * change: a full Rosary is 6 Our Fathers and 53 Hail Marys, five decades of ten.
 */
class RosarySequenceTest {

    private fun steps(set: MysterySet) = RosarySequence(set).steps

    @Test
    fun totalStepCountIsStable() {
        // 2 signs + creed + 6 Our Fathers + 53 Hail Marys + 6 Glory Bes +
        // 5 Fatima + Salve Regina + closing = 75.
        assertEquals(75, steps(MysterySet.JOYFUL).size)
    }

    @Test
    fun prayerCountsMatchAFullRosary() {
        val bySlug = steps(MysterySet.SORROWFUL).groupingBy { it.prayerSlug }.eachCount()
        assertEquals(6, bySlug["our-father"], "6 Our Fathers (1 pendant + 5 decades)")
        assertEquals(53, bySlug["hail-mary"], "53 Hail Marys (3 pendant + 50 decade)")
        assertEquals(6, bySlug["glory-be"], "6 Glory Bes (pendant + 5 decades)")
        assertEquals(5, bySlug["fatima-decade-prayer"])
        assertEquals(2, bySlug["sign-of-the-cross"], "opens and closes the Rosary")
        assertEquals(1, bySlug["apostles-creed"])
        assertEquals(1, bySlug["salve-regina"])
        assertEquals(1, bySlug["rosary-closing-prayer"])
    }

    @Test
    fun ordinalsAreContiguousAndAscending() {
        val s = steps(MysterySet.GLORIOUS)
        s.forEachIndexed { i, step -> assertEquals(i, step.ordinal, "ordinal must equal list index") }
    }

    @Test
    fun opensWithSignThenCreedAndClosesWithSalveClosingSign() {
        val s = steps(MysterySet.LUMINOUS)
        assertEquals("sign-of-the-cross", s.first().prayerSlug)
        assertEquals("apostles-creed", s[1].prayerSlug)
        assertEquals(listOf("salve-regina", "rosary-closing-prayer", "sign-of-the-cross"), s.takeLast(3).map { it.prayerSlug })
    }

    @Test
    fun fiveDecadesEachTenHailMarysBoundedByOurFatherAndGloryBe() {
        val s = steps(MysterySet.JOYFUL)
        // The five decade-opening Our Fathers are exactly the mystery-bearing steps.
        val decadeOpeners = s.filter { it.mysteryId != null }
        assertEquals(5, decadeOpeners.size)
        decadeOpeners.forEachIndexed { d, opener ->
            assertEquals("our-father", opener.prayerSlug)
            val start = opener.ordinal
            val decade = s.subList(start, start + 13) // OF + 10 HM + Glory Be + Fatima
            assertEquals("our-father", decade[0].prayerSlug)
            assertEquals(List(10) { "hail-mary" }, decade.subList(1, 11).map { it.prayerSlug })
            assertEquals("glory-be", decade[11].prayerSlug)
            assertEquals("fatima-decade-prayer", decade[12].prayerSlug)
        }
    }

    @Test
    fun gloryBeAndFatimaShareTheDecadesLastHailMaryBead() {
        val s = steps(MysterySet.SORROWFUL)
        s.filter { it.mysteryId != null }.forEach { opener ->
            val decade = s.subList(opener.ordinal, opener.ordinal + 13)
            val lastHailMaryBead = decade[10].beadIndex
            assertEquals(lastHailMaryBead, decade[11].beadIndex, "Glory Be sits on the last Hail Mary bead")
            assertEquals(lastHailMaryBead, decade[12].beadIndex, "Fatima prayer sits on the last Hail Mary bead")
        }
    }

    @Test
    fun mysteryIdsAreTaggedPerSetAndNumberedOneToFive() {
        for (set in MysterySet.entries) {
            val ids = steps(set).mapNotNull { it.mysteryId }
            assertEquals((1..5).map { "${set.tag}-$it" }, ids, "decade openers name this set's five mysteries in order")
        }
    }

    @Test
    fun beadIndicesAreNonNegativeAndOrderedForwardAcrossTheLoop() {
        // The loop's Our Father / Hail Mary beads only ever advance.
        val loopBeads = steps(MysterySet.GLORIOUS)
            .filter { it.mysteryId != null || it.prayerSlug == "hail-mary" }
            .mapNotNull { it.beadIndex }
        assertTrue(loopBeads.zipWithNext().all { (a, b) -> b >= a }, "carousel never scrolls backwards mid-loop")
        assertTrue(loopBeads.all { it >= 0 })
    }
}
