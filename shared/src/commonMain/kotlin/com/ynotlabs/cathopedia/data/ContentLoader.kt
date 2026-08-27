package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.content.ContentCatalog
import com.ynotlabs.cathopedia.content.LocalizedText
import com.ynotlabs.cathopedia.content.hubContentJson
import com.ynotlabs.cathopedia.content.model.HUB_SCHEMA_VERSION
import com.ynotlabs.cathopedia.content.model.HubDocument
import com.ynotlabs.cathopedia.content.model.HubStrings
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.db.Hub_article
import com.ynotlabs.cathopedia.db.Hub_diagram
import com.ynotlabs.cathopedia.db.Hub_fact_sheet
import com.ynotlabs.cathopedia.db.Hub_hotspot
import com.ynotlabs.cathopedia.db.Hub_section
import com.ynotlabs.cathopedia.db.Hub_stepper
import com.ynotlabs.cathopedia.db.Hub_timeline
import com.ynotlabs.cathopedia.db.Localized_text
import com.ynotlabs.cathopedia.db.Topic_hub
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.PRAYER_SEARCH_ENTITY_TYPE
import com.ynotlabs.cathopedia.resources.Res
import kotlinx.serialization.encodeToString

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
    private const val CONTENT_VERSION = "36"
    private const val CONTENT_VERSION_KEY = "content_version"

    // classDiscriminator/explicitNulls (from com.ynotlabs.cathopedia.content.hubContentJson) are
    // only exercised by the hub content's sealed Block/HotspotShape hierarchy (see
    // HubContentModels.kt) — harmless no-ops for every other (non-polymorphic) content type
    // decoded through this same instance.
    private val json = hubContentJson

    suspend fun loadIfEmpty(database: CathopediaDatabase) {
        val loadedVersion = database.preferenceQueries.getPreference(CONTENT_VERSION_KEY).executeAsOneOrNull()
        if (loadedVersion == CONTENT_VERSION) return

        val bytes = Res.readBytes(CATALOG_PATH)
        val catalog = json.decodeFromString<ContentCatalog>(bytes.decodeToString())
        insert(database, catalog)
        database.preferenceQueries.setPreference(CONTENT_VERSION_KEY, CONTENT_VERSION)
    }

    /** internal (not private) so the hub-seeding test can call it without a Res-backed catalog.json. */
    internal fun insert(database: CathopediaDatabase, catalog: ContentCatalog) {
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
                database.prayerQueries.insertPrayerText(
                    p.id,
                    lang,
                    t.title,
                    t.subtitle,
                    t.bodyMd,
                    t.about,
                    t.attribution,
                    t.source,
                )
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

        catalog.hubs.forEach { hub -> insertHub(database, hub) }

        catalog.hubStrings.forEach { file ->
            file.strings.forEach { (key, value) ->
                database.hubContentQueries.insertString(Localized_text(key, file.lang, value))
            }
        }
    }

    /** One transaction per hub, as required by the topic-hub brief (docs/briefs/topic-hubs.md, T2). */
    private fun insertHub(database: CathopediaDatabase, doc: HubDocument) {
        if (doc.schemaVersion > HUB_SCHEMA_VERSION) {
            println("ContentLoader: skipping hub '${doc.hub.id}' — schemaVersion ${doc.schemaVersion} exceeds supported $HUB_SCHEMA_VERSION")
            return
        }

        database.transaction {
            val hub = doc.hub
            database.hubContentQueries.insertHub(
                Topic_hub(
                    id = hub.id,
                    slug = hub.slug,
                    title_key = hub.titleKey,
                    subtitle_key = hub.subtitleKey,
                    intro_key = hub.introKey,
                    icon = hub.icon,
                    hero_asset = hub.heroAsset,
                    accent_color = hub.accentColor,
                    sort_order = hub.sortOrder.toLong(),
                    content_version = doc.contentVersion.toLong(),
                ),
            )

            doc.sections.forEach { s ->
                database.hubContentQueries.insertSection(
                    Hub_section(
                        id = s.id,
                        hub_id = hub.id,
                        sort_order = s.sortOrder.toLong(),
                        status = s.status.name,
                        layout = s.layout.name,
                        title_key = s.titleKey,
                        summary_key = s.summaryKey,
                        icon = s.icon,
                        hero_asset = s.heroAsset,
                        accent_color = s.accentColor,
                        fact_sheet_id = s.factSheetId,
                        stepper_id = s.stepperId,
                        timeline_id = s.timelineId,
                        collection_query = s.collectionQuery?.let { json.encodeToString(it) },
                        diagram_id = s.diagramIds.firstOrNull(),
                    ),
                )
            }

            doc.articles.forEach { a ->
                database.hubContentQueries.insertArticle(
                    Hub_article(
                        id = a.id,
                        section_id = a.sectionId,
                        sort_order = a.sortOrder.toLong(),
                        title_key = a.titleKey,
                        lead_key = a.leadKey,
                        hero_asset = a.heroAsset,
                        reading_time = a.readingTimeMinutes?.toLong(),
                        blocks_json = json.encodeToString(a.blocks),
                        related_json = json.encodeToString(a.related),
                        sources_json = json.encodeToString(a.sources),
                    ),
                )
            }

            doc.factSheets.forEach { f ->
                database.hubContentQueries.insertFactSheet(
                    Hub_fact_sheet(
                        id = f.id,
                        hub_id = hub.id,
                        title_key = f.titleKey,
                        facts_json = json.encodeToString(f.facts),
                    ),
                )
            }

            doc.diagrams.forEach { d ->
                database.hubContentQueries.insertDiagram(
                    Hub_diagram(
                        id = d.id,
                        hub_id = hub.id,
                        title_key = d.titleKey,
                        caption_key = d.captionKey,
                        asset = d.asset,
                        aspect_ratio = d.aspectRatio.toDouble(),
                        min_zoom = d.minZoom.toDouble(),
                        max_zoom = d.maxZoom.toDouble(),
                        focus_x = d.initialFocus?.x?.toDouble(),
                        focus_y = d.initialFocus?.y?.toDouble(),
                    ),
                )
                d.hotspots.forEach { h ->
                    database.hubContentQueries.insertHotspot(
                        Hub_hotspot(
                            id = h.id,
                            diagram_id = d.id,
                            label_key = h.labelKey,
                            blurb_key = h.blurbKey,
                            tour_order = h.order?.toLong(),
                            shape_json = json.encodeToString(h.shape),
                            target_type = h.target?.type?.name,
                            target_id = h.target?.id,
                        ),
                    )
                }
            }

            doc.steppers.forEach { s ->
                database.hubContentQueries.insertStepper(
                    Hub_stepper(
                        id = s.id,
                        hub_id = hub.id,
                        title_key = s.titleKey,
                        intro_key = s.introKey,
                        steps_json = json.encodeToString(s.steps),
                    ),
                )
            }

            doc.timelines.forEach { t ->
                database.hubContentQueries.insertTimeline(
                    Hub_timeline(
                        id = t.id,
                        hub_id = hub.id,
                        title_key = t.titleKey,
                        events_json = json.encodeToString(t.events),
                    ),
                )
            }
        }
    }

    private fun index(database: CathopediaDatabase, type: ContentType, id: String, language: String, text: LocalizedText) {
        database.searchQueries.deleteSearchEntriesFor(type.tag, id, language)
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