package com.ynotlabs.cathopedia.mass

/**
 * The parts of a monstrance, for the dedicated Monstrance anatomy screen.
 * Descriptions are plain-language summaries drawn from standard catechetical
 * explanations of the vessel; kept as Kotlin strings so the screen is
 * self-contained, as with [ProcessionData] / [SacramentsData].
 */
data class MonstrancePart(
    val id: String,
    val nameEn: String,
    val nameFr: String,
    val descEn: String,
    val descFr: String,
)

object MonstranceData {
    const val INTRO_EN =
        "The monstrance — from the Latin monstrare, “to show” — is a sacred vessel that holds the consecrated Host up for the faithful to see and adore. It is used for Eucharistic Adoration, Benediction and processions. The monstrance itself is not worshipped; it is a sacred vessel that points us to Jesus, truly present in the Eucharist."
    const val INTRO_FR =
        "L'ostensoir — du latin monstrare, « montrer » — est un vase sacré qui présente l'hostie consacrée pour que les fidèles la voient et l'adorent. Il sert à l'adoration eucharistique, au salut du Saint-Sacrement et aux processions. L'ostensoir lui-même n'est pas adoré ; c'est un vase sacré qui nous tourne vers Jésus, vraiment présent dans l'Eucharistie."

    val parts: List<MonstrancePart> = listOf(
        MonstrancePart("cross", "Cross", "La croix",
            "At the very top, the sign of our salvation in Christ.",
            "Tout en haut, le signe de notre salut dans le Christ."),
        MonstrancePart("sunburst", "Sunburst", "La gloire rayonnante",
            "The rays of glory that represent the radiance of Christ, the “Sun of Justice.”",
            "Les rayons de gloire qui figurent l'éclat du Christ, le « Soleil de justice »."),
        MonstrancePart("ostensorium", "Ostensorium", "L'ostensoir (la fenêtre)",
            "The central circular opening, or window, that displays the Eucharist for adoration.",
            "L'ouverture ronde centrale, la fenêtre, qui présente l'Eucharistie à l'adoration."),
        MonstrancePart("glass_front", "Glass Door (Front)", "La vitre (avant)",
            "Protects the Eucharist while allowing it to be seen.",
            "Protège l'Eucharistie tout en permettant de la voir."),
        MonstrancePart("lunula", "Lunula", "La lunule",
            "The crescent holder that keeps the host in place; its shape resembles the moon, a symbol of Our Lady.",
            "Le support en croissant qui maintient l'hostie ; sa forme rappelle la lune, symbole de Notre-Dame."),
        MonstrancePart("host", "The Host", "L'hostie",
            "The Blessed Sacrament — the Body, Blood, Soul and Divinity of Jesus Christ.",
            "Le Saint-Sacrement — le Corps, le Sang, l'Âme et la Divinité de Jésus-Christ."),
        MonstrancePart("glass_back", "Glass Door (Back)", "La vitre (arrière)",
            "Seals and protects the Eucharist from the rear.",
            "Ferme et protège l'Eucharistie par l'arrière."),
        MonstrancePart("back_cover", "Back Cover", "Le couvercle arrière",
            "Provides protection and reverence for the Eucharist.",
            "Assure la protection et la révérence dues à l'Eucharistie."),
        MonstrancePart("node", "Node", "Le nœud",
            "The knop on the stem, worked with wheat and vine — the Bread of Life and the True Vine.",
            "Le renflement de la tige, orné de blé et de vigne — le Pain de vie et la vraie Vigne."),
        MonstrancePart("shaft", "Shaft", "La tige",
            "The stem that connects the base to the sunburst — our journey in faith, lifting our hearts to Christ.",
            "Le fût qui relie le pied à la gloire — notre marche dans la foi, élevant nos cœurs vers le Christ."),
        MonstrancePart("base", "Base", "Le pied",
            "Provides stability and honours the sacred presence of Jesus.",
            "Assure la stabilité et honore la présence sacrée de Jésus."),
    )
}
