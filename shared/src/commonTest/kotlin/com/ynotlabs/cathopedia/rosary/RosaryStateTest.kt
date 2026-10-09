package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RosaryStateTest {

    @Test
    fun advanceBackAndJumpKeepStepAndNodeInSync() {
        val initial = RosaryState("session", MysterySet.JOYFUL, 0)
        assertEquals(0, initial.currentNode?.index)
        val advanced = initial.advance()!!
        assertEquals(1, advanced.currentStepIndex)
        assertEquals(0, advanced.currentNode?.index)
        assertEquals(0, advanced.back().currentStepIndex)

        val jumped = initial.jumpToNode(23)
        assertEquals(23, jumped.currentNode?.index)
        assertEquals(23, jumped.currentStep.beadIndex)
    }

    @Test
    fun completionOccursOnlyAfterTheLastStep() {
        val last = RosaryState("session", MysterySet.GLORIOUS, RosarySequence(MysterySet.GLORIOUS).steps.lastIndex)
        assertNull(last.advance())
    }

    @Test
    fun decadeCountOnlyIncreasesAfterOptionalDecadePrayer() {
        val steps = RosarySequence(MysterySet.SORROWFUL).steps
        val firstFatima = steps.indexOfFirst { it.prayerSlug == "fatima-decade-prayer" }
        assertEquals(0, RosaryState("session", MysterySet.SORROWFUL, firstFatima - 1).decadesCompleted)
        assertEquals(1, RosaryState("session", MysterySet.SORROWFUL, firstFatima).decadesCompleted)
    }

    @Test
    fun carouselCanSelectEveryPrayerIncludingThoseSharingABead() {
        for (set in MysterySet.entries) {
            val initial = RosaryState("session", set, 0)
            initial.steps.forEachIndexed { index, step ->
                val selected = initial.jumpToStep(index)
                assertEquals(index, selected.currentStepIndex)
                assertEquals(step.prayerSlug, selected.currentStep.prayerSlug)
                assertEquals(step.beadIndex, selected.currentNode?.index)
            }
            val creed = initial.jumpToStep(1)
            assertEquals("apostles-creed", creed.currentStep.prayerSlug)
            assertEquals(initial.currentNode, creed.currentNode)
            val fatima = initial.steps.indexOfFirst { it.prayerSlug == "fatima-decade-prayer" }
            assertEquals("glory-be", initial.jumpToStep(fatima - 1).currentStep.prayerSlug)
            assertEquals("fatima-decade-prayer", initial.jumpToStep(fatima).currentStep.prayerSlug)
            assertEquals(initial.jumpToStep(fatima - 2).currentNode, initial.jumpToStep(fatima).currentNode)
        }
    }

    @Test
    fun carouselSelectionClampsToTheSequenceAndDoesNotFinishIt() {
        val initial = RosaryState("session", MysterySet.JOYFUL, 0)
        assertEquals(0, initial.jumpToStep(-1).currentStepIndex)
        val last = initial.jumpToStep(Int.MAX_VALUE)
        assertEquals(initial.steps.lastIndex, last.currentStepIndex)
        assertEquals("sign-of-the-cross", last.currentStep.prayerSlug)
        assertNull(last.advance())
    }

    @Test
    fun sharedPrayersRemainOnOnePhysicalBead() {
        val initial = RosaryState("session", MysterySet.JOYFUL, 0)
        assertEquals(0..1, initial.currentBeadPrayerSteps)
        assertEquals(0..1, initial.advance()!!.currentBeadPrayerSteps)
        val fatima = initial.steps.indexOfFirst { it.prayerSlug == "fatima-decade-prayer" }
        for (step in fatima - 2..fatima) {
            val state = initial.jumpToStep(step)
            assertEquals(fatima - 2..fatima, state.currentBeadPrayerSteps)
            assertEquals(state, state.jumpToNode(state.currentNode!!.index))
        }
        val closing = initial.steps.indexOfFirst { it.prayerSlug == "salve-regina" }
        assertEquals(closing..closing + 1, initial.jumpToStep(closing).currentBeadPrayerSteps)
    }

    @Test
    fun returningToTheCrossAtTheEndKeepsTheClosingPrayer() {
        val state = RosaryState("session", MysterySet.SORROWFUL, 0)
        val closing = state.steps.indexOfFirst { it.prayerSlug == "rosary-closing-prayer" }
        assertEquals(state.steps.lastIndex, state.jumpToStep(closing).jumpToNode(0).currentStepIndex)
        assertEquals(0, state.jumpToStep(2).jumpToNode(0).currentStepIndex)
    }
}
