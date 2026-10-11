package com.ynotlabs.cathopedia.rosary

import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryMeter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

internal data class RosaryJournalDay(
    val date: LocalDate,
    val count: Int,
    val byMysterySet: Map<MysterySet, Int>,
)

internal fun rosaryJournalWeekStart(date: LocalDate): LocalDate =
    LocalDate.fromEpochDays(date.toEpochDays() - date.dayOfWeek.ordinal)

internal fun rosaryJournalWeekDays(start: LocalDate): List<LocalDate> =
    (0..6).map { LocalDate.fromEpochDays(start.toEpochDays() + it) }

@OptIn(ExperimentalTime::class)
internal fun rosaryJournalEntries(
    meter: RosaryMeter,
    weekStart: LocalDate,
    timeZone: TimeZone,
): List<RosaryJournalDay> {
    val end = LocalDate.fromEpochDays(weekStart.toEpochDays() + 6)
    fun localDate(timestamp: Long) = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(timeZone).date
    return if (meter.completions.isNotEmpty()) {
        meter.completions.groupBy { localDate(it.completedAt) }
            .filterKeys { it in weekStart..end }
            .map { (date, completions) ->
                RosaryJournalDay(date, completions.size, completions.groupingBy { it.mysterySet }.eachCount())
            }
    } else {
        // Older callers and previews can still provide timestamp-only history.
        meter.completionTimestamps.groupingBy(::localDate).eachCount()
            .filterKeys { it in weekStart..end }
            .map { (date, count) -> RosaryJournalDay(date, count, emptyMap()) }
    }.sortedByDescending { it.date }
}
