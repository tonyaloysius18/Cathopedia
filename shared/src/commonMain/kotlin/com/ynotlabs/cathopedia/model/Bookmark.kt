package com.ynotlabs.cathopedia.model

data class BookmarkItem(
    val type: ContentType,
    val id: String,
    val name: String,
    val summary: String,
    val note: String?,
    val createdAt: Long,
)
