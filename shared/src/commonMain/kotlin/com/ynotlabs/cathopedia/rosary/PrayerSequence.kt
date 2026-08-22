package com.ynotlabs.cathopedia.rosary

/**
 * One step of a recited prayer sequence — the Rosary is the first consumer,
 * chaplets and novenas reuse this later. [prayerSlug] references a
 * content/prayers/&lt;slug&gt;.json id; the engine builds a valid sequence even
 * when that prayer's text isn't sourced yet (see content/README.md's
 * provenance note) — the UI shows a "text not yet available" placeholder for
 * that step rather than crashing or skipping it.
 *
 * [announcement], when non-null, is a fixed, non-localized string a sequence
 * wants displayed verbatim. [RosarySequence] never sets it — a Rosary decade's
 * announcement text ("The First Joyful Mystery: The Annunciation") is
 * localized content that lives in the Mystery/MysteryText tables, which this
 * generic, DB-free engine has no access to; the UI builds that text itself
 * once it has a language, by looking up [mysteryId].
 */
data class SequenceStep(
    val ordinal: Int,
    val prayerSlug: String,
    val beadIndex: Int?,
    val mysteryId: String?,
    val announcement: String?,
)

interface PrayerSequence {
    val steps: List<SequenceStep>
}
