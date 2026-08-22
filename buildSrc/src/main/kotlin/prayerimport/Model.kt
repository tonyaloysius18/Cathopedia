package prayerimport

import kotlinx.serialization.Serializable

/**
 * One row of /content/sources/wikisource-map.json: where to fetch a single
 * prayer/language pair from, or a flag that it will be pasted in by hand.
 * `page`/`edition` are nullable and start blank — the importer never guesses
 * them (see cathopedia-task-3b-importer.md, 3b.1); a blank `page` is a
 * correct, permanent "not looked up yet" state, not a TODO for this code to
 * fill in.
 */
@Serializable
data class SourceMapEntry(
    val wiki: String? = null,
    val page: String? = null,
    val section: String? = null,
    val edition: String? = null,
    val manual: Boolean = false,
)

/** slug -> language -> where that text comes from. */
typealias SourceMap = Map<String, Map<String, SourceMapEntry>>

/** Cached raw MediaWiki response, committed to git so re-runs need no network. */
@Serializable
data class FetchedPage(
    val slug: String,
    val lang: String,
    val wiki: String,
    val page: String,
    val revid: Long,
    val timestamp: String,
    val wikitext: String,
    val retrievedAt: String,
)

enum class ImportOutcome { IMPORTED, MANUAL, HAND_SOURCED, NO_MAP, FAILED }

data class ImportResult(
    val slug: String,
    val lang: String,
    val outcome: ImportOutcome,
    val reason: String? = null,
)
