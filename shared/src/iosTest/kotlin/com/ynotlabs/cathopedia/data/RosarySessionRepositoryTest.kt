package com.ynotlabs.cathopedia.data

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.model.MysterySet
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Exercises RosarySessionRepository against a real in-memory SQLite driver on
 * iosSimulatorArm64Test — the same no-cost-driver rationale as
 * ContentLoaderHubSeedingTest. The resume test reopens the *same* named
 * in-memory database from a second driver to stand in for a process kill.
 */
@OptIn(ExperimentalTime::class)
class RosarySessionRepositoryTest {

    /**
     * A named in-memory database lives as long as one connection stays open
     * (SQLite mode=memory&cache=shared). Opening a second driver on the same
     * name — while the first is still open — is exactly "reopen after kill".
     */
    private fun repoOn(name: String): RosarySessionRepository {
        val driver = NativeSqliteDriver(
            schema = CathopediaDatabase.Schema,
            name = "$name.db",
            onConfiguration = { it.copy(inMemory = true) },
        )
        return RosarySessionRepository(CathopediaDatabase(driver))
    }

    private fun uniqueName() = "rosary_session_test_${Random.nextLong()}"

    private fun millis(iso: String) = Instant.parse(iso).toEpochMilliseconds()

    @Test
    fun interruptedRosaryResumesAtTheExactBead() = runBlocking {
        val name = uniqueName()
        val opened = repoOn(name) // stays open for the DB's lifetime

        val id = opened.startSession(MysterySet.JOYFUL, startedAt = 1_000L)
        // Advance 23 steps, saving on each — the last save is what must survive.
        for (step in 1..23) opened.saveProgress(id, currentStepIndex = step, decadesCompleted = step / 13, durationSeconds = step * 10L)

        // "Kill the process and reopen": a brand-new repository/driver on the same DB.
        val reopened = repoOn(name)
        val resumed = reopened.resumeInProgress()

        assertNotNull(resumed, "an unfinished session must be offered on reopen")
        assertEquals(id, resumed.id)
        assertEquals(MysterySet.JOYFUL, resumed.mysterySet)
        assertEquals(23, resumed.currentStepIndex, "reopens on the exact bead")
        assertEquals(23 * 10L, resumed.durationSeconds)
    }

    @Test
    fun completedSessionsAreNotOfferedForResume() = runBlocking {
        val repo = repoOn(uniqueName())
        val id = repo.startSession(MysterySet.LUMINOUS, startedAt = 2_000L)
        repo.complete(id, currentStepIndex = 74, decadesCompleted = 5, durationSeconds = 900, completedAt = 3_000L)
        assertNull(repo.resumeInProgress())
    }

    @Test
    fun mostRecentUnfinishedSessionWins() = runBlocking {
        val repo = repoOn(uniqueName())
        repo.startSession(MysterySet.JOYFUL, startedAt = 10L)
        val newer = repo.startSession(MysterySet.SORROWFUL, startedAt = 20L)
        assertEquals(newer, repo.resumeInProgress()?.id)
    }

    @Test
    fun completingIncrementsCumulativeTotalAndPerSetBreakdown() = runBlocking {
        val repo = repoOn(uniqueName())
        val now = millis("2026-08-25T09:00:00Z")

        val a = repo.startSession(MysterySet.GLORIOUS, startedAt = now)
        repo.complete(a, currentStepIndex = 74, decadesCompleted = 5, durationSeconds = 800, completedAt = now)
        val b = repo.startSession(MysterySet.GLORIOUS, startedAt = now)
        repo.complete(b, currentStepIndex = 74, decadesCompleted = 5, durationSeconds = 820, completedAt = now)
        val c = repo.startSession(MysterySet.JOYFUL, startedAt = now)
        repo.complete(c, currentStepIndex = 74, decadesCompleted = 5, durationSeconds = 810, completedAt = now)

        val meter = repo.meter(now = now)
        assertEquals(3, meter.totalCompleted)
        assertEquals(2L, meter.byMysterySet[MysterySet.GLORIOUS])
        assertEquals(1L, meter.byMysterySet[MysterySet.JOYFUL])
        assertEquals(3, meter.completionTimestamps.size)
    }

    @Test
    fun thisMonthCountExcludesEarlierMonths() = runBlocking {
        val repo = repoOn(uniqueName())
        val now = millis("2026-08-25T09:00:00Z")

        // Mid-month on both sides so the assertion holds in any runner time zone
        // (the boundary is local midnight of the 1st, not a fixed UTC instant).
        val thisMonth = repo.startSession(MysterySet.JOYFUL, startedAt = now)
        repo.complete(thisMonth, 74, 5, 800, completedAt = millis("2026-08-15T12:00:00Z"))
        val lastMonth = repo.startSession(MysterySet.JOYFUL, startedAt = now)
        repo.complete(lastMonth, 74, 5, 800, completedAt = millis("2026-07-15T12:00:00Z"))

        val meter = repo.meter(now = now)
        assertEquals(2, meter.totalCompleted, "cumulative total spans months")
        assertEquals(1, meter.completedThisMonth, "only August completion counts this month")
    }

    @Test
    fun abandonStaleDropsOldUnfinishedButKeepsRecentAndCompleted() = runBlocking {
        val repo = repoOn(uniqueName())
        val now = millis("2026-08-25T09:00:00Z")
        val day = 24L * 60 * 60 * 1000

        val stale = repo.startSession(MysterySet.JOYFUL, startedAt = now - 10 * day)
        val recent = repo.startSession(MysterySet.SORROWFUL, startedAt = now - 2 * 60 * 60 * 1000)
        val completedOld = repo.startSession(MysterySet.GLORIOUS, startedAt = now - 30 * day)
        repo.complete(completedOld, 74, 5, 900, completedAt = now - 29 * day)

        repo.abandonStale(olderThanMillis = 7 * day, now = now)

        assertNull(repo.session(stale), "a 10-day-old unfinished session is abandoned")
        assertNotNull(repo.session(recent), "a 2-hour-old session is kept")
        assertNotNull(repo.session(completedOld), "a completed session is never abandoned, however old")
        Unit
    }

    @Test
    fun freshDatabaseHasAnEmptyMeterAndNoResume() = runBlocking {
        val repo = repoOn(uniqueName())
        assertNull(repo.resumeInProgress())
        val meter = repo.meter(now = 0L)
        assertEquals(0, meter.totalCompleted)
        assertEquals(0, meter.completedThisMonth)
        assertTrue(meter.byMysterySet.isEmpty())
        assertTrue(meter.completionTimestamps.isEmpty())
    }
}
