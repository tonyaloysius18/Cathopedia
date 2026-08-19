package com.ynotlabs.cathopedia.content

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
    val relations: List<RelationContent> = emptyList(),
)
