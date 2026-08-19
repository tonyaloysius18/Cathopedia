package com.ynotlabs.cathopedia.model

data class ChurchDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    val latitude: Double?,
    val longitude: Double?,
    val foundedYear: Long?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)
