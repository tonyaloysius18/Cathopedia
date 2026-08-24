package com.ynotlabs.cathopedia.data

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.ynotlabs.cathopedia.content.ContentCatalog
import com.ynotlabs.cathopedia.content.model.Article
import com.ynotlabs.cathopedia.content.model.CircleShape
import com.ynotlabs.cathopedia.content.model.Diagram
import com.ynotlabs.cathopedia.content.model.HeadingBlock
import com.ynotlabs.cathopedia.content.model.Hotspot
import com.ynotlabs.cathopedia.content.model.Hub
import com.ynotlabs.cathopedia.content.model.HubDocument
import com.ynotlabs.cathopedia.content.model.HubStrings
import com.ynotlabs.cathopedia.content.model.ParagraphBlock
import com.ynotlabs.cathopedia.content.model.RectShape
import com.ynotlabs.cathopedia.content.model.Section
import com.ynotlabs.cathopedia.content.model.SectionLayout
import com.ynotlabs.cathopedia.content.model.SectionStatus
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.random.Random

/**
 * Exercises ContentLoader.insert (the real hub-seeding path — SQLDelight rows in, not just JSON
 * decode) against a real in-memory SQLite driver. Runs on iosSimulatorArm64Test/iosArm64Test:
 * that's the one KMP target here where an in-memory driver doesn't cost a new dependency — the
 * Android side would need Robolectric or a JVM target, which the topic-hub brief's own "no new
 * third-party dependencies" ruled out (see docs/briefs/topic-hubs.md, T2).
 *
 * Builds the HubDocument as real Kotlin objects rather than decoding content/hubs/holy_see.json,
 * so this test isn't coupled to that file's exact section/article counts as the content grows.
 */
class ContentLoaderHubSeedingTest {

    /**
     * A named in-memory SQLite database is still shared-by-name for the process's lifetime (SQLite's
     * `mode=memory&cache=shared` semantics) — reusing one name across tests leaked rows from an
     * earlier test into a later one here. Each call gets its own name so tests are truly isolated.
     */
    private fun freshDatabase(): CathopediaDatabase {
        val driver = NativeSqliteDriver(
            schema = CathopediaDatabase.Schema,
            name = "content_loader_test_${Random.nextLong()}.db",
            onConfiguration = { it.copy(inMemory = true) },
        )
        return CathopediaDatabase(driver)
    }

    private fun fixtureHub(): HubDocument = HubDocument(
        schemaVersion = 1,
        contentVersion = 1,
        hub = Hub(
            id = "test.hub",
            slug = "test-hub",
            titleKey = "hub.test.title",
            subtitleKey = "hub.test.subtitle",
            sortOrder = 1,
        ),
        sections = listOf(
            Section(
                id = "test.section.articles",
                sortOrder = 1,
                status = SectionStatus.PUBLISHED,
                layout = SectionLayout.ARTICLES,
                titleKey = "section.articles.title",
            ),
            Section(
                id = "test.section.diagram",
                sortOrder = 2,
                status = SectionStatus.PUBLISHED,
                layout = SectionLayout.DIAGRAM,
                titleKey = "section.diagram.title",
                diagramIds = listOf("test.diagram"),
            ),
            Section(
                id = "test.section.stub",
                sortOrder = 3,
                status = SectionStatus.STUB,
                layout = SectionLayout.ARTICLES,
                titleKey = "section.stub.title",
            ),
        ),
        articles = listOf(
            Article(
                id = "test.article.one",
                sectionId = "test.section.articles",
                sortOrder = 1,
                titleKey = "article.one.title",
                blocks = listOf(
                    HeadingBlock(level = 2, textKey = "article.one.heading"),
                    ParagraphBlock(textKey = "article.one.body"),
                ),
            ),
            Article(
                id = "test.article.two",
                sectionId = "test.section.articles",
                sortOrder = 2,
                titleKey = "article.two.title",
                blocks = listOf(ParagraphBlock(textKey = "article.two.body")),
            ),
        ),
        diagrams = listOf(
            Diagram(
                id = "test.diagram",
                asset = "test/diagram.svg",
                aspectRatio = 1.5f,
                hotspots = listOf(
                    Hotspot(id = "hs.1", labelKey = "hotspot.one.label", order = 1, shape = RectShape(0f, 0f, 0.2f, 0.2f)),
                    Hotspot(id = "hs.2", labelKey = "hotspot.two.label", order = 2, shape = CircleShape(0.5f, 0.5f, 0.1f)),
                    Hotspot(id = "hs.3", labelKey = "hotspot.three.label", shape = RectShape(0.6f, 0.6f, 0.1f, 0.1f)),
                ),
            ),
        ),
    )

    private fun fixtureStrings(): List<HubStrings> = listOf(
        HubStrings(
            schemaVersion = 1,
            hubId = "test.hub",
            lang = "en",
            strings = mapOf(
                "hub.test.title" to "Test Hub",
                "hub.test.subtitle" to "English subtitle",
                "section.articles.title" to "Articles Section",
                "section.diagram.title" to "Diagram Section",
                "section.stub.title" to "Stub Section",
                "article.one.title" to "First Article",
                "article.one.heading" to "A Heading",
                "article.one.body" to "First article body, in English.",
                "article.two.title" to "Second Article",
                "article.two.body" to "Second article body, in English.",
                "hotspot.one.label" to "Hotspot One",
                "hotspot.two.label" to "Hotspot Two",
                "hotspot.three.label" to "Hotspot Three",
                // Only English has this one — exercises per-key fallback from fr.
                "english.only.key" to "Only in English",
            ),
        ),
        HubStrings(
            schemaVersion = 1,
            hubId = "test.hub",
            lang = "fr",
            strings = mapOf(
                "hub.test.title" to "Centre de test",
                // subtitleKey deliberately left untranslated to exercise fallback-to-en.
                "section.articles.title" to "Section Articles",
                "section.diagram.title" to "Section Diagramme",
                "section.stub.title" to "Section Ébauche",
                "article.one.title" to "Premier Article",
                "article.one.heading" to "Un Titre",
                "article.one.body" to "Corps du premier article, en français.",
                "article.two.title" to "Deuxième Article",
                "article.two.body" to "Corps du deuxième article, en français.",
                "hotspot.one.label" to "Point Un",
                "hotspot.two.label" to "Point Deux",
                "hotspot.three.label" to "Point Trois",
            ),
        ),
    )

    @Test
    fun seedsSectionsArticlesAndHotspots() {
        val database = freshDatabase()
        ContentLoader.insert(database, ContentCatalog(hubs = listOf(fixtureHub()), hubStrings = fixtureStrings()))

        val sections = database.hubContentQueries.selectSections("test.hub").executeAsList()
        assertEquals(3, sections.size)

        val articles = database.hubContentQueries.selectArticlesForSection("test.section.articles").executeAsList()
        assertEquals(2, articles.size)

        val hotspots = database.hubContentQueries.selectHotspots("test.diagram").executeAsList()
        assertEquals(3, hotspots.size)
    }

    @Test
    fun resolvesStringsWithPerKeyFallbackToEnglish() {
        val database = freshDatabase()
        ContentLoader.insert(database, ContentCatalog(hubs = listOf(fixtureHub()), hubStrings = fixtureStrings()))

        val frResolved = database.hubContentQueries.selectStrings(
            lang = "fr",
            fallbackLang = "en",
            keys = listOf("hub.test.title", "hub.test.subtitle"),
        ).executeAsList().associate { it.key to it.value_ }

        // Translated in fr.
        assertEquals("Centre de test", frResolved["hub.test.title"])
        // Missing in fr — falls back to the en value rather than showing blank.
        assertEquals("English subtitle", frResolved["hub.test.subtitle"])
    }

    @Test
    fun schemaVersionAboveSupportedIsRejectedWithoutCrashing() {
        val database = freshDatabase()
        val tooNew = fixtureHub().copy(schemaVersion = 999)

        // Must not throw — ContentLoader.insertHub is expected to skip and log, not crash the seed.
        ContentLoader.insert(database, ContentCatalog(hubs = listOf(tooNew), hubStrings = fixtureStrings()))

        val hub = database.hubContentQueries.selectHub("test.hub").executeAsOneOrNull()
        assertNull(hub)
    }
}
