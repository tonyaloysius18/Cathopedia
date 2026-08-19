package com.ynotlabs.cathopedia.model

data class PopeDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val regnalNumber: Long?,
    val papacyStart: String?,
    val papacyEnd: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
