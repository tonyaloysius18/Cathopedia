package com.ynotlabs.cathopedia.model

data class ApostleDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val originalName: String?,
    val martyrdom: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
