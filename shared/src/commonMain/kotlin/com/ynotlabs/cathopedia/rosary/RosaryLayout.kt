package com.ynotlabs.cathopedia.rosary

/**
 * A rendered Rosary bead — kind, which decade it belongs to, and its normalized
 * position on the layout box (x/y and radius are fractions of the box's width,
 * 0..1). This is the single source of the Rosary's geometry: the landing screen
 * composes sprites at these coordinates (A4), the flat-image map derives its tap
 * targets from them (RosaryDiagramHotspots), and the same numbers feed the
 * carousel later (A6/A8).
 *
 * [index] is the prayer-order bead number used everywhere else — it matches
 * [SequenceStep.beadIndex] from [RosarySequence] (0 = crucifix … 61 = the last
 * Hail Mary), so a step always resolves to exactly one bead.
 */
enum class BeadKind { CROSS, OUR_FATHER, HAIL_MARY, CENTERPIECE }

data class RosaryBead(
    val index: Int,
    val kind: BeadKind,
    /** 1..5 for the loop's decade beads; null on the crucifix, pendant and centerpiece. */
    val decade: Int?,
    /** 1..10 for a decade's Hail Marys; null otherwise. */
    val indexInDecade: Int?,
    /** 1..6 for Hail Mary beads (cycles so no two adjacent share a pattern); 1 otherwise. */
    val spriteVariant: Int,
    val x: Float,
    val y: Float,
    val radius: Float,
)

/**
 * The whole Rosary as one positioned list, 62 beads. Coordinates were tuned to a
 * clean tail-and-ellipse layout (the crucifix hangs at the foot, the loop of five
 * decades fills the upper field). Totals: 1 crucifix, 1 centerpiece, 53 Hail
 * Marys, and 7 large beads (the pendant Our Father, the Glory-Be bead before the
 * medal, and the five decade Our Fathers).
 */
val rosaryLayout: List<RosaryBead> = listOf(
    RosaryBead(0, BeadKind.CROSS, decade = null, indexInDecade = null, spriteVariant = 1, x = 0.4833f, y = 0.9222f, radius = 0.045f),
    RosaryBead(1, BeadKind.OUR_FATHER, decade = null, indexInDecade = null, spriteVariant = 1, x = 0.4775f, y = 0.8635f, radius = 0.028f),
    RosaryBead(2, BeadKind.HAIL_MARY, decade = null, indexInDecade = null, spriteVariant = 1, x = 0.4883f, y = 0.795f, radius = 0.02f),
    RosaryBead(3, BeadKind.HAIL_MARY, decade = null, indexInDecade = null, spriteVariant = 2, x = 0.5021f, y = 0.741f, radius = 0.02f),
    RosaryBead(4, BeadKind.HAIL_MARY, decade = null, indexInDecade = null, spriteVariant = 3, x = 0.5149f, y = 0.6873f, radius = 0.02f),
    RosaryBead(5, BeadKind.OUR_FATHER, decade = null, indexInDecade = null, spriteVariant = 1, x = 0.5207f, y = 0.6202f, radius = 0.028f),
    RosaryBead(6, BeadKind.CENTERPIECE, decade = null, indexInDecade = null, spriteVariant = 1, x = 0.5f, y = 0.55f, radius = 0.035f),
    RosaryBead(7, BeadKind.OUR_FATHER, decade = 1, indexInDecade = null, spriteVariant = 1, x = 0.4608f, y = 0.5486f, radius = 0.028f),
    RosaryBead(8, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 1, spriteVariant = 4, x = 0.4221f, y = 0.5446f, radius = 0.02f),
    RosaryBead(9, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 2, spriteVariant = 5, x = 0.3844f, y = 0.5378f, radius = 0.02f),
    RosaryBead(10, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 3, spriteVariant = 6, x = 0.3481f, y = 0.5285f, radius = 0.02f),
    RosaryBead(11, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 4, spriteVariant = 1, x = 0.3138f, y = 0.5168f, radius = 0.02f),
    RosaryBead(12, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 5, spriteVariant = 2, x = 0.2818f, y = 0.5027f, radius = 0.02f),
    RosaryBead(13, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 6, spriteVariant = 3, x = 0.2525f, y = 0.4865f, radius = 0.02f),
    RosaryBead(14, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 7, spriteVariant = 4, x = 0.2264f, y = 0.4684f, radius = 0.02f),
    RosaryBead(15, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 8, spriteVariant = 5, x = 0.2036f, y = 0.4486f, radius = 0.02f),
    RosaryBead(16, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 9, spriteVariant = 6, x = 0.1847f, y = 0.4273f, radius = 0.02f),
    RosaryBead(17, BeadKind.HAIL_MARY, decade = 1, indexInDecade = 10, spriteVariant = 1, x = 0.1696f, y = 0.4049f, radius = 0.02f),
    RosaryBead(18, BeadKind.OUR_FATHER, decade = 2, indexInDecade = null, spriteVariant = 1, x = 0.1588f, y = 0.3815f, radius = 0.028f),
    RosaryBead(19, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 1, spriteVariant = 2, x = 0.1522f, y = 0.3576f, radius = 0.02f),
    RosaryBead(20, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 2, spriteVariant = 3, x = 0.15f, y = 0.3333f, radius = 0.02f),
    RosaryBead(21, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 3, spriteVariant = 4, x = 0.1522f, y = 0.3091f, radius = 0.02f),
    RosaryBead(22, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 4, spriteVariant = 5, x = 0.1588f, y = 0.2851f, radius = 0.02f),
    RosaryBead(23, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 5, spriteVariant = 6, x = 0.1696f, y = 0.2618f, radius = 0.02f),
    RosaryBead(24, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 6, spriteVariant = 1, x = 0.1847f, y = 0.2393f, radius = 0.02f),
    RosaryBead(25, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 7, spriteVariant = 2, x = 0.2036f, y = 0.2181f, radius = 0.02f),
    RosaryBead(26, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 8, spriteVariant = 3, x = 0.2264f, y = 0.1982f, radius = 0.02f),
    RosaryBead(27, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 9, spriteVariant = 4, x = 0.2525f, y = 0.1801f, radius = 0.02f),
    RosaryBead(28, BeadKind.HAIL_MARY, decade = 2, indexInDecade = 10, spriteVariant = 5, x = 0.2818f, y = 0.1639f, radius = 0.02f),
    RosaryBead(29, BeadKind.OUR_FATHER, decade = 3, indexInDecade = null, spriteVariant = 1, x = 0.3138f, y = 0.1499f, radius = 0.028f),
    RosaryBead(30, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 1, spriteVariant = 6, x = 0.3481f, y = 0.1381f, radius = 0.02f),
    RosaryBead(31, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 2, spriteVariant = 1, x = 0.3844f, y = 0.1288f, radius = 0.02f),
    RosaryBead(32, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 3, spriteVariant = 2, x = 0.4221f, y = 0.1221f, radius = 0.02f),
    RosaryBead(33, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 4, spriteVariant = 3, x = 0.4608f, y = 0.118f, radius = 0.02f),
    RosaryBead(34, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 5, spriteVariant = 4, x = 0.5f, y = 0.1167f, radius = 0.02f),
    RosaryBead(35, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 6, spriteVariant = 5, x = 0.5392f, y = 0.118f, radius = 0.02f),
    RosaryBead(36, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 7, spriteVariant = 6, x = 0.5779f, y = 0.1221f, radius = 0.02f),
    RosaryBead(37, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 8, spriteVariant = 1, x = 0.6156f, y = 0.1288f, radius = 0.02f),
    RosaryBead(38, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 9, spriteVariant = 2, x = 0.6519f, y = 0.1381f, radius = 0.02f),
    RosaryBead(39, BeadKind.HAIL_MARY, decade = 3, indexInDecade = 10, spriteVariant = 3, x = 0.6862f, y = 0.1499f, radius = 0.02f),
    RosaryBead(40, BeadKind.OUR_FATHER, decade = 4, indexInDecade = null, spriteVariant = 1, x = 0.7182f, y = 0.1639f, radius = 0.028f),
    RosaryBead(41, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 1, spriteVariant = 4, x = 0.7475f, y = 0.1801f, radius = 0.02f),
    RosaryBead(42, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 2, spriteVariant = 5, x = 0.7736f, y = 0.1982f, radius = 0.02f),
    RosaryBead(43, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 3, spriteVariant = 6, x = 0.7964f, y = 0.2181f, radius = 0.02f),
    RosaryBead(44, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 4, spriteVariant = 1, x = 0.8153f, y = 0.2393f, radius = 0.02f),
    RosaryBead(45, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 5, spriteVariant = 2, x = 0.8304f, y = 0.2618f, radius = 0.02f),
    RosaryBead(46, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 6, spriteVariant = 3, x = 0.8412f, y = 0.2851f, radius = 0.02f),
    RosaryBead(47, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 7, spriteVariant = 4, x = 0.8478f, y = 0.3091f, radius = 0.02f),
    RosaryBead(48, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 8, spriteVariant = 5, x = 0.85f, y = 0.3333f, radius = 0.02f),
    RosaryBead(49, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 9, spriteVariant = 6, x = 0.8478f, y = 0.3576f, radius = 0.02f),
    RosaryBead(50, BeadKind.HAIL_MARY, decade = 4, indexInDecade = 10, spriteVariant = 1, x = 0.8412f, y = 0.3815f, radius = 0.02f),
    RosaryBead(51, BeadKind.OUR_FATHER, decade = 5, indexInDecade = null, spriteVariant = 1, x = 0.8304f, y = 0.4049f, radius = 0.028f),
    RosaryBead(52, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 1, spriteVariant = 2, x = 0.8153f, y = 0.4273f, radius = 0.02f),
    RosaryBead(53, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 2, spriteVariant = 3, x = 0.7964f, y = 0.4486f, radius = 0.02f),
    RosaryBead(54, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 3, spriteVariant = 4, x = 0.7736f, y = 0.4684f, radius = 0.02f),
    RosaryBead(55, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 4, spriteVariant = 5, x = 0.7475f, y = 0.4865f, radius = 0.02f),
    RosaryBead(56, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 5, spriteVariant = 6, x = 0.7182f, y = 0.5027f, radius = 0.02f),
    RosaryBead(57, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 6, spriteVariant = 1, x = 0.6862f, y = 0.5168f, radius = 0.02f),
    RosaryBead(58, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 7, spriteVariant = 2, x = 0.6519f, y = 0.5285f, radius = 0.02f),
    RosaryBead(59, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 8, spriteVariant = 3, x = 0.6156f, y = 0.5378f, radius = 0.02f),
    RosaryBead(60, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 9, spriteVariant = 4, x = 0.5779f, y = 0.5446f, radius = 0.02f),
    RosaryBead(61, BeadKind.HAIL_MARY, decade = 5, indexInDecade = 10, spriteVariant = 5, x = 0.5392f, y = 0.5486f, radius = 0.02f),
)

/** The Rosary structure as a positioned node list — 62 beads (see [rosaryLayout]). */
fun buildRosary(): List<RosaryBead> = rosaryLayout
