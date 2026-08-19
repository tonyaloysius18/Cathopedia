package com.ynotlabs.cathopedia.model

data class MiracleDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val location: String?,
    val miracleYear: Long?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
