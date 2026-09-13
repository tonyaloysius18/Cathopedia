package com.ynotlabs.cathopedia.model

/**
 * A papal document — encyclicals first, with the other forms listed on
 * "Kinds of Papal Documents" using the same shape.
 *
 * [body] is Cathopedia's own summary, never the letter itself: the texts belong
 * to Libreria Editrice Vaticana, so [sourceUrl] sends the reader to vatican.va
 * for the full document.
 */
data class DocumentDetail(
    val id: String,
    val name: String,
    val summary: String,
    val body: String,
    /** "encyclical", "apostolic_constitution", "apostolic_exhortation", "motu_proprio". */
    val kind: String,
    /** Slug of the promulgating pope in `content/popes`, when we carry that pope. */
    val popeId: String?,
    /** ISO date the document was promulgated, where it is known precisely. */
    val promulgated: String?,
    val documentYear: Long?,
    val imageUrl: String?,
    val sourceUrl: String?,
    val sourceAttribution: String?,
    val related: List<RelatedItem> = emptyList(),
)

/** The document forms the app knows, matching `DocumentContent.kind`. */
object DocumentKinds {
    const val ENCYCLICAL = "encyclical"
    const val APOSTOLIC_CONSTITUTION = "apostolic_constitution"
    const val APOSTOLIC_EXHORTATION = "apostolic_exhortation"
    const val MOTU_PROPRIO = "motu_proprio"
}

/** One pope's documents of a given kind, for the grouped index. */
data class DocumentPopeGroup(
    val popeId: String?,
    val popeName: String,
    val documents: List<ContentSummary>,
    /** Year of this pope's most recent document — what orders the groups. */
    val latestYear: Long,
)
