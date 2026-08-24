package com.ynotlabs.cathopedia.model

/** The People/Places/Events/Feasts grouping used by Explore and Search's filter chips. */
enum class ContentCategory(val types: List<ContentType>) {
    PEOPLE(listOf(ContentType.SAINT, ContentType.POPE, ContentType.APOSTLE)),
    EVENTS(listOf(ContentType.APPARITION, ContentType.MIRACLE)),
    PLACES(listOf(ContentType.CHURCH)),
    FEASTS(listOf(ContentType.FEAST)),
    ;

    companion object {
        fun of(type: ContentType): ContentCategory = entries.first { type in it.types }
    }
}
