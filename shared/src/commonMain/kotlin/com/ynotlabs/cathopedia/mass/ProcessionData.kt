package com.ynotlabs.cathopedia.mass

/**
 * One minister in the Entrance Procession of the Mass, in the order they walk.
 * Plain-language descriptions mirror the hub's procession stepper content, kept
 * as Kotlin strings so the carousel screen is self-contained (as with
 * [com.ynotlabs.cathopedia.sacraments.SacramentsData]).
 */
data class Minister(
    val number: Int,
    val id: String,
    val carriesEn: String,
    val carriesFr: String,
    val titleEn: String,
    val titleFr: String,
    val descriptionEn: String,
    val descriptionFr: String,
)

object ProcessionData {
    val ministers: List<Minister> = listOf(
        Minister(
            number = 1, id = "thurifer",
            carriesEn = "Incense", carriesFr = "L'encens",
            titleEn = "Thurible Bearer", titleFr = "Le thuriféraire",
            descriptionEn = "The thurible bearer leads the procession with burning incense — a sign of our prayers rising up to God, and a mark of honour for the sacred things that follow.",
            descriptionFr = "Le thuriféraire ouvre la procession avec l'encens qui brûle — signe de nos prières qui montent vers Dieu, et marque d'honneur pour les choses saintes qui suivent.",
        ),
        Minister(
            number = 2, id = "crossbearer",
            carriesEn = "The Cross", carriesFr = "La croix",
            titleEn = "Cross Bearer", titleFr = "Le porte-croix",
            descriptionEn = "The processional cross leads the ministers forward, the sign of Christ who goes before us. All who follow walk behind the Cross toward the altar.",
            descriptionFr = "La croix de procession conduit les ministres, signe du Christ qui nous précède. Tous ceux qui suivent marchent derrière la Croix vers l'autel.",
        ),
        Minister(
            number = 3, id = "candlebearer",
            carriesEn = "The Light", carriesFr = "La lumière",
            titleEn = "Candle Bearers", titleFr = "Les porte-cierges",
            descriptionEn = "Acolytes carry lit candles on either side of the cross — Christ, the Light of the World, whose light the Church carries into the assembly.",
            descriptionFr = "Les acolytes portent des cierges allumés de part et d'autre de la croix — le Christ, Lumière du monde, dont l'Église porte la lumière dans l'assemblée.",
        ),
        Minister(
            number = 4, id = "bellringer",
            carriesEn = "The Bell", carriesFr = "La clochette",
            titleEn = "Bell Ringer", titleFr = "Le sonneur",
            descriptionEn = "A bell may be rung to announce the procession and to call the faithful to worship, gathering the whole assembly into one attentive prayer.",
            descriptionFr = "On peut sonner une clochette pour annoncer la procession et appeler les fidèles à la prière, rassemblant toute l'assemblée en une seule prière attentive.",
        ),
        Minister(
            number = 5, id = "bookbearer",
            carriesEn = "The Word", carriesFr = "La Parole",
            titleEn = "Book Bearer", titleFr = "Le porte-évangéliaire",
            descriptionEn = "The Book of the Gospels is carried aloft — the Word of God proclaimed in the Mass. It is set upon the altar until the moment of the Gospel.",
            descriptionFr = "On porte l'évangéliaire élevé — la Parole de Dieu proclamée dans la messe. On le dépose sur l'autel jusqu'au moment de l'Évangile.",
        ),
        Minister(
            number = 6, id = "priest",
            carriesEn = "The Presider", carriesFr = "Le président",
            titleEn = "The Priest", titleFr = "Le prêtre",
            descriptionEn = "The priest follows last as the one who presides. He represents Christ himself, the Head of the Church, and it is he who will offer the sacrifice at the altar.",
            descriptionFr = "Le prêtre suit en dernier comme celui qui préside. Il représente le Christ lui-même, la Tête de l'Église, et c'est lui qui offrira le sacrifice à l'autel.",
        ),
    )
}
