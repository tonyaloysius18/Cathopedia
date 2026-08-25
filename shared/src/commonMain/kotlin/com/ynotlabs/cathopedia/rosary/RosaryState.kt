package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet

/** Single source of truth shared by the prayer pane and bead carousel. */
data class RosaryState(
    val sessionId: String,
    val mysterySet: MysterySet,
    val currentStepIndex: Int,
    val steps: List<SequenceStep> = RosarySequence(mysterySet).steps,
) {
    val currentStep: SequenceStep get() = steps[currentStepIndex.coerceIn(0, steps.lastIndex)]
    val currentNode: RosaryBead? get() = currentStep.beadIndex?.let { beadIndex ->
        rosaryLayout.firstOrNull { it.index == beadIndex }
    }

    val decadesCompleted: Int
        get() = steps.take(currentStepIndex + 1).count { it.prayerSlug == "fatima-decade-prayer" }

    fun advance(): RosaryState? =
        if (currentStepIndex >= steps.lastIndex) null else copy(currentStepIndex = currentStepIndex + 1)

    fun back(): RosaryState = copy(currentStepIndex = (currentStepIndex - 1).coerceAtLeast(0))

    fun jumpToNode(nodeIndex: Int): RosaryState {
        if (currentStep.beadIndex == nodeIndex) return this
        val target = steps.indexOfFirst { it.beadIndex == nodeIndex }
        return if (target < 0) this else copy(currentStepIndex = target)
    }
}
