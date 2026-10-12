package com.ynotlabs.cathopedia.saints

import com.ynotlabs.cathopedia.model.ContentSummary
import kotlinx.datetime.LocalDate

data class SaintsFeastSelection(val date: LocalDate, val saints: List<ContentSummary>, val isToday: Boolean)

/** Uses the recorded anniversary, not the liturgical rank or observance of a regional calendar. */
fun saintsFeastSelection(saints: List<ContentSummary>, today: LocalDate): SaintsFeastSelection? {
    val dated = saints.mapNotNull { saint ->
        val parts = saint.feastDay?.split('-') ?: return@mapNotNull null
        if (parts.size != 2) return@mapNotNull null
        val month = parts[0].toIntOrNull() ?: return@mapNotNull null
        val day = parts[1].toIntOrNull() ?: return@mapNotNull null
        // Include the next leap year for anniversaries on February 29.
        val date = (today.year..today.year + 4).firstNotNullOfOrNull { year ->
            runCatching { LocalDate(year, month, day) }.getOrNull()?.takeIf { it >= today }
        } ?: return@mapNotNull null
        saint to date
    }
    val next = dated.minOfOrNull { it.second } ?: return null
    return SaintsFeastSelection(next, dated.filter { it.second == next }.map { it.first }, next == today)
}

/** Editorial selection, independent of a saint's popularity or liturgical rank. */
fun saintsFeatured(saints: List<ContentSummary>, excludedIds: Set<String> = emptySet()): List<ContentSummary> {
    val byId = saints.associateBy { it.id }
    return listOf("padre-pio", "therese-of-lisieux", "francis-of-assisi", "anthony-of-padua",
        "teresa-of-calcutta", "carlo-acutis")
        .filterNot { it in excludedIds }.mapNotNull(byId::get)
}
