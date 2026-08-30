package com.ynotlabs.cathopedia.mass

/**
 * The sacred vessels of the altar, for the dedicated Sacred Vessels screen —
 * the counterpart of [MonstranceData] / [ThuribleData]. Unlike those two, the
 * vessels are distinct objects rather than parts of one, so the screen shows
 * no single "whole" image.
 */
data class Vessel(
    val id: String,
    val nameEn: String,
    val nameFr: String,
    val descEn: String,
    val descFr: String,
)

object VesselsData {
    const val INTRO_EN =
        "Because they hold the Eucharist, the vessels of the altar are made of precious metal (or lined with it), blessed for sacred use, and handled only with reverence."
    const val INTRO_FR =
        "Parce qu'ils contiennent l'Eucharistie, les vases de l'autel sont faits de métal précieux (ou en sont doublés), bénis pour l'usage sacré, et maniés avec révérence."

    val vessels: List<Vessel> = listOf(
        Vessel("chalice", "Chalice", "Le calice",
            "The cup that holds the wine which becomes the Blood of Christ.",
            "La coupe qui contient le vin qui devient le Sang du Christ."),
        Vessel("paten", "Paten", "La patène",
            "The small plate that holds the priest's host.",
            "Le petit plateau qui porte l'hostie du prêtre."),
        Vessel("ciborium", "Ciborium", "Le ciboire",
            "The covered cup that holds the hosts for the Communion of the faithful and reserves them in the tabernacle.",
            "Le vase couvert qui contient les hosties pour la communion des fidèles et les conserve au tabernacle."),
        Vessel("cruets", "Cruets", "Les burettes",
            "The small jugs of wine and water brought to the altar at the Preparation of the Gifts.",
            "Les petits flacons de vin et d'eau apportés à l'autel à la préparation des dons."),
        Vessel("pyx", "Pyx", "La custode",
            "The small round case used to carry Communion to the sick.",
            "Le petit boîtier rond servant à porter la communion aux malades."),
        Vessel("luna", "Luna", "La lunule",
            "The crescent clip that holds the Host upright in the monstrance for adoration.",
            "La pince en croissant qui maintient l'hostie dressée dans l'ostensoir pour l'adoration."),
    )
}
