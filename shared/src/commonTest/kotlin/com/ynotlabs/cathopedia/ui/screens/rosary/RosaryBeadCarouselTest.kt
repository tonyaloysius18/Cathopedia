package com.ynotlabs.cathopedia.ui.screens.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.rosary.BeadKind
import com.ynotlabs.cathopedia.rosary.RosarySequence
import com.ynotlabs.cathopedia.rosary.RosaryState
import com.ynotlabs.cathopedia.rosary.rosaryLayout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RosaryBeadCarouselTest {
    private fun RosaryState.swipe(forward: Boolean): RosaryState {
        val next = adjacentCarouselSlot(currentCarouselSlot(this), forward, isClosingCross(this))
        return selectSlot(carouselSlots[next])
    }

    private val medal = rosaryLayout.first { it.kind == BeadKind.CENTERPIECE }.index
    private val loopBeads = rosaryLayout.filter { it.decade != null }.map { it.index }
    private val pendantBeads = rosaryLayout.filter { it.decade == null && it.kind != BeadKind.CENTERPIECE }.map { it.index }

    @Test
    fun swipesFollowPrayerOrderFromTheMedalRoundTheLoopAndBackToTheCrucifix() {
        for (set in MysterySet.entries) {
            var state = RosaryState("session", set, 0)
            val forward = pendantBeads.drop(1) + medal + loopBeads + medal + 0
            for (expected in forward) {
                state = state.swipe(forward = true)
                assertEquals(expected, state.currentNode?.index)
            }
            assertEquals(state.steps.lastIndex, state.currentStepIndex, "ends on the final Sign of the Cross")
            assertEquals(state, state.swipe(forward = true), "nothing past the final Sign of the Cross")

            val backward = listOf(medal) + loopBeads.asReversed() + medal + pendantBeads.asReversed()
            for (expected in backward) {
                state = state.swipe(forward = false)
                assertEquals(expected, state.currentNode?.index)
            }
            assertEquals(0, state.currentStepIndex, "back at the opening Sign of the Cross")
        }
    }

    @Test
    fun theMedalAtTheStartAnnouncesTheFirstMystery() {
        val fromPendant = RosaryState("session", MysterySet.JOYFUL, 0).jumpToNode(5)
        val announcement = fromPendant.swipe(forward = true)
        assertEquals(medal, announcement.currentNode?.index)
        assertEquals(RosarySequence.ANNOUNCE_MYSTERY, announcement.currentStep.prayerSlug)
        assertEquals("joyful-1", announcement.currentStep.mysteryId)
        assertEquals(1, announcement.currentDecade)
        assertTrue(announcement.currentStepIndex < announcement.steps.size / 2, "must not jump to the closing prayers")
        assertEquals(medal, carouselSlots[currentCarouselSlot(announcement)].bead.index)
        assertTrue(!carouselSlots[currentCarouselSlot(announcement)].closesLoop)

        val firstOurFather = announcement.swipe(forward = true)
        assertEquals(7, firstOurFather.currentNode?.index)
        assertEquals("our-father", firstOurFather.currentStep.prayerSlug)
        assertEquals(announcement, firstOurFather.swipe(forward = false))
        assertEquals(5, announcement.swipe(forward = false).currentNode?.index)
    }

    @Test
    fun theClosingMedalComesAfterTheLastHailMary() {
        val lastHailMary = RosaryState("session", MysterySet.GLORIOUS, 0).jumpToNode(61)
        val closing = lastHailMary.swipe(forward = true)
        assertEquals(medal, closing.currentNode?.index)
        assertEquals("salve-regina", closing.currentStep.prayerSlug)
        assertEquals(carouselSlots.lastIndex, currentCarouselSlot(closing))
        assertEquals(61, closing.swipe(forward = false).currentNode?.index)
    }

    @Test
    fun tappingEitherMedalPicksItsOwnPrayers() {
        val early = RosaryState("session", MysterySet.LUMINOUS, 0).jumpToNode(20)
        val opening = carouselSlots.first { it.bead.index == medal && !it.closesLoop }
        val closing = carouselSlots.last()
        assertEquals(RosarySequence.ANNOUNCE_MYSTERY, early.selectSlot(opening).currentStep.prayerSlug)
        assertEquals("salve-regina", early.selectSlot(closing).currentStep.prayerSlug)
    }

    @Test
    fun swipingFromSharedPrayersMovesOnlyToTheNeighboringPhysicalBead() {
        val initial = RosaryState("session", MysterySet.SORROWFUL, 0)
        val fatima = initial.steps.indexOfFirst { it.prayerSlug == "fatima-decade-prayer" }
        for (step in fatima - 2..fatima) {
            val state = initial.jumpToStep(step)
            assertEquals(18, state.swipe(forward = true).currentNode?.index)
            assertEquals(16, state.swipe(forward = false).currentNode?.index)
        }
    }
}
