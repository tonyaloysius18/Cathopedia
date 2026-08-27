package com.ynotlabs.cathopedia.content

import com.ynotlabs.cathopedia.content.model.HubDocument
import com.ynotlabs.cathopedia.content.model.HubStrings
import kotlinx.serialization.Serializable

/** Mirrors the field names used in every /content/&lt;type&gt;/&lt;id&gt;.json file. */
@Serializable
data class LocalizedText(
    val name: String,
    val summary: String,
    val body: String,
    val sourceAttribution: String? = null,
)

@Serializable
data class SaintContent(
    val id: String,
    val feastDay: String? = null,
    val canonizationYear: Long? = null,
    val patronage: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class PopeContent(
    val id: String,
    val regnalNumber: Long? = null,
    val papacyStart: String? = null,
    val papacyEnd: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class ApostleContent(
    val id: String,
    val originalName: String? = null,
    val martyrdom: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class ChurchContent(
    val id: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val foundedYear: Long? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class ApparitionContent(
    val id: String,
    val location: String? = null,
    val apparitionYear: Long? = null,
    val approvalStatus: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class MiracleContent(
    val id: String,
    val location: String? = null,
    val miracleYear: Long? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class FeastContent(
    val id: String,
    val date: String? = null,
    val rank: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null,
    val text: Map<String, LocalizedText>,
)

@Serializable
data class RelationRef(val type: String, val id: String)

@Serializable
data class RelationContent(val from: RelationRef, val to: RelationRef, val kind: String)

/**
 * Prayer text has a different shape than [LocalizedText] (title/bodyMd instead
 * of name/body, a mandatory [source] rather than an optional sourceAttribution),
 * so it gets its own class rather than overloading that one.
 */
@Serializable
data class PrayerLocalizedText(
    val title: String,
    val subtitle: String? = null,
    val bodyMd: String,
    /**
     * Short contextual description shown in the prayer detail screen.
     *
     * Defaults to an empty string so older prayer JSON files continue to
     * deserialize while their descriptions are added gradually.
     */
    val about: String = "",
    val attribution: String? = null,
    val source: String,
)

@Serializable
data class PrayerContent(
    val id: String,
    /** One of PrayerCategory's tags — see model/Prayer.kt. */
    val category: String,
    val sortOrder: Long,
    val isSequence: Boolean = false,
    val text: Map<String, PrayerLocalizedText>,
)

/**
 * Mirrors MysteryText (Mystery.sq): title/fruit are the factual metadata from
 * the Rosary brief's Appendix A table; [meditation] is left null everywhere
 * for now — someone else is writing those separately (see content/README.md).
 */
@Serializable
data class MysteryLocalizedText(
    val title: String,
    val fruit: String,
    val meditation: String? = null,
)

@Serializable
data class MysteryContent(
    val id: String,
    /** One of MysterySet's tags — see model/Mystery.kt. */
    val mysterySet: String,
    val sortOrder: Long,
    /** Language-neutral Bible citation (e.g. "Lk 1:26-38") — lives here, not in [text]. */
    val scriptureRef: String?,
    val text: Map<String, MysteryLocalizedText> = emptyMap(),
)

/** Deserialised shape of the compiled `catalog.json` bundle. */
@Serializable
data class ContentCatalog(
    val saints: List<SaintContent> = emptyList(),
    val popes: List<PopeContent> = emptyList(),
    val apostles: List<ApostleContent> = emptyList(),
    val churches: List<ChurchContent> = emptyList(),
    val apparitions: List<ApparitionContent> = emptyList(),
    val miracles: List<MiracleContent> = emptyList(),
    val feasts: List<FeastContent> = emptyList(),
    val prayers: List<PrayerContent> = emptyList(),
    val mysteries: List<MysteryContent> = emptyList(),
    val relations: List<RelationContent> = emptyList(),
    val hubs: List<HubDocument> = emptyList(),
    val hubStrings: List<HubStrings> = emptyList(),
)
