package com.ynotlabs.cathopedia.content.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire models for the topic-hub content format (schemaVersion 1).
 *
 * These mirror schema/hub-content.schema.json one-for-one and are only used by the
 * content seeder. The UI layer consumes domain models read back out of SQLDelight,
 * so the app never depends on the on-disk JSON shape at runtime.
 *
 * Decode with:
 *   val json = Json {
 *       ignoreUnknownKeys = true      // forward compatibility with newer content files
 *       classDiscriminator = "type"
 *       explicitNulls = false
 *   }
 */

const val HUB_SCHEMA_VERSION = 1

@Serializable
data class HubDocument(
    val schemaVersion: Int,
    val contentVersion: Int,
    val hub: Hub,
    val sections: List<Section>,
    val articles: List<Article> = emptyList(),
    val factSheets: List<FactSheet> = emptyList(),
    val diagrams: List<Diagram> = emptyList(),
    val steppers: List<Stepper> = emptyList(),
    val timelines: List<Timeline> = emptyList(),
)

@Serializable
data class Hub(
    val id: String,
    val slug: String,
    val titleKey: String,
    val subtitleKey: String? = null,
    val introKey: String? = null,
    val icon: String? = null,
    val heroAsset: String? = null,
    val accentColor: String? = null,
    val sortOrder: Int,
    val tags: List<String> = emptyList(),
)

@Serializable
data class Section(
    val id: String,
    val sortOrder: Int,
    val status: SectionStatus,
    val layout: SectionLayout,
    val titleKey: String,
    val summaryKey: String? = null,
    val icon: String? = null,
    val heroAsset: String? = null,
    val accentColor: String? = null,
    val articleIds: List<String> = emptyList(),
    val factSheetId: String? = null,
    val diagramIds: List<String> = emptyList(),
    val stepperId: String? = null,
    val timelineId: String? = null,
    val collectionQuery: CollectionQuery? = null,
)

@Serializable
enum class SectionStatus { PUBLISHED, STUB }

@Serializable
enum class SectionLayout { ARTICLES, DIAGRAM, STEPPER, TIMELINE, FACT_SHEET, COLLECTION }

@Serializable
data class CollectionQuery(
    val entityType: EntityType,
    val filter: Map<String, String> = emptyMap(),
    val groupBy: String? = null,
    val sortBy: String? = null,
)

@Serializable
enum class EntityType { ARTICLE, POPE, SAINT, CHURCH, PRAYER, DOCUMENT, COUNCIL, ARTWORK, PLACE }

@Serializable
data class Article(
    val id: String,
    val sectionId: String,
    val sortOrder: Int,
    val titleKey: String,
    val leadKey: String? = null,
    val heroAsset: String? = null,
    val readingTimeMinutes: Int? = null,
    val blocks: List<Block>,
    val related: List<EntityRef> = emptyList(),
    val sources: List<Source> = emptyList(),
)

@Serializable
data class EntityRef(
    val type: EntityType,
    val id: String,
    val labelKey: String? = null,
)

@Serializable
data class Source(
    val label: String,
    val url: String? = null,
    val accessed: String? = null,
)

// ---------------------------------------------------------------- blocks

@Serializable
sealed interface Block

@Serializable
@SerialName("heading")
data class HeadingBlock(val level: Int = 2, val textKey: String) : Block

@Serializable
@SerialName("paragraph")
data class ParagraphBlock(val textKey: String) : Block

@Serializable
@SerialName("list")
data class ListBlock(val style: ListStyle = ListStyle.bullet, val itemKeys: List<String>) : Block

@Serializable
enum class ListStyle { bullet, numbered }

@Serializable
@SerialName("quote")
data class QuoteBlock(val textKey: String, val attributionKey: String? = null) : Block

@Serializable
@SerialName("callout")
data class CalloutBlock(
    val tone: CalloutTone = CalloutTone.info,
    val titleKey: String? = null,
    val textKey: String,
) : Block

@Serializable
enum class CalloutTone { info, note, warning, devotional }

@Serializable
@SerialName("image")
data class ImageBlock(
    val asset: String,
    val captionKey: String? = null,
    val credit: String? = null,
    val license: String,
) : Block

@Serializable
@SerialName("factGrid")
data class FactGridBlock(val facts: List<InlineFact>) : Block

@Serializable
data class InlineFact(val labelKey: String, val valueKey: String, val icon: String? = null)

@Serializable
@SerialName("diagramRef")
data class DiagramRefBlock(val diagramId: String) : Block

@Serializable
@SerialName("stepperRef")
data class StepperRefBlock(val stepperId: String) : Block

@Serializable
@SerialName("timelineRef")
data class TimelineRefBlock(val timelineId: String) : Block

@Serializable
@SerialName("entityCards")
data class EntityCardsBlock(val titleKey: String? = null, val refs: List<EntityRef>) : Block

@Serializable
@SerialName("externalLink")
data class ExternalLinkBlock(val labelKey: String, val url: String, val reason: String? = null) : Block

// ---------------------------------------------------------------- fact sheets

@Serializable
data class FactSheet(
    val id: String,
    val titleKey: String,
    val facts: List<Fact>,
)

@Serializable
data class Fact(
    val id: String,
    val labelKey: String,
    val valueKey: String,
    val icon: String? = null,
    val footnoteKey: String? = null,
    val volatile: Boolean = false,
)

// ---------------------------------------------------------------- diagrams

@Serializable
data class Diagram(
    val id: String,
    val titleKey: String? = null,
    val captionKey: String? = null,
    val asset: String,
    val aspectRatio: Float,
    val minZoom: Float = 1f,
    val maxZoom: Float = 5f,
    val initialFocus: NormPoint? = null,
    val hotspots: List<Hotspot> = emptyList(),
)

/** Coordinates are normalized 0..1 against the artwork box — never pixels. */
@Serializable
data class NormPoint(val x: Float, val y: Float)

@Serializable
data class Hotspot(
    val id: String,
    val labelKey: String,
    val blurbKey: String? = null,
    val order: Int? = null,
    val shape: HotspotShape,
    val target: EntityRef? = null,
)

@Serializable
sealed interface HotspotShape

@Serializable
@SerialName("rect")
data class RectShape(val x: Float, val y: Float, val w: Float, val h: Float) : HotspotShape

@Serializable
@SerialName("circle")
data class CircleShape(val cx: Float, val cy: Float, val r: Float) : HotspotShape

@Serializable
@SerialName("polygon")
data class PolygonShape(val points: List<NormPoint>) : HotspotShape

// ---------------------------------------------------------------- steppers & timelines

@Serializable
data class Stepper(
    val id: String,
    val titleKey: String,
    val introKey: String? = null,
    val steps: List<Step>,
)

@Serializable
data class Step(
    val id: String,
    val order: Int,
    val titleKey: String,
    val bodyKey: String,
    val latinKey: String? = null,
    val icon: String? = null,
    val asset: String? = null,
)

@Serializable
data class Timeline(
    val id: String,
    val titleKey: String,
    val events: List<TimelineEvent>,
)

@Serializable
data class TimelineEvent(
    val id: String,
    val year: Int,
    val endYear: Int? = null,
    val approximate: Boolean = false,
    val titleKey: String,
    val bodyKey: String? = null,
    val ref: EntityRef? = null,
)

// ---------------------------------------------------------------- strings

@Serializable
data class HubStrings(
    val schemaVersion: Int,
    val hubId: String,
    val lang: String,
    val contentVersion: Int? = null,
    val translationStatus: TranslationStatus = TranslationStatus.COMPLETE,
    val strings: Map<String, String>,
)

@Serializable
enum class TranslationStatus { COMPLETE, PARTIAL, MACHINE_DRAFT }
