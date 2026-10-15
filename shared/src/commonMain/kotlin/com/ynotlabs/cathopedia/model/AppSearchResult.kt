package com.ynotlabs.cathopedia.model

/** Search destinations are broader than the encyclopedia/bookmark taxonomy. */
sealed interface SearchTarget {
    data class Entity(val type: ContentType, val id: String) : SearchTarget
    data class Prayer(val id: String) : SearchTarget
    data class Hub(val id: String) : SearchTarget
    data class Section(val hubId: String, val id: String) : SearchTarget
    data class Article(val hubId: String, val id: String) : SearchTarget
    data class Guide(val id: String) : SearchTarget

    companion object {
        fun decode(tag: String?, id: String?): SearchTarget? {
            if (id.isNullOrBlank()) return null
            ContentType.entries.find { it.tag == tag }?.let { return Entity(it, id) }
            return when (tag) {
                "prayer" -> Prayer(id)
                "hub" -> Hub(id)
                "section", "article" -> {
                    val parts = id.split('|', limit = 2)
                    if (parts.size != 2 || parts.any { it.isBlank() }) null
                    else if (tag == "section") Section(parts[0], parts[1]) else Article(parts[0], parts[1])
                }
                "guide" -> if (id in setOf("rosary", "stations", "vestments")) Guide(id) else null
                else -> null
            }
        }
    }
}

enum class SearchCategory { PEOPLE, EVENTS, PLACES, FEASTS, DOCUMENTS, TOPICS, PRAYERS }

data class AppSearchResult(
    val target: SearchTarget,
    val name: String,
    val summary: String,
    val artworkAsset: String? = null,
) {
    val key: String get() = target.toString()
    val category: SearchCategory get() = when (val t = target) {
        is SearchTarget.Entity -> SearchCategory.valueOf(ContentCategory.of(t.type).name)
        is SearchTarget.Prayer -> SearchCategory.PRAYERS
        is SearchTarget.Guide -> if (t.id == "vestments") SearchCategory.TOPICS else SearchCategory.PRAYERS
        else -> SearchCategory.TOPICS
    }
}
