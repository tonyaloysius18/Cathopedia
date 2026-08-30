package com.ynotlabs.cathopedia.mass

/**
 * The altar — its furnishings, its cloths, and its markings — for the dedicated
 * Altar screen. Counterpart of [MonstranceData] / [ThuribleData] /
 * [VesselsData] / [PosturesData], but grouped into decoration and cloths, with
 * a markings block below.
 */
data class AltarItem(
    val id: String,
    val nameEn: String,
    val nameFr: String,
    val descEn: String,
    val descFr: String,
)

object AltarData {
    const val INTRO_EN =
        "The altar is the heart of the church. It is at once the table of the Lord's Supper and the altar of sacrifice, where the offering of Calvary is made present — and so it stands for Christ himself."
    const val INTRO_FR =
        "L'autel est le cœur de l'église. Il est à la fois la table de la Cène et l'autel du sacrifice, où l'offrande du Calvaire est rendue présente — et il représente ainsi le Christ lui-même."

    const val MARKINGS_EN =
        "When a new altar is dedicated, the bishop anoints it with sacred chrism and incenses it. Five crosses are carved or marked into it — one at each corner and one in the centre — recalling the five wounds of Christ. In many altars the relics of a saint, often a martyr, are sealed within or beneath the altar stone, an ancient link to the tombs on which the early Church offered Mass."
    const val MARKINGS_FR =
        "Lors de la dédicace d'un autel neuf, l'évêque l'oint du saint chrême et l'encense. Cinq croix y sont gravées ou marquées — une à chaque angle et une au centre — rappelant les cinq plaies du Christ. Dans bien des autels, les reliques d'un saint, souvent un martyr, sont scellées dans la pierre d'autel ou sous elle, lien ancien avec les tombes sur lesquelles l'Église primitive célébrait la messe."

    const val KISS_TITLE_EN = "Why the kiss"
    const val KISS_TITLE_FR = "Pourquoi le baiser"
    const val KISS_BODY_EN =
        "The priest kisses the altar because it stands for Christ, the cornerstone. At a solemn Mass it is also honoured with incense — the same reverence shown to the Gospel and to the gifts."
    const val KISS_BODY_FR =
        "Le prêtre baise l'autel parce qu'il représente le Christ, la pierre angulaire. À la messe solennelle, on l'honore aussi de l'encens — la même révérence rendue à l'Évangile et aux offrandes."

    val decoration: List<AltarItem> = listOf(
        AltarItem("crucifix", "The Crucifix", "Le crucifix",
            "A cross bearing the figure of the crucified Christ stands on or near the altar, in view of the assembly.",
            "Une croix portant le Christ crucifié se dresse sur l'autel ou près de lui, en vue de l'assemblée."),
        AltarItem("candles", "Candles", "Les cierges",
            "At least two lit candles (up to six, or seven for a bishop) — a sign of reverence and festivity.",
            "Au moins deux cierges allumés (jusqu'à six, ou sept pour un évêque) — signe de révérence et de fête."),
        AltarItem("flowers", "Flowers", "Les fleurs",
            "May adorn the altar in moderation according to the season; never during Lent, and sparingly in Advent.",
            "Peuvent orner l'autel avec mesure selon le temps ; jamais durant le Carême, et sobrement pendant l'Avent."),
        AltarItem("frontal", "The Frontal", "Le devant d'autel",
            "An antependium may hang before the altar in the colour of the day.",
            "Un antependium peut être suspendu devant l'autel, à la couleur du jour."),
    )

    val cloths: List<AltarItem> = listOf(
        AltarItem("altarcloth", "Altar Cloth", "La nappe d'autel",
            "The white “fair linen” that covers the altar table; earlier custom used three cloths together.",
            "Le linge blanc qui couvre la table de l'autel ; l'ancien usage en employait trois superposés."),
        AltarItem("corporal", "The Corporal", "Le corporal",
            "A square cloth unfolded at the centre of the altar, on which the chalice and paten rest so no fragment of the Host is lost.",
            "Un linge carré déplié au centre de l'autel, sur lequel reposent le calice et la patène afin qu'aucune parcelle de l'hostie ne soit perdue."),
        AltarItem("purificator", "The Purificator", "Le purificatoire",
            "A small folded cloth used to wipe the chalice and the priest's fingers.",
            "Un petit linge plié servant à essuyer le calice et les doigts du prêtre."),
        AltarItem("pall", "The Pall", "La pale",
            "A stiff linen-covered square laid over the chalice to keep anything from falling into it.",
            "Un carton rigide couvert de lin posé sur le calice pour empêcher qu'il n'y tombe quelque chose."),
    )
}
