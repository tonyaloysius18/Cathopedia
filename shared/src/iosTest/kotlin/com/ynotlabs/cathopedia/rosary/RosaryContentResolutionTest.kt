package com.ynotlabs.cathopedia.rosary

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ynotlabs.cathopedia.data.ContentLoader
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.model.MysterySet
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * The brief's A2 requirement that "every prayerKey resolves" — checked against
 * the real compiled catalog, not a fixture, so it also guards the EN + FR
 * seeding of the Rosary's prayers and all twenty mystery titles. Runs on
 * iosSimulatorArm64Test, matching ContentLoaderHubSeedingTest: the one target
 * here with a no-cost in-memory driver.
 *
 * Every prayer slug and every mystery id the sequence engine can emit (across
 * all four sets) must have text in both shipped languages; a missing French row
 * is exactly the gap this task filled and must not silently reappear.
 */
class RosaryContentResolutionTest {

    private fun freshDatabase(): CathopediaDatabase {
        val driver = NativeSqliteDriver(
            schema = CathopediaDatabase.Schema,
            name = "rosary_resolution_test_${Random.nextLong()}.db",
            onConfiguration = { it.copy(inMemory = true) },
        )
        return CathopediaDatabase(driver)
    }

    private val allSteps = MysterySet.entries.flatMap { RosarySequence(it).steps }
    private val prayerSlugs = allSteps.map { it.prayerSlug }.distinct().sorted()
    private val mysteryIds = allSteps.mapNotNull { it.mysteryId }.distinct().sorted()

    @Test
    fun everyRosaryPrayerResolvesInEnglishAndFrench() = runBlocking {
        val db = freshDatabase()
        ContentLoader.loadIfEmpty(db)

        assertTrue(prayerSlugs.isNotEmpty())
        for (slug in prayerSlugs) {
            val langs = db.prayerQueries.selectPrayerLanguages(slug).executeAsList().toSet()
            assertTrue("en" in langs, "prayer '$slug' has no English text")
            assertTrue("fr" in langs, "prayer '$slug' has no French text")
        }
    }

    @Test
    fun allTwentyMysteriesResolveInEnglishAndFrench() = runBlocking {
        val db = freshDatabase()
        ContentLoader.loadIfEmpty(db)

        assertTrue(mysteryIds.size == 20, "expected 20 distinct mysteries, got ${mysteryIds.size}")
        for (id in mysteryIds) {
            val en = db.mysteryQueries.selectMysteryDetail("en", id).executeAsOneOrNull()
            val fr = db.mysteryQueries.selectMysteryDetail("fr", id).executeAsOneOrNull()
            assertNotNull(en, "mystery '$id' has no English text")
            assertNotNull(fr, "mystery '$id' has no French text")
            assertTrue(fr.title.isNotBlank(), "mystery '$id' French title is blank")
            assertTrue(fr.fruit.isNotBlank(), "mystery '$id' French fruit is blank")
        }
    }
}
