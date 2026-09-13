package com.ynotlabs.cathopedia.model

/**
 * The grouping used by Explore, Search's filter chips, and a detail page's
 * Connected section.
 *
 * [browsable] marks the four categories Explore offers as tiles. DOCUMENTS is not
 * one of them — papal documents are reached through the Holy See hub — but it
 * still needs to exist so that a document related to a pope is labelled as a
 * document rather than falling into some other category's heading.
 */
enum class ContentCategory(val types: List<ContentType>, val browsable: Boolean = true) {
    PEOPLE(listOf(ContentType.SAINT, ContentType.POPE, ContentType.APOSTLE)),
    EVENTS(listOf(ContentType.APPARITION, ContentType.MIRACLE)),
    PLACES(listOf(ContentType.CHURCH)),
    FEASTS(listOf(ContentType.FEAST)),
    DOCUMENTS(listOf(ContentType.DOCUMENT), browsable = false),
    ;

    companion object {
        /** Every type belongs to exactly one category; the fallback is defensive only. */
        fun of(type: ContentType): ContentCategory =
            entries.firstOrNull { type in it.types } ?: EVENTS

        /** The categories Explore shows as tiles. */
        val browsable: List<ContentCategory> get() = entries.filter { it.browsable }
    }
}
