package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.content.ContentCatalog
import com.ynotlabs.cathopedia.content.hubContentJson
import com.ynotlabs.cathopedia.content.model.*
import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.i18n.stringsFor
import com.ynotlabs.cathopedia.liturgical.OrderOfMassText
import com.ynotlabs.cathopedia.ui.screens.vestments.vestmentsSearchText
import kotlinx.serialization.json.*

/** Builds the derived index from the same text and references that the screens render. */
internal object GlobalSearchIndex {
    private val articlePrefixes = mapOf(
        "art.thurible.overview" to "screen.mass.thurible.",
        "art.monstrance.overview" to "screen.mass.monstrance.",
        "art.altar.overview" to "screen.mass.altar.",
        "art.vessels.overview" to "screen.mass.vessels.",
        "art.postures.overview" to "screen.mass.postures.",
        "art.cat.great_commandments" to "screen.catechism.commandments.",
        "art.cat.gifts_prayers" to "screen.catechism.gifts.",
    )

    fun insert(database: CathopediaDatabase, catalog: ContentCatalog, massTexts: Map<String, OrderOfMassText> = emptyMap()) {
        val byLanguage = catalog.hubStrings.groupBy { it.lang }
            .mapValues { (_, files) -> files.flatMap { it.strings.entries }.associate { it.toPair() } }
        val english = byLanguage["en"].orEmpty()
        (byLanguage.keys + massTexts.keys + "en").forEach { language ->
            val strings = english + byLanguage[language].orEmpty()
            fun text(key: String?) = key?.let { strings[it] }.orEmpty()
            fun prefix(prefix: String) = strings.filterKeys { it.startsWith(prefix) }.values.joinToString("\n")
            fun add(tag: String, id: String, name: String, summary: String, body: String) {
                if (name.isNotBlank()) database.searchQueries.insertSearchEntry(tag, id, language, name, summary, body)
            }

            catalog.hubs.filter { it.schemaVersion <= HUB_SCHEMA_VERSION }.forEach { doc ->
                val hubId = doc.hub.id
                val references = buildMap<String, JsonElement> {
                    doc.diagrams.forEach { put(it.id, hubContentJson.encodeToJsonElement(it)) }
                    doc.factSheets.forEach { put(it.id, hubContentJson.encodeToJsonElement(it)) }
                    doc.steppers.forEach { put(it.id, hubContentJson.encodeToJsonElement(it)) }
                    doc.timelines.forEach { put(it.id, hubContentJson.encodeToJsonElement(it)) }
                }
                // Collect localized text keys, following diagram/fact/step/timeline references.
                // IDs, image paths and other structural JSON are deliberately not searchable.
                fun renderedText(element: JsonElement): String {
                    val values = linkedSetOf<String>()
                    val visited = mutableSetOf<String>()
                    fun visit(value: JsonElement) {
                        when (value) {
                            is JsonObject -> value.values.forEach { visit(it) }
                            is JsonArray -> value.forEach { visit(it) }
                            is JsonPrimitive -> if (value.isString) {
                                val key = value.content
                                strings[key]?.let(values::add)
                                if (visited.add(key)) references[key]?.let { visit(it) }
                            }
                        }
                    }
                    visit(element)
                    return values.joinToString("\n")
                }
                add("hub", hubId, text(doc.hub.titleKey), text(doc.hub.subtitleKey),
                    listOf(text(doc.hub.introKey), doc.hub.tags.joinToString(" ")).joinToString("\n"))

                val sections = doc.sections.filter { it.status == SectionStatus.PUBLISHED }.associateBy { it.id }
                sections.values.forEach sectionLoop@{ section ->
                    val articles = doc.articles.filter { it.sectionId == section.id }
                    // A one-article section opens that article directly in search: no duplicate cards.
                    if (section.layout == SectionLayout.ARTICLES && articles.size == 1 &&
                        section.diagramIds.isEmpty() && section.factSheetId == null &&
                        section.stepperId == null && section.timelineId == null) return@sectionLoop
                    val extra = when (section.id) {
                        "cat.sacraments" -> prefix("screen.sacraments.")
                        "mass.procession" -> prefix("screen.mass.procession.")
                        else -> ""
                    }
                    add("section", "$hubId|${section.id}", text(section.titleKey), text(section.summaryKey),
                        renderedText(hubContentJson.encodeToJsonElement(section)) + "\n" + extra)
                }
                doc.articles.forEach { article ->
                    val section = sections[article.sectionId] ?: return@forEach
                    add("article", "$hubId|${article.id}", text(article.titleKey),
                        text(article.leadKey).ifBlank { text(section.summaryKey) },
                        listOf(renderedText(hubContentJson.encodeToJsonElement(article)), text(section.titleKey),
                            text(section.summaryKey), articlePrefixes[article.id]?.let(::prefix).orEmpty()).joinToString("\n"))
                }
            }

            val s = stringsFor(language)
            val mysteries = catalog.mysteries.joinToString("\n") { mystery ->
                val t = mystery.text[language] ?: mystery.text["en"]
                listOfNotNull(t?.title, t?.fruit, t?.meditation, mystery.scriptureRef).joinToString("\n")
            }
            add("guide", "rosary", text("rosary.title").ifBlank { s.rosaryScreenTitle },
                text("rosary.landing.subtitle"), prefix("rosary.") + "\n" + mysteries)
            add("guide", "stations", s.stationsScreenTitle, s.prayersWayOfCrossDescription,
                "Stations of the Cross\n" + prefix("screen.stations."))
            add("guide", "vestments", s.vestmentsTitle, "", vestmentsSearchText(s))
            (massTexts[language] ?: massTexts["en"])?.let { mass ->
                val body = mass.sections.joinToString("\n") { section ->
                    section.title + "\n" + section.parts.joinToString("\n") { part ->
                        listOfNotNull(part.title, part.who, part.rubric, part.rubricLent,
                            part.text, part.textNote, part.missal).joinToString("\n")
                    }
                }
                add("guide", "mass", text("screen.mass.title").ifBlank { "Order of Mass" },
                    text("screen.mass.parts"), body)
            }
        }
    }
}
