package com.ynotlabs.cathopedia.mass

/**
 * The parts of a thurible, for the dedicated Thurible anatomy screen — the
 * exact counterpart of [MonstranceData]. Descriptions are plain-language
 * summaries drawn from standard catechetical explanations of the censer.
 */
data class ThuriblePart(
    val id: String,
    val nameEn: String,
    val nameFr: String,
    val descEn: String,
    val descFr: String,
)

object ThuribleData {
    const val INTRO_EN =
        "The thurible is a sacred vessel used to burn incense. The rising smoke is a sign of our prayers ascending to God — “Let my prayer be counted as incense before you” (Psalm 141:2). It is used at the Entrance, the Gospel, the Preparation of the Gifts, the Eucharistic Prayer and Benediction."
    const val INTRO_FR =
        "L'encensoir est un vase sacré où l'on brûle l'encens. La fumée qui s'élève est le signe de nos prières qui montent vers Dieu — « Que ma prière devant toi s'élève comme un encens » (Psaume 141, 2). Il sert à l'entrée, à l'Évangile, à la préparation des dons, à la prière eucharistique et au salut du Saint-Sacrement."

    val parts: List<ThuriblePart> = listOf(
        ThuriblePart("finial", "Finial (Cross)", "Le fleuron (la croix)",
            "The cross on top, a reminder that our prayers are offered in the name of Christ.",
            "La croix au sommet, rappel que nos prières sont offertes au nom du Christ."),
        ThuriblePart("lid", "Lid (Cover)", "Le couvercle",
            "The perforated cover that lets the smoke rise, symbolising prayers ascending to heaven.",
            "Le couvercle perforé qui laisse monter la fumée, symbole des prières qui s'élèvent vers le ciel."),
        ThuriblePart("charcoal", "Charcoal Plate", "La coupelle à charbon",
            "Holds the burning charcoal on which the incense is placed.",
            "Reçoit le charbon ardent sur lequel on dépose l'encens."),
        ThuriblePart("bowl", "Censer Bowl", "La coupe (cassolette)",
            "The cup that holds the charcoal and incense — the heart of the thurible.",
            "Le récipient qui contient le charbon et l'encens — le cœur de l'encensoir."),
        ThuriblePart("chains", "Chains", "Les chaînes",
            "Usually three, for faith, hope and love; they balance the thurible as it is swung.",
            "Au nombre de trois d'ordinaire, pour la foi, l'espérance et la charité ; elles équilibrent l'encensoir quand on le balance."),
        ThuriblePart("ring", "Suspension Ring", "L'anneau de suspension",
            "The ring at the top from which the chains hang.",
            "L'anneau supérieur d'où pendent les chaînes."),
        ThuriblePart("handle", "Handle", "La poignée",
            "Used by the thurifer to raise the lid and swing the thurible during the liturgy.",
            "Sert au thuriféraire à soulever le couvercle et à balancer l'encensoir pendant la liturgie."),
    )
}
