package com.ynotlabs.cathopedia.rosary

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

internal const val ROSARY_OVERVIEW_ASPECT = 0.5f

/** Presentation geometry only: prayer order and the strand's snap positions stay unchanged. */
internal val rosaryOverviewLayout: List<RosaryBead> = run {
    // Sample in physical space, so the beads have even spacing around a tall oval.
    val samples = (0..720).map { sample ->
        val angle = sample * 2.0 * PI / 720
        (0.5f - 0.37f * sin(angle).toFloat()) to
            (0.33f + 0.30f * cos(angle).toFloat())
    }
    val distances = FloatArray(samples.size)
    for (i in 1 until samples.size) {
        val dx = samples[i].first - samples[i - 1].first
        val dy = (samples[i].second - samples[i - 1].second) / ROSARY_OVERVIEW_ASPECT
        distances[i] = distances[i - 1] + sqrt(dx * dx + dy * dy)
    }
    val pendantY = listOf(0.955f, 0.899f, 0.855f, 0.811f, 0.767f, 0.715f, 0.654f)
    rosaryLayout.map { bead ->
        if (bead.index <= 6) {
            bead.copy(
                x = 0.5f,
                y = pendantY[bead.index],
                radius = when (bead.kind) {
                    BeadKind.CROSS -> 0.065f
                    BeadKind.CENTERPIECE -> 0.043f
                    else -> bead.radius
                },
            )
        } else {
            // Leave one interval at each end for the medal joining the loop.
            val distance = distances.last() * (bead.index - 6) / 56f
            val end = distances.indexOfFirst { it >= distance }.coerceAtLeast(1)
            val fraction = (distance - distances[end - 1]) / (distances[end] - distances[end - 1])
            val startPoint = samples[end - 1]
            val endPoint = samples[end]
            bead.copy(
                x = startPoint.first + (endPoint.first - startPoint.first) * fraction,
                y = startPoint.second + (endPoint.second - startPoint.second) * fraction,
            )
        }
    }
}
