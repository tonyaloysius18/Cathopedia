package com.ynotlabs.cathopedia.model

data class ApparitionDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val location: String?,
    val apparitionYear: Long?,
    val approvalStatus: String?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
