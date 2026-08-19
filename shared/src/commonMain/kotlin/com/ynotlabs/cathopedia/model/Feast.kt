package com.ynotlabs.cathopedia.model

data class FeastDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val date: String?,
    val rank: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
