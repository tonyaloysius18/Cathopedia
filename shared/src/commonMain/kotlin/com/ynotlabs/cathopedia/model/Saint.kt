package com.ynotlabs.cathopedia.model

data class SaintDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val feastDay: String?,
    val canonizationYear: Long?,
    val patronage: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
