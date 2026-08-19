package com.ynotlabs.cathopedia.model

/** The People/Places/Events/Feasts grouping used by Explore and Search's filter chips. */
enum class ContentCategory(val label: String, val types: List<ContentType>) {
    PEOPLE("People", listOf(ContentType.SAINT, ContentType.POPE, ContentType.APOSTLE)),
    PLACES("Places", listOf(ContentType.CHURCH)),
    EVENTS("Events", listOf(ContentType.APPARITION, ContentType.MIRACLE)),
    FEASTS("Feasts", listOf(ContentType.FEAST)),
    ;

    companion object {
        fun of(type: ContentType): ContentCategory = entries.first { type in it.types }
    }
}
