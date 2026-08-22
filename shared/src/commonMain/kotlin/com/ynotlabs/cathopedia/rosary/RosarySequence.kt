package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet

// Bead indices match RosaryCanvas.kt's prayer-order numbering exactly (0 =
// crucifix, 1..5 = tail, 6 = medal, 7..61 = loop) so guided mode and the
// canvas stay in sync off the same currentIndex.
private const val CRUCIFIX_BEAD = 0
private const val TAIL_OUR_FATHER_BEAD = 1
private const val TAIL_HAIL_MARY_START_BEAD = 2
private const val TAIL_CLOSING_BEAD = 5 // last tail bead, nearest the medal — Glory Be is said here
private const val MEDAL_BEAD = 6
private const val LOOP_START_BEAD = 7
private const val BEADS_PER_DECADE = 11 // 1 Our Father + 10 Hail Marys, matching RosaryCanvas.kt

/**
 * Builds one full five-decade Rosary in [mysterySet]: Sign of the Cross →
 * Apostles' Creed → Our Father → 3x Hail Mary → Glory Be → 5 decades (Our
 * Father, 10x Hail Mary, Glory Be, Fatima prayer) → Salve Regina → closing
 * prayer → Sign of the Cross.
 *
 * The brief's per-decade step list names "announce mystery" as its own item
 * ahead of "Our Father," but [SequenceStep.prayerSlug] is non-nullable — there
 * is no prayer text for a bare announcement to reference. This ships the
 * announcement as the decade's Our Father step carrying [SequenceStep.mysteryId]
 * (announcement text itself stays out of the step — see [SequenceStep]'s doc),
 * which the UI shows before that step's prayer text rather than as a separate
 * step; there's no observable difference to a "Mystery announcement... shown
 * at the start of each decade" UI (Task 6) either way.
 *
 * Glory Be and the Fatima prayer are recited after the tenth Hail Mary
 * without a bead of their own on a real rosary, so both share that bead's index.
 */
class RosarySequence(mysterySet: MysterySet) : PrayerSequence {

    override val steps: List<SequenceStep> = buildList {
        var ordinal = 0
        fun step(prayerSlug: String, beadIndex: Int? = null, mysteryId: String? = null) {
            add(SequenceStep(ordinal = ordinal++, prayerSlug = prayerSlug, beadIndex = beadIndex, mysteryId = mysteryId, announcement = null))
        }

        step("sign-of-the-cross", beadIndex = CRUCIFIX_BEAD)
        step("apostles-creed", beadIndex = CRUCIFIX_BEAD)
        step("our-father", beadIndex = TAIL_OUR_FATHER_BEAD)
        repeat(3) { i -> step("hail-mary", beadIndex = TAIL_HAIL_MARY_START_BEAD + i) }
        step("glory-be", beadIndex = TAIL_CLOSING_BEAD)

        for (decade in 0 until 5) {
            val decadeOurFatherBead = LOOP_START_BEAD + decade * BEADS_PER_DECADE
            val lastHailMaryBead = decadeOurFatherBead + 10
            val mysteryId = "${mysterySet.tag}-${decade + 1}"

            step("our-father", beadIndex = decadeOurFatherBead, mysteryId = mysteryId)
            repeat(10) { i -> step("hail-mary", beadIndex = decadeOurFatherBead + 1 + i) }
            step("glory-be", beadIndex = lastHailMaryBead)
            step("fatima-decade-prayer", beadIndex = lastHailMaryBead)
        }

        step("salve-regina", beadIndex = MEDAL_BEAD)
        step("rosary-closing-prayer", beadIndex = MEDAL_BEAD)
        step("sign-of-the-cross", beadIndex = CRUCIFIX_BEAD)
    }
}
