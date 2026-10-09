package com.ynotlabs.cathopedia.ui.screens.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import kotlin.test.Test
import kotlin.test.assertEquals

class RosaryBeadCarouselTest {
    @Test
    fun swipesVisitEveryPhysicalElementOnceInBothDirections() {
        for (set in MysterySet.entries) {
            var state = RosaryState("session", set, 0)
            for (expected in rosaryLayout.drop(1)) {
                state = state.jumpToNode(adjacentCarouselBead(state.currentStep.beadIndex!!, forward = true))
                assertEquals(expected.index, state.currentNode?.index)
            }
            for (expected in rosaryLayout.asReversed().drop(1)) {
                state = state.jumpToNode(adjacentCarouselBead(state.currentStep.beadIndex!!, forward = false))
                assertEquals(expected.index, state.currentNode?.index)
            }
        }
    }

    @Test
    fun swipesStopAtTheEndsAndLockOnTheMedalFromBothSides() {
        assertEquals(0, adjacentCarouselBead(0, forward = false))
        assertEquals(61, adjacentCarouselBead(61, forward = true))
        assertEquals(6, adjacentCarouselBead(5, forward = true))
        assertEquals(6, adjacentCarouselBead(7, forward = false))
        assertEquals(7, adjacentCarouselBead(6, forward = true))
        assertEquals(5, adjacentCarouselBead(6, forward = false))
    }

    @Test
    fun theMedalShowsItsPrayerAndTheNextSwipeLeavesForOnlyTheAdjacentBead() {
        val initial = RosaryState("session", MysterySet.JOYFUL, 0)
        val fromPendant = initial.jumpToNode(5)
        val fromDecade = initial.jumpToNode(7)
        val medal = fromPendant.jumpToNode(adjacentCarouselBead(5, forward = true))
        assertEquals(medal, fromDecade.jumpToNode(adjacentCarouselBead(7, forward = false)))
        assertEquals(6, medal.currentNode?.index)
        assertEquals("salve-regina", medal.currentStep.prayerSlug)
        assertEquals(5, medal.jumpToNode(adjacentCarouselBead(6, forward = false)).currentNode?.index)
        assertEquals(7, medal.jumpToNode(adjacentCarouselBead(6, forward = true)).currentNode?.index)
    }

    @Test
    fun swipingFromSharedPrayersMovesOnlyToTheNeighboringPhysicalBead() {
        val initial = RosaryState("session", MysterySet.SORROWFUL, 0)
        val fatima = initial.steps.indexOfFirst { it.prayerSlug == "fatima-decade-prayer" }
        for (step in fatima - 2..fatima) {
            val state = initial.jumpToStep(step)
            val forward = state.jumpToNode(adjacentCarouselBead(state.currentStep.beadIndex!!, forward = true))
            val backward = state.jumpToNode(adjacentCarouselBead(state.currentStep.beadIndex!!, forward = false))
            assertEquals(18, forward.currentNode?.index)
            assertEquals(16, backward.currentNode?.index)
        }
    }
}
