package com.ynotlabs.cathopedia.model

/**
 * The seven entity kinds from the Phase 0 content model. [tag] is the exact
 * string stored in EntityRelation.fromType/toType and ContentSearch.entityType,
 * so it must stay in sync with the SQLDelight schema.
 */
enum class ContentType(val tag: String) {
    SAINT("saint"),
    POPE("pope"),
    APOSTLE("apostle"),
    CHURCH("church"),
    APPARITION("apparition"),
    MIRACLE("miracle"),
    FEAST("feast");

    companion object {
        fun fromTag(tag: String): ContentType = entries.first { it.tag == tag }
    }
}
