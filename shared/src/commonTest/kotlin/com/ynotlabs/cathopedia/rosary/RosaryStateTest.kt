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
}
