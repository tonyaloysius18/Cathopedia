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

    /** The decade being prayed, including the first mystery's announcement on the medal. */
    val currentDecade: Int?
        get() = currentNode?.decade ?: currentStep.mysteryId?.substringAfterLast('-')?.toIntOrNull()

    val decadesCompleted: Int
        get() = steps.take(currentStepIndex + 1).count { it.prayerSlug == "fatima-decade-prayer" }

    /** Consecutive prayers recited on this physical bead; no extra beads are invented. */
    val currentBeadPrayerSteps: IntRange
        get() {
            var first = currentStepIndex
            var last = currentStepIndex
            while (first > 0 && steps[first - 1].beadIndex == currentStep.beadIndex) first--
            while (last < steps.lastIndex && steps[last + 1].beadIndex == currentStep.beadIndex) last++
            return first..last
        }

    fun advance(): RosaryState? =
        if (currentStepIndex >= steps.lastIndex) null else copy(currentStepIndex = currentStepIndex + 1)

    fun back(): RosaryState = copy(currentStepIndex = (currentStepIndex - 1).coerceAtLeast(0))

    /** Explicit prayer navigation, including several prayers on the same physical bead. */
    fun jumpToStep(stepIndex: Int): RosaryState =
        copy(currentStepIndex = stepIndex.coerceIn(0, steps.lastIndex))

    fun jumpToNode(nodeIndex: Int): RosaryState {
        if (currentStep.beadIndex == nodeIndex) return this
        // The cross and the medal are each visited at the opening and the closing.
        // Returning to one near the end reveals its closing prayers rather than
        // resetting the session to the opening ones.
        val matches = steps.indices.filter { steps[it].beadIndex == nodeIndex }
        if (matches.isEmpty()) return this
        val half = steps.size / 2
        val target = if (currentStepIndex > half) matches.firstOrNull { it > half } ?: matches.first() else matches.first()
        return copy(currentStepIndex = target)
    }
}
