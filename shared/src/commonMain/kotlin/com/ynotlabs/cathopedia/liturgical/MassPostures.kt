package com.ynotlabs.cathopedia.liturgical

enum class MassPosture(val key: String) {
    STAND("stand"), SIT("sit"), KNEEL("kneel"), BOW("bow"), GENUFLECT("genuflect"), SIGN_OF_CROSS("sign"),
}

data class MassPostureCue(val posture: MassPosture, val detailKey: String? = null)

/**
 * Posture of the congregation, independently of the minister named in `who`.
 * GIRM 43, 137 and 160: https://www.usccb.org/prayer-and-worship/the-mass/general-instruction-of-the-roman-missal
 * Do not prescribe a single posture during Communion or the optional Sequence.
 */
fun massPostureCues(partId: String, profile: OrdoProfile, celebrationKey: String): List<MassPostureCue> {
    val primary = when (partId) {
        "entrance", "sign_of_cross", "greeting", "penitential_act", "kyrie", "gloria", "collect",
        "acclamation", "gospel", "creed", "universal_prayer", "prayer_over_offerings", "preface",
        "sanctus", "lords_prayer", "sign_of_peace", "agnus_dei", "prayer_after_communion",
        "announcements", "blessing", "dismissal" -> MassPostureCue(MassPosture.STAND)
        "first_reading", "psalm", "second_reading", "homily", "preparation" -> MassPostureCue(MassPosture.SIT)
        "eucharistic_prayer" -> MassPostureCue(MassPosture.KNEEL,
            if (profile == OrdoProfile.US) "screen.mass.postures.local_custom"
            else "screen.mass.postures.at_consecration")
        else -> null
    }
    val gesture = when (partId) {
        "sign_of_cross", "blessing" -> MassPostureCue(MassPosture.SIGN_OF_CROSS)
        "creed" -> MassPostureCue(
            if (celebrationKey == "Christmas" || celebrationKey == "Annunciation") MassPosture.GENUFLECT else MassPosture.BOW,
            "screen.mass.postures.at_incarnation",
        )
        else -> null
    }
    return listOfNotNull(primary, gesture)
}
