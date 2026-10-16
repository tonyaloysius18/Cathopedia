package com.ynotlabs.cathopedia.data

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ynotlabs.cathopedia.content.*
import com.ynotlabs.cathopedia.content.model.*
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.model.SearchTarget
import com.ynotlabs.cathopedia.liturgical.MassPart
import com.ynotlabs.cathopedia.liturgical.MassSection
import com.ynotlabs.cathopedia.liturgical.OrderOfMassText
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.*

/** Real FTS5 and the production seeding/repository path, with no external services. */
class GlobalSearchTest {
    private fun fixture() = ContentCatalog(
        churches = listOf(ChurchContent("test-church", address = "77 Pilgrim Avenue", text = mapOf(
            "en" to LocalizedText("Test Church", "", ""),
        ))),
        saints = listOf(SaintContent("test-saint", text = mapOf(
            "en" to LocalizedText("Test Saint", "", "", patronage = "Lighthouse keepers"),
            "fr" to LocalizedText("Sainte Test", "Résumé", "", patronage = "Gardiens de phare"),
        ))),
        prayers = listOf(PrayerContent("our-father", "everyday", 1, text = mapOf(
            "en" to PrayerLocalizedText("Our Father", bodyMd = "Hallowed be thy name", about = "The Lord's Prayer", source = "Test fixture"),
            "la" to PrayerLocalizedText("Pater Noster", bodyMd = "Sanctificetur nomen tuum", source = "Test fixture"),
        ))),
        mysteries = listOf(MysteryContent("presentation", "joyful", 4, "Lk 2:22-38", mapOf(
            "en" to MysteryLocalizedText("Presentation", "obedience", "Simeon welcomes the infant Jesus"),
        ))),
        hubs = listOf(HubDocument(1, 1, Hub("mass", "mass", "hub.title", sortOrder = 1), sections = listOf(
            Section("thurible", 1, SectionStatus.PUBLISHED, SectionLayout.ARTICLES, "section.title", articleIds = listOf("art.thurible.overview")),
            Section("law", 2, SectionStatus.PUBLISHED, SectionLayout.ARTICLES, "law.section", articleIds = listOf("canon")),
            Section("diagram", 3, SectionStatus.PUBLISHED, SectionLayout.DIAGRAM, "diagram.title", diagramIds = listOf("parts")),
            Section("stub", 4, SectionStatus.STUB, SectionLayout.ARTICLES, "stub.title", articleIds = listOf("hidden")),
        ), articles = listOf(
            Article("art.thurible.overview", "thurible", 1, "thurible.title", "thurible.lead", blocks = listOf(ParagraphBlock("thurible.body"), DiagramRefBlock("parts"))),
            Article("canon", "law", 1, "law.title", blocks = listOf(ParagraphBlock("law.body"))),
            Article("hidden", "stub", 1, "stub.title", blocks = emptyList()),
        ), diagrams = listOf(Diagram("parts", asset = "test.svg", aspectRatio = 1f,
            hotspots = listOf(Hotspot("bowl", "bowl.title", "bowl.body", shape = CircleShape(.5f, .5f, .1f))))))),
        hubStrings = listOf(
            HubStrings(1, "mass", "en", 1, strings = mapOf(
                "hub.title" to "Holy Mass", "section.title" to "Thuribles", "thurible.title" to "The Thurible",
                "thurible.lead" to "An incense vessel", "thurible.body" to "Chains and a charcoal plate",
                "law.section" to "Canon Law", "law.title" to "What Is Canon Law", "law.body" to "The Church's legal system",
                "diagram.title" to "Parts of the vessel", "bowl.title" to "Bowl", "bowl.body" to "The removable brazier",
                "stub.title" to "Unpublished secret", "screen.mass.thurible.lid.desc" to "A perforated dome",
                "screen.stations.one.meditation" to "Veronica wipes the face of Jesus",
            )),
            HubStrings(1, "mass", "fr", 1, strings = mapOf("thurible.title" to "L'encensoir")),
        ),
    )

    private fun massTexts() = mapOf("en" to OrderOfMassText(
        language = "en", note = "", missalPending = "", readingPending = "", homilyPending = "",
        goodFriday = "", holySaturday = "",
        sections = listOf(MassSection("intro", "Introductory Rites", listOf(
            MassPart("kyrie", "Kyrie", rubric = "The penitential act", text = "Lord, have mercy"),
        ))),
    ))

    private fun withDatabase(block: (CathopediaDatabase, CathopediaRepository) -> Unit) {
        val driver = NativeSqliteDriver(CathopediaDatabase.Schema, "global_search_${Random.nextLong()}.db",
            onConfiguration = { it.copy(inMemory = true) })
        try {
            // Enable cascades to catch an accidental REPLACE that destroys prayer user state.
            driver.execute(null, "PRAGMA foreign_keys = ON", 0)
            val db = CathopediaDatabase(driver)
            db.transaction { ContentLoader.insert(db, fixture(), massTexts()) }
            block(db, CathopediaRepository(db))
        } finally { driver.close() }
    }

    @Test fun searchesTopicsBodiesDiagramReferencesAndPrayers() = withDatabase { _, repository -> runBlocking {
        assertEquals(SearchTarget.Article("mass", "art.thurible.overview"), repository.searchAll("turnible", "en").first().target)
        assertEquals(SearchTarget.Article("mass", "canon"), repository.searchAll("cannon law", "en").first().target)
        assertTrue(repository.searchAll("charcoal plate", "en").any { it.target == SearchTarget.Article("mass", "art.thurible.overview") })
        assertTrue(repository.searchAll("removable brazier", "en").any { it.target == SearchTarget.Article("mass", "art.thurible.overview") })
        assertTrue(repository.searchAll("perforated dome", "en").any { it.target == SearchTarget.Article("mass", "art.thurible.overview") })
        assertEquals(SearchTarget.Prayer("our-father"), repository.searchAll("hallowed", "en").single().target)
        // A phrase may also occur in a guide; the matching prayer must still be included.
        assertTrue(repository.searchAll("Lord's Prayer", "en").any { it.target == SearchTarget.Prayer("our-father") })
        assertEquals(SearchTarget.Prayer("our-father"), repository.searchAll("sanctificetur", "en").single().target)
        assertEquals(SearchTarget.Guide("rosary"), repository.searchAll("Simeon welcomes", "en").single().target)
        assertEquals(SearchTarget.Guide("stations"), repository.searchAll("Veronica wipes", "en").single().target)
        assertTrue(repository.searchAll("chasuble", "en").any { it.target == SearchTarget.Guide("vestments") })
        assertTrue(repository.searchAll("Kyrie", "en").any { it.target == SearchTarget.Guide("mass") })
        assertTrue(repository.searchAll("Pilgrim Avenue", "en").any { it.target == SearchTarget.Entity(com.ynotlabs.cathopedia.model.ContentType.CHURCH, "test-church") })
        assertTrue(repository.searchAll("Lighthouse keepers", "en").any { it.target == SearchTarget.Entity(com.ynotlabs.cathopedia.model.ContentType.SAINT, "test-saint") })
        assertTrue(repository.searchAll("Unpublished secret", "en").isEmpty())
    } }

    @Test fun languageFallbackDeduplicatesAndKeepsLocalizedTitle() = withDatabase { _, repository -> runBlocking {
        val results = repository.searchAll("charcoal", "fr")
        assertEquals("L'encensoir", results.single().name)
        assertEquals("Our Father", repository.searchAll("hallowed", "fr").single().name)
        assertEquals("The Thurible", repository.searchAll("thurible", "de").first().name)
        // Found through the English index, shown with the reader's own name and summary.
        val saint = repository.searchAll("Lighthouse keepers", "fr").single()
        assertEquals("Sainte Test" to "Résumé", saint.name to saint.summary)
    } }

    @Test fun punctuationAndSpellingNeverBecomeFtsSyntax() = withDatabase { _, repository -> runBlocking {
        assertTrue(repository.searchAll("\"canon-law\"?", "en").isNotEmpty())
        assertTrue(repository.searchAll(" :*()\" ", "en").isEmpty())
        assertTrue(repository.searchAll("name: OR zxqneverword:*", "en").isEmpty())
        assertEquals("Our Father", repository.searchAll("our fathre", "en").single().name)
        assertEquals("Our Father", repository.searchPrayers("hallowed?", "en").single().title)
        assertTrue(repository.search("thurible", "en").isEmpty()) // entity-only API safely ignores article/guide tags
    } }

    @Test fun reseedingReplacesIndexAndPreservesPrayerState() = withDatabase { db, repository -> runBlocking {
        db.prayerUserStateQueries.setPrayerFavorite(1, "our-father")
        db.prayerUserStateQueries.recordPrayerOpened(123, "our-father")
        db.searchQueries.insertSearchEntry("article", "mass|obsolete", "en", "Obsolete", "", "")
        db.transaction { ContentLoader.insert(db, fixture(), massTexts()) }
        assertTrue(repository.searchAll("Obsolete", "en").isEmpty())
        assertEquals(1, db.searchQueries.search("hallowed", "en", "prayer", 50).executeAsList().size)
        val state = db.prayerUserStateQueries.selectPrayerUserState("our-father").executeAsOne()
        assertEquals(1L, state.isFavorite)
        assertEquals(1L, state.timesPrayed)
        assertEquals(123L, state.lastOpenedAt)
    } }
}
