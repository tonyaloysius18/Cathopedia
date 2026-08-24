package com.ynotlabs.cathopedia.model

/**
 * A resumable Rosary in progress — the subset of the rosary_session row the
 * praying screen needs to reopen on the exact bead. [currentStepIndex] indexes
 * into [com.ynotlabs.cathopedia.rosary.RosarySequence.steps].
 */
data class RosarySessionState(
    val id: String,
    val mysterySet: MysterySet,
    val startedAt: Long,
    val currentStepIndex: Int,
    val decadesCompleted: Int,
    val durationSeconds: Long,
)

/**
 * The landing-screen meter (A7). Every field only ever grows or is a snapshot;
 * there is deliberately no streak or missed-day count — see the brief's A7 note
 * that a broken streak turns a devotion into a debt.
 */
data class RosaryMeter(
    val totalCompleted: Long,
    val completedThisMonth: Long,
    val byMysterySet: Map<MysterySet, Long>,
    /** Completion instants (epoch millis), newest first — feeds the calendar heat-map. */
    val completionTimestamps: List<Long>,
)
