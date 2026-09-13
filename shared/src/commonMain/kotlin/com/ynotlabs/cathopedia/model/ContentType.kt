package com.ynotlabs.cathopedia.model

/**
 * The entity kinds in the content model. [tag] is the exact string stored in
 * EntityRelation.fromType/toType and ContentSearch.entityType, so it must stay
 * in sync with the SQLDelight schema.
 *
 * DOCUMENT covers the papal texts — encyclicals first. Their full text belongs
 * to Libreria Editrice Vaticana, so a document entity carries our own summary
 * and links out to vatican.va rather than reproducing the letter.
 */
enum class ContentType(val tag: String) {
    SAINT("saint"),
    POPE("pope"),
    APOSTLE("apostle"),
    CHURCH("church"),
    APPARITION("apparition"),
    MIRACLE("miracle"),
    FEAST("feast"),
    DOCUMENT("document");

    companion object {
        fun fromTag(tag: String): ContentType = entries.first { it.tag == tag }
    }
}
