package com.ynotlabs.cathopedia.data

import com.ynotlabs.cathopedia.db.CathopediaDatabase
import com.ynotlabs.cathopedia.model.ApostleDetail
import com.ynotlabs.cathopedia.model.ApparitionDetail
import com.ynotlabs.cathopedia.model.BookmarkItem
import com.ynotlabs.cathopedia.model.ChurchDetail
import com.ynotlabs.cathopedia.model.ContentSummary
import com.ynotlabs.cathopedia.model.ContentType
import com.ynotlabs.cathopedia.model.FeastDetail
import com.ynotlabs.cathopedia.model.MiracleDetail
import com.ynotlabs.cathopedia.model.MysteryDetail
import com.ynotlabs.cathopedia.model.MysterySet
import com.ynotlabs.cathopedia.model.MysterySummary
import com.ynotlabs.cathopedia.model.PopeDetail
import com.ynotlabs.cathopedia.model.PRAYER_SEARCH_ENTITY_TYPE
import com.ynotlabs.cathopedia.model.PrayerCategory
import com.ynotlabs.cathopedia.model.PrayerDetail
import com.ynotlabs.cathopedia.model.PrayerSummary
import com.ynotlabs.cathopedia.model.RelatedItem
import com.ynotlabs.cathopedia.model.SaintDetail
import com.ynotlabs.cathopedia.liturgical.LiturgicalCalendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.random.Random

/**
 * Single access point onto the bundled SQLite database. Every list/detail query
 * is language-scoped per the Phase 0 content model; search is FTS5-backed and
 * optionally filtered by content type.
 */
@OptIn(ExperimentalTime::class)
class CathopediaRepository(private val database: CathopediaDatabase) {

    suspend fun listSaints(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.saintQueries.selectAllSaints(language)
            .executeAsList()
            .map { 
                ContentSummary(
                    type = ContentType.SAINT, 
                    id = it.id, 
                    name = it.name, 
                    summary = it.summary, 
                    imageUrl = it.imageUrl,
                    feastDay = it.feastDay
                ) 
            }
    }

    suspend fun saintDetail(id: String, language: String): SaintDetail? = withContext(Dispatchers.Default) {
        database.saintQueries.selectSaintDetail(language = language, id = id).executeAsOneOrNull()?.let {
            SaintDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                feastDay = it.feastDay,
                canonizationYear = it.canonizationYear,
                patronage = it.patronage,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.SAINT, id),
            )
        }
    }

    suspend fun listPopes(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.popeQueries.selectAllPopes(language)
            .executeAsList()
            .map { ContentSummary(ContentType.POPE, it.id, it.name, it.summary, it.imageUrl, it.papacyStartYear, papacyStart = it.papacyStart, papacyEnd = it.papacyEnd) }
    }

    suspend fun popeDetail(id: String, language: String): PopeDetail? = withContext(Dispatchers.Default) {
        database.popeQueries.selectPopeDetail(language = language, id = id).executeAsOneOrNull()?.let {
            PopeDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                regnalNumber = it.regnalNumber,
                papacyStart = it.papacyStart,
                papacyEnd = it.papacyEnd,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.POPE, id),
            )
        }
    }

    suspend fun listApostles(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.apostleQueries.selectAllApostles(language)
            .executeAsList()
            .map { ContentSummary(ContentType.APOSTLE, it.id, it.name, it.summary, it.imageUrl) }
    }

    suspend fun apostleDetail(id: String, language: String): ApostleDetail? = withContext(Dispatchers.Default) {
        database.apostleQueries.selectApostleDetail(language = language, id = id).executeAsOneOrNull()?.let {
            ApostleDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                originalName = it.originalName,
                martyrdom = it.martyrdom,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.APOSTLE, id),
            )
        }
    }

    suspend fun listChurches(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.churchQueries.selectAllChurches(language)
            .executeAsList()
            .map { ContentSummary(ContentType.CHURCH, it.id, it.name, it.summary, it.imageUrl) }
    }

    suspend fun churchDetail(id: String, language: String): ChurchDetail? = withContext(Dispatchers.Default) {
        database.churchQueries.selectChurchDetail(language = language, id = id).executeAsOneOrNull()?.let {
            ChurchDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                latitude = it.latitude,
                longitude = it.longitude,
                foundedYear = it.foundedYear,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.CHURCH, id),
            )
        }
    }

    suspend fun listApparitions(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.apparitionQueries.selectAllApparitions(language)
            .executeAsList()
            .map { ContentSummary(ContentType.APPARITION, it.id, it.name, it.summary, it.imageUrl) }
    }

    suspend fun apparitionDetail(id: String, language: String): ApparitionDetail? = withContext(Dispatchers.Default) {
        database.apparitionQueries.selectApparitionDetail(language = language, id = id).executeAsOneOrNull()?.let {
            ApparitionDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                location = it.location,
                apparitionYear = it.apparitionYear,
                approvalStatus = it.approvalStatus,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.APPARITION, id),
            )
        }
    }

    suspend fun listMiracles(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.miracleQueries.selectAllMiracles(language)
            .executeAsList()
            .map { ContentSummary(ContentType.MIRACLE, it.id, it.name, it.summary, it.imageUrl) }
    }

    suspend fun miracleDetail(id: String, language: String): MiracleDetail? = withContext(Dispatchers.Default) {
        database.miracleQueries.selectMiracleDetail(language = language, id = id).executeAsOneOrNull()?.let {
            MiracleDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                location = it.location,
                miracleYear = it.miracleYear,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.MIRACLE, id),
            )
        }
    }

    suspend fun listFeasts(language: String): List<ContentSummary> = withContext(Dispatchers.Default) {
        database.feastQueries.selectAllFeasts(language)
            .executeAsList()
            .map { ContentSummary(ContentType.FEAST, it.id, it.name, it.summary, it.imageUrl, rank = it.rank) }
    }

    /**
     * The feast (if any) celebrated on [today] — cross-references each feast's raw
     * `date` string against [LiturgicalCalendar.resolveDate] rather than a SQL date
     * comparison, since movable feasts ("Movable" in the content data) have no
     * calendar date until resolved against that year's Easter. Prefers the
     * higher-ranking feast if more than one somehow lands on the same day.
     */
    suspend fun feastOfToday(language: String, today: LocalDate = LiturgicalCalendar.today()): ContentSummary? =
        withContext(Dispatchers.Default) {
            val todaysFeastIds = database.feastQueries.selectFeastDates()
                .executeAsList()
                .filter { LiturgicalCalendar.resolveDate(it.date, it.id, today.year) == today }
                .map { it.id }
                .toSet()
            if (todaysFeastIds.isEmpty()) return@withContext null

            listFeasts(language)
                .filter { it.id in todaysFeastIds }
                .minByOrNull { if (it.rank == "Solemnity") 0 else 1 }
        }

    /**
     * Every feast from [from] onward, resolved to its actual calendar date and
     * paired with its localized name/summary, ready to hand to a
     * [com.ynotlabs.cathopedia.notifications.FeastNotificationScheduler]. Looks up
     * to three years ahead so [limit] is always filled even right after a content
     * update adds new feasts, then returns the earliest [limit] by date.
     */
    suspend fun upcomingFeasts(
        language: String,
        from: LocalDate = LiturgicalCalendar.today(),
        limit: Int = 60,
    ): List<Pair<LocalDate, ContentSummary>> = withContext(Dispatchers.Default) {
        val rawDates = database.feastQueries.selectFeastDates().executeAsList()
        val textById = listFeasts(language).associateBy { it.id }

        (from.year until from.year + 3)
            .asSequence()
            .flatMap { year ->
                rawDates.asSequence().mapNotNull { row ->
                    val resolved = LiturgicalCalendar.resolveDate(row.date, row.id, year) ?: return@mapNotNull null
                    if (resolved < from) return@mapNotNull null
                    val summary = textById[row.id] ?: return@mapNotNull null
                    resolved to summary
                }
            }
            .sortedBy { it.first }
            .take(limit)
            .toList()
    }

    suspend fun feastDetail(id: String, language: String): FeastDetail? = withContext(Dispatchers.Default) {
        database.feastQueries.selectFeastDetail(language = language, id = id).executeAsOneOrNull()?.let {
            FeastDetail(
                id = it.id,
                name = it.name,
                summary = it.summary,
                body = it.body,
                date = it.date,
                rank = it.rank,
                imageUrl = it.imageUrl,
                sourceUrl = it.sourceUrl,
                sourceAttribution = it.sourceAttribution,
                related = relatedItems(ContentType.FEAST, id),
            )
        }
    }

    /** Cross-links resolved from EntityRelation, merged from both directions. */
    private fun relatedItems(type: ContentType, id: String): List<RelatedItem> {
        val outgoing = database.relationQueries.selectRelationsFrom(type.tag, id)
            .executeAsList()
            .map { RelatedItem(ContentType.fromTag(it.toType), it.toId, it.relationKind) }
        val incoming = database.relationQueries.selectRelationsTo(type.tag, id)
            .executeAsList()
            .map { RelatedItem(ContentType.fromTag(it.fromType), it.fromId, it.relationKind) }
        return outgoing + incoming
    }

    fun addRelation(from: ContentType, fromId: String, to: ContentType, toId: String, kind: String) {
        database.relationQueries.insertRelation(from.tag, fromId, to.tag, toId, kind)
    }

    /** Re-indexes one entity's language-tagged text into the FTS5 table. Call after every text insert. */
    fun reindexSearch(type: ContentType, id: String, language: String, name: String, summary: String, body: String) {
        database.searchQueries.deleteSearchEntriesFor(type.tag, id, language)
        database.searchQueries.insertSearchEntry(type.tag, id, language, name, summary, body)
    }

    suspend fun search(
        query: String,
        language: String,
        type: ContentType? = null,
        limit: Long = 50,
    ): List<ContentSummary> = withContext(Dispatchers.Default) {
        // FTS5 prefix match so "franc*" finds "Francis" as the user is still typing.
        val matchQuery = query.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
            .joinToString(" ") { "$it*" }
        if (matchQuery.isBlank()) return@withContext emptyList()

        database.searchQueries.search(
            matchQuery = matchQuery,
            language = language,
            entityTypeFilter = type?.tag,
            resultLimit = limit,
        )
            .executeAsList()
            .mapNotNull {
                // FTS5 virtual tables can't express NOT NULL, so SQLDelight infers these
                // columns as nullable even though every row is written by insertSearchEntry
                // with real values — safe to assert non-null here.
                val entityType = it.entityType ?: return@mapNotNull null
                
                // ContentSearch index shared by encyclopedia (7 types) and prayers.
                // Prayers are handled by searchPrayers() from the Prayers tab, but
                // global search needs to filter them out here because they don't
                // map to the ContentType enum.
                if (entityType == PRAYER_SEARCH_ENTITY_TYPE) return@mapNotNull null

                ContentSummary(
                    type = ContentType.fromTag(entityType),
                    id = it.entityId!!,
                    name = it.name!!,
                    summary = it.summary!!
                )
            }
    }

    /** Dispatches to the right per-type list query — used wherever the UI works across all 7 types generically. */
    suspend fun listByType(type: ContentType, language: String): List<ContentSummary> = when (type) {
        ContentType.SAINT -> listSaints(language)
        ContentType.POPE -> listPopes(language)
        ContentType.APOSTLE -> listApostles(language)
        ContentType.CHURCH -> listChurches(language)
        ContentType.APPARITION -> listApparitions(language)
        ContentType.MIRACLE -> listMiracles(language)
        ContentType.FEAST -> listFeasts(language)
    }

    /** Resolves a related item's display name — used to label Connected chips with a real title, not a raw id. */
    suspend fun summaryOf(type: ContentType, id: String, language: String): ContentSummary? =
        listByType(type, language).firstOrNull { it.id == id }

    // ---- Search History ----

    suspend fun addSearchHistory(query: String) = withContext(Dispatchers.Default) {
        val cleaned = query.trim()
        if (cleaned.isNotEmpty()) {
            database.searchHistoryQueries.insertSearch(cleaned, Clock.System.now().toEpochMilliseconds())
        }
    }

    suspend fun getSearchHistory(): List<String> = withContext(Dispatchers.Default) {
        database.searchHistoryQueries.selectAllHistory().executeAsList()
    }

    suspend fun removeSearchHistory(query: String) = withContext(Dispatchers.Default) {
        database.searchHistoryQueries.deleteHistory(query)
    }

    suspend fun clearSearchHistory() = withContext(Dispatchers.Default) {
        database.searchHistoryQueries.clearAllHistory()
    }

    // ---- Bookmarks (Saved tab) ----

    suspend fun isBookmarked(type: ContentType, id: String): Boolean = withContext(Dispatchers.Default) {
        database.bookmarkQueries.selectBookmark(type.tag, id).executeAsOneOrNull() != null
    }

    /** Toggles the bookmark and returns the new state. */
    suspend fun toggleBookmark(type: ContentType, id: String, name: String, summary: String): Boolean =
        withContext(Dispatchers.Default) {
            val existing = database.bookmarkQueries.selectBookmark(type.tag, id).executeAsOneOrNull()
            if (existing != null) {
                database.bookmarkQueries.deleteBookmark(type.tag, id)
                false
            } else {
                database.bookmarkQueries.insertBookmark(
                    type.tag, id, name, summary, null, Clock.System.now().toEpochMilliseconds(),
                )
                true
            }
        }

    suspend fun listBookmarks(): List<BookmarkItem> = withContext(Dispatchers.Default) {
        database.bookmarkQueries.selectAllBookmarks().executeAsList().map {
            BookmarkItem(ContentType.fromTag(it.entityType), it.entityId, it.name, it.summary, it.note, it.createdAt)
        }
    }

    // ---- Recently viewed / Discover (Home) ----

    fun recordView(type: ContentType, id: String, name: String, summary: String) {
        database.recentlyViewedQueries.recordView(
            type.tag, id, name, summary, Clock.System.now().toEpochMilliseconds(),
        )
    }

    suspend fun mostRecentlyViewed(): ContentSummary? = withContext(Dispatchers.Default) {
        database.recentlyViewedQueries.selectMostRecent().executeAsOneOrNull()?.let {
            ContentSummary(ContentType.fromTag(it.entityType), it.entityId, it.name, it.summary)
        }
    }

    /** One entity, picked at random across every type, for Home's Discover card. */
    suspend fun discoverPick(language: String): ContentSummary? = withContext(Dispatchers.Default) {
        val types = ContentType.entries.shuffled()
        for (type in types) {
            val items = listByType(type, language)
            if (items.isNotEmpty()) return@withContext items[Random.nextInt(items.size)]
        }
        null
    }

    // ---- Prayers ----

    suspend fun listPrayers(category: PrayerCategory, language: String): List<PrayerSummary> =
        withContext(Dispatchers.Default) {
            database.prayerQueries.selectPrayersByCategory(language, category.tag).executeAsList().map {
                PrayerSummary(
                    it.id,
                    it.title ?: it.id.hyphenatedToTitle(),
                    it.subtitle,
                    PrayerCategory.fromTag(it.category),
                    it.isSequence == 1L
                )
            }
        }

    suspend fun prayerDetail(id: String, language: String): PrayerDetail? = withContext(Dispatchers.Default) {
        database.prayerQueries.selectPrayerDetail(language = language, id = id).executeAsOneOrNull()?.let {
            PrayerDetail(
                id = it.id,
                category = PrayerCategory.fromTag(it.category),
                isSequence = it.isSequence == 1L,
                title = it.title ?: it.id.hyphenatedToTitle(),
                subtitle = it.subtitle,
                bodyMd = it.bodyMd ?: "",
                attribution = it.attribution,
                source = it.source ?: "",
                availableLanguages = database.prayerQueries.selectPrayerLanguages(id).executeAsList(),
            )
        }
    }

    /** FTS5 search scoped to prayers only — see the PRAYER_SEARCH_ENTITY_TYPE note in ContentLoader. */
    suspend fun searchPrayers(query: String, language: String, limit: Long = 50): List<PrayerSummary> =
        withContext(Dispatchers.Default) {
            val matchQuery = query.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                .joinToString(" ") { "$it*" }
            if (matchQuery.isBlank()) return@withContext emptyList()

            database.searchQueries.search(
                matchQuery = matchQuery,
                language = language,
                entityTypeFilter = PRAYER_SEARCH_ENTITY_TYPE,
                resultLimit = limit,
            ).executeAsList().mapNotNull { row ->
                // Search rows carry only title/subtitle, not the full body — resolve
                // against PrayerEntity for category/isSequence so results can be
                // grouped/routed the same as a regular list row.
                val id = row.entityId ?: return@mapNotNull null
                database.prayerQueries.selectPrayerDetail(language = language, id = id).executeAsOneOrNull()?.let {
                    PrayerSummary(
                        it.id,
                        it.title ?: it.id.hyphenatedToTitle(),
                        it.subtitle,
                        PrayerCategory.fromTag(it.category),
                        it.isSequence == 1L
                    )
                }
            }
        }

    /**
     * Favourite prayer summaries in reading language, pinned atop PrayersHome.
     * Reuses [prayerDetail] per id rather than a new joined query — favourites
     * are a short list, and this naturally excludes a favourite whose text
     * isn't sourced in [language] yet, same as every other prayer list query.
     */
    suspend fun listFavoritePrayers(language: String): List<PrayerSummary> = withContext(Dispatchers.Default) {
        database.prayerUserStateQueries.selectFavoritePrayerIds().executeAsList().mapNotNull { id ->
            database.prayerQueries.selectPrayerDetail(language = language, id = id).executeAsOneOrNull()?.let {
                PrayerSummary(
                    it.id,
                    it.title ?: it.id.hyphenatedToTitle(),
                    it.subtitle,
                    PrayerCategory.fromTag(it.category),
                    it.isSequence == 1L
                )
            }
        }
    }

    suspend fun isPrayerFavorite(id: String): Boolean = withContext(Dispatchers.Default) {
        database.prayerUserStateQueries.selectPrayerUserState(id).executeAsOneOrNull()?.isFavorite == 1L
    }

    suspend fun listFavoritePrayerIds(): List<String> = withContext(Dispatchers.Default) {
        database.prayerUserStateQueries.selectFavoritePrayerIds().executeAsList()
    }

    /** Toggles the favourite and returns the new state. */
    suspend fun togglePrayerFavorite(id: String): Boolean = withContext(Dispatchers.Default) {
        val newState = !(database.prayerUserStateQueries.selectPrayerUserState(id).executeAsOneOrNull()?.isFavorite == 1L)
        database.prayerUserStateQueries.setPrayerFavorite(if (newState) 1L else 0L, id)
        newState
    }

    /** Bumps timesPrayed and lastOpenedAt — call when a prayer (or the Rosary) is actually recited, not merely viewed. */
    suspend fun recordPrayerRecited(id: String) = withContext(Dispatchers.Default) {
        database.prayerUserStateQueries.recordPrayerOpened(Clock.System.now().toEpochMilliseconds(), id)
    }

    // ---- Rosary mysteries ----

    suspend fun listMysteries(set: MysterySet, language: String): List<MysterySummary> =
        withContext(Dispatchers.Default) {
            database.mysteryQueries.selectMysteriesBySet(language, set.tag).executeAsList().map {
                MysterySummary(it.id, MysterySet.fromTag(it.mysterySet), it.sortOrder, it.title, it.scriptureRef, it.fruit)
            }
        }

    suspend fun mysteryDetail(id: String, language: String): MysteryDetail? = withContext(Dispatchers.Default) {
        database.mysteryQueries.selectMysteryDetail(language = language, id = id).executeAsOneOrNull()?.let {
            MysteryDetail(
                id = it.id,
                set = MysterySet.fromTag(it.mysterySet),
                sortOrder = it.sortOrder,
                scriptureRef = it.scriptureRef,
                title = it.title,
                fruit = it.fruit,
                meditation = it.meditation,
            )
        }
    }

    // ---- Preferences ----

    fun setPreference(key: String, value: String) {
        database.preferenceQueries.setPreference(key, value)
    }

    fun getPreference(key: String): String? =
        database.preferenceQueries.getPreference(key).executeAsOneOrNull()

    // ---- Content pipeline ----

    /** Loads the compiled /content bundle on first launch. See ContentLoader and content/README.md. */
    suspend fun ensureContentLoaded() = withContext(Dispatchers.Default) {
        ContentLoader.loadIfEmpty(database)
    }

    private fun String.hyphenatedToTitle(): String =
        this.split("-").joinToString(" ") { it.replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase() else char.toString() } }
}
