package com.ynotlabs.cathopedia.model

/** Shared shape for list rows and search results across all content types. */
data class ContentSummary(
    val type: ContentType,
    val id: String,
    val name: String,
    val summary: String,
    val imageUrl: String? = null,
    val sortYear: Long? = null,

    val papacyStart: String? = null,
    val papacyEnd: String? = null,
    val feastDay: String? = null,
    val rank: String? = null,
)

/** One edge out of EntityRelation, resolved to a browsable target. */
data class RelatedItem(
    val type: ContentType,
    val id: String,
    val relationKind: String,
)
