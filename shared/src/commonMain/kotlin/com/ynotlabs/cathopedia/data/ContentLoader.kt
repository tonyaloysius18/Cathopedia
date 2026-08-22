package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.content.ContentCatalog
import com.ynotlabs.cathopedia.content.LocalizedText
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.PRAYER_SEARCH_ENTITY_TYPE
import com.ynotlabs.cathopedia.resources.Res
import kotlinx.serialization.json.Json

/**
 * Loads the compiled content bundle (`/content` → `:shared:compileContent` →
 * `catalog.json`, see content/README.md) into the local database, re-running
 * whenever CONTENT_VERSION has been bumped since the last load. This is what
 * SeedData.kt used to do with hand-written Kotlin calls — the pipeline exists
 * so growing the catalogue is a content operation, not a code change.
 */
object ContentLoader {
    private const val CATALOG_PATH = "files/content/catalog.json"

    /**
     * Bump this whenever /content gains or changes entries. insertX queries
     * are all INSERT OR REPLACE, so re-running the load is safe/idempotent —
     * this just controls whether it re-runs on an existing install rather
     * than only ever loading once on a database with zero rows.
     */
    private const val CONTENT_VERSION = "23"
    private const val CONTENT_VERSION_KEY = "content_version"

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadIfEmpty(database: CathopediaDatabase) {
        val loadedVersion = database.preferenceQueries.getPreference(CONTENT_VERSION_KEY).executeAsOneOrNull()
        if (loadedVersion == CONTENT_VERSION) return

        val bytes = Res.readBytes(CATALOG_PATH)
        val catalog = json.decodeFromString<ContentCatalog>(bytes.decodeToString())
        insert(database, catalog)
        database.preferenceQueries.setPreference(CONTENT_VERSION_KEY, CONTENT_VERSION)
    }

    private fun insert(database: CathopediaDatabase, catalog: ContentCatalog) {
        catalog.saints.forEach { s ->
            database.saintQueries.insertSaint(s.id, s.feastDay, s.canonizationYear, s.patronage, s.imageUrl, s.sourceUrl)
            s.text.forEach { (lang, t) ->
                database.saintQueries.insertSaintText(s.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.SAINT, s.id, lang, t)
            }
        }
        catalog.popes.forEach { p ->
            database.popeQueries.insertPope(
                p.id, p.regnalNumber, p.papacyStart, p.papacyEnd, sortKey(p.papacyStart), p.imageUrl, p.sourceUrl,
            )
            p.text.forEach { (lang, t) ->
                database.popeQueries.insertPopeText(p.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.POPE, p.id, lang, t)
            }
        }
        catalog.apostles.forEach { a ->
            database.apostleQueries.insertApostle(a.id, a.originalName, a.martyrdom, a.imageUrl, a.sourceUrl)
            a.text.forEach { (lang, t) ->
                database.apostleQueries.insertApostleText(a.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.APOSTLE, a.id, lang, t)
            }
        }
        catalog.churches.forEach { c ->
            database.churchQueries.insertChurch(c.id, c.latitude, c.longitude, c.foundedYear, c.imageUrl, c.sourceUrl)
            c.text.forEach { (lang, t) ->
                database.churchQueries.insertChurchText(c.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.CHURCH, c.id, lang, t)
            }
        }
        catalog.apparitions.forEach { a ->
            database.apparitionQueries.insertApparition(a.id, a.location, a.apparitionYear, a.approvalStatus, a.imageUrl, a.sourceUrl)
            a.text.forEach { (lang, t) ->
                database.apparitionQueries.insertApparitionText(a.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.APPARITION, a.id, lang, t)
            }
        }
        catalog.miracles.forEach { m ->
            database.miracleQueries.insertMiracle(m.id, m.location, m.miracleYear, m.imageUrl, m.sourceUrl)
            m.text.forEach { (lang, t) ->
                database.miracleQueries.insertMiracleText(m.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.MIRACLE, m.id, lang, t)
            }
        }
        catalog.feasts.forEach { f ->
            database.feastQueries.insertFeast(f.id, f.date, f.rank, f.imageUrl, f.sourceUrl)
            f.text.forEach { (lang, t) ->
                database.feastQueries.insertFeastText(f.id, lang, t.name, t.summary, t.body, t.sourceAttribution)
                index(database, ContentType.FEAST, f.id, lang, t)
            }
        }

        catalog.prayers.forEach { p ->
            database.prayerQueries.insertPrayer(p.id, p.category, p.sortOrder, if (p.isSequence) 1L else 0L)
            p.text.forEach { (lang, t) ->
                database.prayerQueries.insertPrayerText(p.id, lang, t.title, t.subtitle, t.bodyMd, t.attribution, t.source)
                database.searchQueries.insertSearchEntry(PRAYER_SEARCH_ENTITY_TYPE, p.id, lang, t.title, t.subtitle ?: "", t.bodyMd)
            }
            database.prayerUserStateQueries.insertPrayerUserStateDefault(p.id)
        }

        catalog.mysteries.forEach { m ->
            database.mysteryQueries.insertMystery(m.id, m.mysterySet, m.sortOrder, m.scriptureRef)
            m.text.forEach { (lang, t) ->
                database.mysteryQueries.insertMysteryText(m.id, lang, t.title, t.fruit, t.meditation)
            }
        }

        catalog.relations.forEach { r ->
            database.relationQueries.insertRelation(r.from.type, r.from.id, r.to.type, r.to.id, r.kind)
        }
    }

    private fun index(database: CathopediaDatabase, type: ContentType, id: String, language: String, text: LocalizedText) {
        database.searchQueries.insertSearchEntry(type.tag, id, language, text.name, text.summary, text.body)
    }

    /**
     * Chronological sort key from date strings like "c. 64", "988-01-01", or "1003-06":
     * year * 12 + (month - 1), so popes sharing a year (e.g. Benedict IX's three
     * non-consecutive reigns, all touching 1045) still land in month order rather
     * than falling back to arbitrary insertion order. Month defaults to January
     * when absent. Divide by 12 to recover the year (e.g. for century grouping).
     */
    private fun sortKey(date: String?): Long? {
        if (date == null) return null
        val cleaned = date.removePrefix("c.").trim()
        val year = cleaned.substringBefore("-").toLongOrNull() ?: return null
        val month = cleaned.substringAfter("-", "").substringBefore("-").toLongOrNull()?.coerceIn(1, 12) ?: 1
        return year * 12 + (month - 1)
    }
}
