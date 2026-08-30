package com.ynotlabs.cathopedia.mass

/**
 * The postures of the Mass, for the dedicated Postures & Responses screen —
 * the counterpart of [MonstranceData] / [ThuribleData] / [VesselsData]. The
 * screen adds a responses block below the posture cards.
 */
data class Posture(
    val id: String,
    val nameEn: String,
    val nameFr: String,
    val descEn: String,
    val descFr: String,
)

object PosturesData {
    const val INTRO_EN =
        "At Mass the whole body prays. The faithful stand, sit and kneel together at set moments — a shared language of reverence that expresses one faith in one body."
    const val INTRO_FR =
        "À la messe, tout le corps prie. Les fidèles se lèvent, s'assoient et s'agenouillent ensemble à des moments précis — un langage commun de révérence qui exprime une seule foi en un seul corps."

    const val RESPONSES_EN =
        "Throughout the Mass the people answer the priest. To “The Lord be with you” they reply “And with your spirit”; the readings close with “Thanks be to God” and “Praise to you, Lord Jesus Christ”; and to the great prayers the assembly seals its assent with “Amen.”"
    const val RESPONSES_FR =
        "Tout au long de la messe, le peuple répond au prêtre. À « Le Seigneur soit avec vous », il répond « Et avec votre esprit » ; les lectures s'achèvent par « Nous rendons grâce à Dieu » et « Louange à toi, Seigneur Jésus » ; et aux grandes prières l'assemblée scelle son adhésion par « Amen »."

    val postures: List<Posture> = listOf(
        Posture("stand", "Standing", "Debout",
            "For prayer and to honour the Gospel — the posture of the risen and the attentive.",
            "Pour la prière et pour honorer l'Évangile — l'attitude du ressuscité et de celui qui écoute."),
        Posture("sit", "Sitting", "Assis",
            "For the readings before the Gospel and the homily — the posture of listening.",
            "Pour les lectures avant l'Évangile et l'homélie — l'attitude de l'écoute."),
        Posture("kneel", "Kneeling", "À genoux",
            "During the Eucharistic Prayer and before Communion — the posture of adoration and humility.",
            "Pendant la prière eucharistique et avant la communion — l'attitude de l'adoration et de l'humilité."),
        Posture("genuflect", "Genuflection", "La génuflexion",
            "Bending the right knee toward the tabernacle, adoring Christ present in the Eucharist.",
            "Fléchir le genou droit vers le tabernacle, pour adorer le Christ présent dans l'Eucharistie."),
        Posture("bow", "Bowing", "L'inclination",
            "A bow of the head at the holy names, and a deeper bow before receiving Communion.",
            "Une inclination de la tête aux saints noms, et une inclination plus profonde avant de communier."),
        Posture("sign", "The Sign of the Cross", "Le signe de croix",
            "Made at the beginning and end, marking the whole prayer as Christ's.",
            "Fait au début et à la fin, marquant toute la prière comme celle du Christ."),
    )
}
