package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.db.Rosary_session
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.RosaryMeter
import com.ynotlabs.cathopedia.model.RosarySessionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Persistence for Rosary sessions (rosary-screen-brief.md, A3). Separate from
 * [CathopediaRepository] — that one is content (bundled, read-mostly); this is
 * purely user devotional state written on every bead advance. Both sit on the
 * one [CathopediaDatabase], per the single-database pattern.
 *
 * Every method hops to [Dispatchers.Default] like [CathopediaRepository]; the
 * timestamped ones take the current time as a defaulted parameter so tests can
 * pin "now" without a fake clock.
 */
@OptIn(ExperimentalTime::class)
class RosarySessionRepository(private val database: CathopediaDatabase) {

    private val queries get() = database.rosarySessionQueries

    /** Starts a fresh session at step 0 and returns its id. */
    suspend fun startSession(
        mysterySet: MysterySet,
        startedAt: Long = now(),
    ): String = withContext(Dispatchers.Default) {
        val id = newId(startedAt)
        queries.startSession(id, mysterySet.tag, startedAt)
        id
    }

    /** Cheap per-advance save — a single-row UPDATE, safe to call on every bead. */
    suspend fun saveProgress(
        id: String,
        currentStepIndex: Int,
        decadesCompleted: Int,
        durationSeconds: Long,
    ) = withContext(Dispatchers.Default) {
        queries.saveProgress(
            currentStepIndex = currentStepIndex.toLong(),
            decadesCompleted = decadesCompleted.toLong(),
            durationSeconds = durationSeconds,
            id = id,
        )
    }

    /** Marks the session finished; its completed_at is what the meter counts. */
    suspend fun complete(
        id: String,
        currentStepIndex: Int,
        decadesCompleted: Int,
        durationSeconds: Long,
        completedAt: Long = now(),
    ) = withContext(Dispatchers.Default) {
        queries.complete(
            completedAt = completedAt,
            currentStepIndex = currentStepIndex.toLong(),
            decadesCompleted = decadesCompleted.toLong(),
            durationSeconds = durationSeconds,
            id = id,
        )
    }

    /** Discards unfinished sessions older than [olderThanMillis] (default 7 days). */
    suspend fun abandonStale(
        olderThanMillis: Long = SEVEN_DAYS_MILLIS,
        now: Long = now(),
    ) = withContext(Dispatchers.Default) {
        queries.abandonStale(cutoff = now - olderThanMillis)
    }

    /** The session to reopen, if any — the most recently started unfinished one. */
    suspend fun resumeInProgress(): RosarySessionState? = withContext(Dispatchers.Default) {
        queries.resumeInProgress().executeAsOneOrNull()?.toState()
    }

    /** By id — used by resume flows and tests. */
    suspend fun session(id: String): RosarySessionState? = withContext(Dispatchers.Default) {
        queries.selectSession(id).executeAsOneOrNull()?.toState()
    }

    /** The full landing-screen meter (A7), computed as of [now]. */
    suspend fun meter(now: Long = now()): RosaryMeter = withContext(Dispatchers.Default) {
        val total = queries.countCompleted().executeAsOne()
        val thisMonth = queries.countCompletedSince(firstOfMonthMillis(now)).executeAsOne()
        val bySet = queries.countCompletedByMysterySet().executeAsList()
            .associate { MysterySet.fromTag(it.mystery_set) to it.total }
        val timestamps = queries.selectCompletedTimestamps().executeAsList().filterNotNull()
        RosaryMeter(
            totalCompleted = total,
            completedThisMonth = thisMonth,
            byMysterySet = bySet,
            completionTimestamps = timestamps,
        )
    }

    private fun now(): Long = Clock.System.now().toEpochMilliseconds()

    /** Sortable, collision-resistant id: start-time millis + a random suffix. */
    private fun newId(startedAt: Long): String =
        "rs_${startedAt}_${Random.nextLong().toString(16).removePrefix("-")}"

    private fun Rosary_session.toState() = RosarySessionState(
        id = id,
        mysterySet = MysterySet.fromTag(mystery_set),
        startedAt = started_at,
        currentStepIndex = current_step_index.toInt(),
        decadesCompleted = decades_completed.toInt(),
        durationSeconds = duration_seconds,
    )

    companion object {
        private const val SEVEN_DAYS_MILLIS = 7L * 24 * 60 * 60 * 1000

        /**
         * Epoch millis of midnight on the first of [now]'s month, in the system
         * time zone — the boundary for "completed this month". Pulled out and
         * internal so it can be unit-tested without a database.
         */
        internal fun firstOfMonthMillis(
            now: Long,
            timeZone: TimeZone = TimeZone.currentSystemDefault(),
        ): Long {
            val date: LocalDateTime = Instant.fromEpochMilliseconds(now).toLocalDateTime(timeZone)
            val firstOfMonth = LocalDate(date.year, date.month, 1)
            return firstOfMonth.atStartOfDayIn(timeZone).toEpochMilliseconds()
        }
    }
}
