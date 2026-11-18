package com.ynotlabs.cathopedia.ui.screens.bible

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.bible.Bible
import com.ynotlabs.cathopedia.bible.BibleBook
import com.ynotlabs.cathopedia.bible.BibleIndex
import com.ynotlabs.cathopedia.bible.BibleRef
import com.ynotlabs.cathopedia.bible.BibleSearchHit
import com.ynotlabs.cathopedia.bible.BibleStore
import com.ynotlabs.cathopedia.bible.BibleVerse
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.explore_bible
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.i18n.ScreenText
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton
import com.ynotlabs.cathopedia.ui.screens.common.ArticleHeader
import com.ynotlabs.cathopedia.ui.screens.common.ArticleScaffold
import com.ynotlabs.cathopedia.ui.theme.CardBorder
import com.ynotlabs.cathopedia.ui.theme.DarkGold
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val GROUPS = mapOf(
    "ot" to listOf("pentateuch", "historical", "wisdom", "prophets"),
    "nt" to listOf("gospels", "acts", "pauline", "catholic_letters", "revelation"),
)
private val FONT_STEPS = listOf(0.85f, 1f, 1.15f, 1.3f, 1.5f)
private const val SEARCH_LIMIT = 300

// The reading page follows the app theme: deep green in dark mode, parchment in light mode.
private val Parchment: Color @Composable get() = MaterialTheme.colorScheme.background
private val ParchmentInk: Color @Composable get() = MaterialTheme.colorScheme.onBackground
private val ParchmentMuted: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val ParchmentGold: Color @Composable get() = MaterialTheme.colorScheme.primary
private val ParchmentRed: Color @Composable get() = MaterialTheme.colorScheme.primary
// Fixed "binding" colours: the Testament covers, the continue card and read chapters.
private val OtCover = Color(0xFF3A2A14)
private val NtCover = Color(0xFF4A1C1C)
private val BindingGreen = Color(0xFF1F4034)
private val CoverText = Color(0xFFEDE6D3)
private val CoverMuted = Color(0xFFB9C2BC)

private fun ScreenText.fmt(key: String, vararg pairs: Pair<String, Any>): String =
    pairs.fold(get(key)) { acc, (k, v) -> acc.replace("{$k}", v.toString()) }

private fun ScreenText.chapters(n: Int): String =
    if (n == 1) get("screen.bible.chapter_one") else fmt("screen.bible.chapters", "n" to n)

@Composable
private fun rememberBibleIndex(): BibleIndex? {
    var index by remember { mutableStateOf<BibleIndex?>(null) }
    LaunchedEffect(Unit) { index = Bible.index() }
    return index
}

// ---------------------------------------------------------------- Home: the library

@Composable
fun BibleHomeScreen(
    repository: CathopediaRepository,
    /** Null when this is the root of the Bible tab, so no back button shows. */
    onBack: (() -> Unit)?,
    onOpenChapter: (BibleRef) -> Unit,
    onOpenBookmarks: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val store = remember(repository) { BibleStore(repository) }
    val index = rememberBibleIndex()
    var testament by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    var hits by remember { mutableStateOf<List<BibleSearchHit>?>(null) }
    var lastRead by remember { mutableStateOf<BibleRef?>(null) }
    var resumeLine by remember { mutableStateOf("") }
    var bookmarkCount by remember { mutableIntStateOf(0) }
    var read by remember { mutableStateOf<Map<String, Set<Int>>>(emptyMap()) }
    var sheetBook by rememberSaveable { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // Where each group's heading sits in the list, filled in as the list is built.
    val groupStarts = remember { mutableMapOf<String, Int>() }

    LaunchedEffect(Unit) {
        val last = store.lastRead()
        lastRead = last
        bookmarkCount = store.bookmarks().size
        read = store.allRead()
        // Open on the testament the reader was last in, unless they have already chosen one here.
        if (testament == null) testament = last?.let { Bible.book(it.book)?.testament } ?: "ot"
        resumeLine = last?.let { Bible.chapter(it.book, it.chapter).firstOrNull()?.text }.orEmpty()
    }
    LaunchedEffect(query) {
        hits = null
        if (query.trim().length < 2) return@LaunchedEffect
        delay(350)
        hits = Bible.search(query, SEARCH_LIMIT)
    }
    val currentGroup by remember {
        derivedStateOf {
            val top = listState.firstVisibleItemIndex + 1
            groupStarts.entries.filter { it.value <= top }.maxByOrNull { it.value }?.key
        }
    }

    ArticleScaffold(
        title = t["screen.bible.title"],
        subtitle = t["screen.bible.subtitle"],
        backDescription = s.back,
        onBack = onBack,
        listState = listState,
        horizontalPadding = 16.dp,
        headerArt = Res.drawable.explore_bible,
    ) {
        var position = 0
        fun row(key: String, content: @Composable () -> Unit) {
            item(key = key) { content() }
            position++
        }

        row("search") {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                singleLine = true,
                placeholder = { Text(t["screen.bible.search_hint"], fontSize = 14.sp, color = colors.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = colors.primary) },
                trailingIcon = if (query.isNotEmpty()) {
                    { IconButton(onClick = { query = "" }) { Icon(Icons.Default.Close, s.back, tint = colors.onSurfaceVariant) } }
                } else null,
                shape = RoundedCornerShape(26.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.surfaceContainer,
                    unfocusedContainerColor = colors.surfaceContainer,
                    focusedBorderColor = colors.primary.copy(alpha = 0.55f),
                    unfocusedBorderColor = colors.outline,
                    focusedTextColor = colors.onBackground,
                    unfocusedTextColor = colors.onBackground,
                    cursorColor = colors.primary,
                ),
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp, bottom = 12.dp),
            )
        }

        if (query.trim().length >= 2) {
            val found = hits
            when {
                found == null -> row("searching") {
                    Text(s.loading, color = colors.onSurfaceVariant, modifier = Modifier.padding(vertical = 12.dp))
                }
                found.isEmpty() -> row("none") {
                    Text(t["screen.bible.no_results"], color = colors.onSurfaceVariant, modifier = Modifier.padding(vertical = 12.dp))
                }
                else -> {
                    row("count") {
                        Text(
                            if (found.size >= SEARCH_LIMIT) t.fmt("screen.bible.results_more", "n" to found.size)
                            else t.fmt("screen.bible.results", "n" to found.size),
                            color = colors.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(bottom = 8.dp),
                        )
                    }
                    found.forEach { hit ->
                        row("hit-${hit.book.code}-${hit.chapter}-${hit.verse.number}") {
                            SearchHitRow(hit, query) { onOpenChapter(BibleRef(hit.book.code, hit.chapter, hit.verse.number)) }
                        }
                    }
                }
            }
            return@ArticleScaffold
        }

        val books = index?.books.orEmpty()
        val resume = lastRead?.let { ref -> books.find { it.code == ref.book }?.let { it to ref } }
        resume?.let { (book, ref) ->
            row("continue") {
                ContinueCard(book, ref.chapter, resumeLine) { onOpenChapter(BibleRef(ref.book, ref.chapter)) }
            }
        }
        if (bookmarkCount > 0) {
            row("bookmarks") { BookmarksRow(bookmarkCount, onOpenBookmarks) }
        }

        val selected = testament ?: "ot"
        row("covers") {
            Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("ot" to OtCover, "nt" to NtCover).forEach { (id, cover) ->
                    TestamentCover(
                        title = t[if (id == "ot") "screen.bible.old_testament" else "screen.bible.new_testament"],
                        count = t.fmt("screen.bible.books_count", "n" to books.count { it.testament == id }),
                        cover = cover,
                        selected = selected == id,
                        modifier = Modifier.weight(1f),
                    ) { testament = id }
                }
            }
        }

        val groups = GROUPS.getValue(selected).filter { g -> books.any { it.group == g } }
        row("chips-$selected") {
            LazyRow(
                Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(groups, key = { it }) { group ->
                    JumpChip(t["screen.bible.group.$group"], active = group == currentGroup) {
                        groupStarts[group]?.let { scope.launch { listState.animateScrollToItem(it) } }
                    }
                }
            }
        }

        groupStarts.clear()
        groups.forEach { group ->
            val inGroup = books.filter { it.group == group }
            groupStarts[group] = position
            row("group-$group") {
                Text(
                    t["screen.bible.group.$group"].uppercase(),
                    color = colors.primary, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 18.dp, bottom = 8.dp),
                )
            }
            inGroup.chunked(2).forEachIndexed { r, pair ->
                row("$group-$r") {
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        pair.forEach { book ->
                            BookTile(book, read[book.code]?.size ?: 0, t, Modifier.weight(1f)) { sheetBook = book.code }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        index?.let { row("credit") { TranslationNote(it) } }
    }

    sheetBook?.let { code ->
        BiblePickerSheet(
            store = store,
            startBook = code,
            current = lastRead,
            onDismiss = { sheetBook = null },
            onPick = { ref -> sheetBook = null; onOpenChapter(ref) },
        )
    }
}

@Composable
private fun ContinueCard(book: BibleBook, chapter: Int, line: String, onClick: () -> Unit) {
    val t = LocalScreenText.current
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape).background(BindingGreen).border(1.5.dp, DarkGold, shape)
            .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(t["screen.bible.continue"].uppercase(), color = DarkGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.height(5.dp))
        Text("${book.name} $chapter", color = CoverText, fontFamily = FontFamily.Serif, fontSize = 22.sp)
        if (line.isNotBlank()) {
            Spacer(Modifier.height(3.dp))
            Text(
                "“$line”", color = CoverMuted, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic,
                fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.height(10.dp))
        ProgressLine(chapter.toFloat() / book.chapterCount, track = Color.Black.copy(alpha = 0.3f))
    }
}

@Composable
private fun ProgressLine(fraction: Float, track: Color, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(track)) {
        Box(Modifier.fillMaxWidth(fraction.coerceIn(0f, 1f)).fillMaxHeight().background(DarkGold))
    }
}

@Composable
private fun BookmarksRow(count: Int, onClick: () -> Unit) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.fillMaxWidth().padding(top = 10.dp).clip(shape).background(colors.surfaceContainerLow).border(1.dp, CardBorder, shape)
            .clickable(role = Role.Button, onClick = onClick).heightIn(min = 48.dp).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.Bookmark, null, tint = colors.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(t["screen.bible.bookmarks"], color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(count.toString(), color = colors.primary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = colors.onSurfaceVariant)
    }
}

@Composable
private fun TestamentCover(title: String, count: String, cover: Color, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier.height(92.dp)
            .graphicsLayer { alpha = if (selected) 1f else 0.72f }
            .clip(shape).background(cover)
            .border(if (selected) 2.dp else 1.dp, DarkGold.copy(alpha = if (selected) 1f else 0.45f), shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = "$title, $count" }
            .padding(14.dp),
    ) {
        Text(title, color = CoverText, fontFamily = FontFamily.Serif, fontSize = 18.sp, lineHeight = 22.sp, maxLines = 2)
        Spacer(Modifier.weight(1f))
        Text(count, color = if (selected) DarkGold else CoverMuted, fontSize = 13.sp)
    }
}

@Composable
private fun JumpChip(label: String, active: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Text(
        label,
        color = if (active) colors.onPrimary else colors.onSurface,
        fontSize = 13.sp,
        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        maxLines = 1,
        modifier = Modifier.clip(shape)
            .background(if (active) colors.primary else colors.surfaceContainerLow)
            .border(1.dp, if (active) colors.primary else CardBorder, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    )
}

@Composable
private fun BookTile(book: BibleBook, readCount: Int, t: ScreenText, modifier: Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.clip(shape).background(colors.surfaceContainerLow).border(1.dp, CardBorder, shape)
            .clickable(role = Role.Button, onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp),
    ) {
        Text(book.short, color = colors.primary, fontFamily = FontFamily.Serif, fontSize = 22.sp, lineHeight = 26.sp)
        Text(book.name, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(t.chapters(book.chapterCount), color = colors.onSurfaceVariant, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        ProgressLine(
            readCount.toFloat() / book.chapterCount,
            track = colors.onSurface.copy(alpha = 0.08f),
            modifier = Modifier.graphicsLayer { alpha = if (readCount > 0) 1f else 0.6f },
        )
    }
}

@Composable
private fun SearchHitRow(hit: BibleSearchHit, query: String, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val words = query.lowercase().split(Regex("\\s+")).filter { it.isNotBlank() }
    val text = buildAnnotatedString {
        val body = hit.verse.text
        val lower = body.lowercase()
        var i = 0
        while (i < body.length) {
            val match = words.mapNotNull { w -> lower.indexOf(w, i).takeIf { it >= 0 }?.let { it to w } }.minByOrNull { it.first }
            if (match == null) { append(body.substring(i)); break }
            append(body.substring(i, match.first))
            withStyle(SpanStyle(color = colors.primary, fontWeight = FontWeight.SemiBold)) {
                append(body.substring(match.first, match.first + match.second.length))
            }
            i = match.first + match.second.length
        }
    }
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        Text("${hit.book.name} ${hit.chapter}:${hit.verse.number}", color = colors.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(2.dp))
        Text(text, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 16.sp, lineHeight = 23.sp)
    }
}

@Composable
private fun TranslationNote(index: BibleIndex) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(14.dp)
    Column(Modifier.fillMaxWidth().padding(top = 24.dp).clip(shape).background(colors.surfaceContainerHigh).padding(16.dp)) {
        Text(t["screen.bible.about_title"], color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 17.sp)
        Spacer(Modifier.height(6.dp))
        Text(t["screen.bible.about_body"], color = colors.onSurfaceVariant, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(8.dp))
        Text(index.credit, color = colors.onSurfaceVariant, fontSize = 11.sp, fontStyle = FontStyle.Italic)
    }
}

// ---------------------------------------------------------------- Pickers (slide-up sheet)

/**
 * The quick navigator: a sheet that slides up over the page. With [startBook] it opens on that
 * book's chapters; with null it starts on the list of books, then shows the chosen book's chapters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BiblePickerSheet(
    store: BibleStore,
    startBook: String?,
    current: BibleRef?,
    onDismiss: () -> Unit,
    onPick: (BibleRef) -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val index = rememberBibleIndex()
    var bookCode by remember { mutableStateOf(startBook) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surfaceContainerLow,
    ) {
        val books = index?.books ?: return@ModalBottomSheet
        val book = bookCode?.let { c -> books.find { it.code == c } }
        if (book == null) {
            BookPicker(books, current?.book) { bookCode = it }
        } else {
            ChapterPicker(
                book = book,
                read = remember(book.code) { store.readChapters(book.code) },
                stopped = current?.takeIf { it.book == book.code }?.chapter,
                onBack = if (startBook == null) ({ bookCode = null }) else null,
            ) { onPick(BibleRef(book.code, it)) }
        }
    }
}

@Composable
private fun BookPicker(books: List<BibleBook>, currentBook: String?, onPick: (String) -> Unit) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    var testament by remember { mutableStateOf(books.find { it.code == currentBook }?.testament ?: "ot") }
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Text(t["screen.bible.choose_book"], color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 24.sp)
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("ot" to "screen.bible.old_testament", "nt" to "screen.bible.new_testament").forEach { (id, key) ->
                Box(Modifier.weight(1f)) { JumpChipWide(t[key], active = testament == id) { testament = id } }
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            GROUPS.getValue(testament).forEach { group ->
                val inGroup = books.filter { it.group == group }
                if (inGroup.isEmpty()) return@forEach
                item(key = "h-$group", span = { GridItemSpan(2) }) {
                    Text(
                        t["screen.bible.group.$group"].uppercase(), color = colors.primary, fontSize = 12.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(top = 8.dp),
                    )
                }
                inGroup.forEach { book ->
                    item(key = book.code) {
                        val isCurrent = book.code == currentBook
                        val shape = RoundedCornerShape(12.dp)
                        Row(
                            Modifier.clip(shape)
                                .background(if (isCurrent) colors.primary.copy(alpha = 0.16f) else colors.surfaceContainer)
                                .border(1.dp, if (isCurrent) colors.primary else CardBorder, shape)
                                .clickable(role = Role.Button) { onPick(book.code) }
                                .heightIn(min = 46.dp).padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(book.name, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JumpChipWide(label: String, active: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Text(
        label,
        color = if (active) colors.onPrimary else colors.onSurface,
        fontSize = 14.sp,
        fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.fillMaxWidth().clip(shape)
            .background(if (active) colors.primary else colors.surfaceContainer)
            .border(1.dp, if (active) colors.primary else CardBorder, shape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 10.dp),
    )
}

@Composable
private fun ChapterPicker(book: BibleBook, read: Set<Int>, stopped: Int?, onBack: (() -> Unit)?, onPick: (Int) -> Unit) {
    val t = LocalScreenText.current
    val s = LocalStrings.current
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, s.back, tint = colors.primary) }
            }
            Text(book.name, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 24.sp, modifier = Modifier.weight(1f))
            Text(
                t.chapters(book.chapterCount) + if (read.isNotEmpty()) " · " + t.fmt("screen.bible.chapters_read", "n" to read.size) else "",
                color = colors.onSurfaceVariant, fontSize = 13.sp,
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp).navigationBarsPadding(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            items(book.chapterCount, key = { it }) { i ->
                val n = i + 1
                val isStopped = n == stopped
                val isRead = n in read
                val shape = RoundedCornerShape(10.dp)
                Box(
                    Modifier.aspectRatio(1f).clip(shape)
                        .background(
                            when {
                                isStopped -> DarkGold
                                isRead -> BindingGreen
                                else -> colors.surfaceContainer
                            },
                        )
                        .border(1.dp, if (isStopped || isRead) DarkGold.copy(alpha = 0.6f) else CardBorder, shape)
                        .clickable(role = Role.Button) { onPick(n) }
                        .semantics { contentDescription = t.fmt("screen.bible.chapter", "n" to n) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        n.toString(),
                        color = when {
                            isStopped -> Color(0xFF0E1A15)
                            isRead -> DarkGold
                            else -> colors.onSurface
                        },
                        fontSize = 16.sp,
                        fontWeight = if (isStopped) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
            item(key = "legend", span = { GridItemSpan(6) }) {
                Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    LegendDot(BindingGreen, t["screen.bible.legend_read"])
                    LegendDot(DarkGold, t["screen.bible.legend_stopped"])
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(color).border(1.dp, DarkGold.copy(alpha = 0.6f), RoundedCornerShape(3.dp)))
        Spacer(Modifier.width(6.dp))
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}

// ---------------------------------------------------------------- Reader: the parchment page

@Composable
fun BibleChapterScreen(
    repository: CathopediaRepository,
    start: BibleRef,
    onBack: () -> Unit,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val store = remember(repository) { BibleStore(repository) }
    // Picking or swiping replaces the chapter in place, so Back still returns to where the reader came from.
    var bookCode by rememberSaveable { mutableStateOf(start.book) }
    var chapter by rememberSaveable { mutableIntStateOf(start.chapter) }
    var target by rememberSaveable { mutableStateOf(start.verse) }
    var book by remember { mutableStateOf<BibleBook?>(null) }
    var paragraphs by remember { mutableStateOf<List<List<BibleVerse>>?>(null) }
    var bookmarked by remember { mutableStateOf(setOf<Int>()) }
    var previous by remember { mutableStateOf<BibleRef?>(null) }
    var next by remember { mutableStateOf<BibleRef?>(null) }
    var fontIndex by remember { mutableIntStateOf(FONT_STEPS.indexOf(store.fontScale()).takeIf { it >= 0 } ?: 1) }
    /** null: closed; "": the list of books; a code: that book's chapters. */
    var picker by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(bookCode, chapter) {
        paragraphs = null
        book = Bible.book(bookCode)
        val loaded = Bible.paragraphs(bookCode, chapter)
        paragraphs = loaded
        previous = Bible.neighbour(bookCode, chapter, -1)
        next = Bible.neighbour(bookCode, chapter, 1)
        bookmarked = store.bookmarks().filter { it.book == bookCode && it.chapter == chapter }.mapNotNull { it.verse }.toSet()
        store.setLastRead(BibleRef(bookCode, chapter))
        // Item 0 is the chapter heading; paragraph i is item i + 1.
        val goTo = target
        val i = if (goTo == null) -1 else loaded.indexOfFirst { p -> p.any { it.number == goTo } }
        listState.scrollToItem(if (i > 0) i + 1 else 0)
        // Reaching the end of the page marks the chapter as read.
        snapshotFlow {
            val info = listState.layoutInfo
            info.totalItemsCount > 0 && info.visibleItemsInfo.lastOrNull()?.index == info.totalItemsCount - 1
        }.first { it }
        store.markRead(BibleRef(bookCode, chapter))
    }

    fun go(ref: BibleRef) {
        target = ref.verse
        bookCode = ref.book
        chapter = ref.chapter
    }

    val scale = FONT_STEPS[fontIndex]
    val density = LocalDensity.current
    var headerPx by remember { mutableIntStateOf(0) }
    val headerHeight = with(density) { headerPx.toDp() }
    var drag by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = with(density) { 90.dp.toPx() }
    val prevRef by rememberUpdatedState(previous)
    val nextRef by rememberUpdatedState(next)

    Box(Modifier.fillMaxSize().background(Parchment)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
                .navigationBarsPadding()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val d = drag
                            drag = 0f
                            if (d < -swipeThreshold) nextRef?.let { go(BibleRef(it.book, it.chapter)) }
                            else if (d > swipeThreshold) prevRef?.let { go(BibleRef(it.book, it.chapter)) }
                        },
                        onDragCancel = { drag = 0f },
                    ) { change, amount ->
                        change.consume()
                        drag += amount
                    }
                }
                .graphicsLayer {
                    translationX = drag * 0.45f
                    alpha = 1f - (abs(drag) / (swipeThreshold * 5f)).coerceAtMost(0.35f)
                },
            contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = headerHeight + 10.dp, bottom = 24.dp),
        ) {
            val loaded = paragraphs
            if (loaded == null) {
                item(key = "loading") { Text(s.loading, color = ParchmentMuted, modifier = Modifier.padding(vertical = 16.dp)) }
            } else {
                item(key = "heading-$bookCode-$chapter") { ChapterHeading(book?.name.orEmpty(), chapter) }
                loaded.forEachIndexed { i, para ->
                    item(key = "p-$bookCode-$chapter-$i") {
                        ReaderParagraph(
                            verses = para,
                            first = i == 0,
                            scale = scale,
                            bookmarked = bookmarked,
                            target = target,
                            onTap = { if (target != null) target = null },
                        ) { verse ->
                            val nowMarked = store.toggleBookmark(BibleRef(bookCode, chapter, verse))
                            bookmarked = if (nowMarked) bookmarked + verse else bookmarked - verse
                        }
                    }
                }
                item(key = "hint") {
                    Text(
                        t["screen.bible.reader_hint"], color = ParchmentMuted, fontSize = 12.sp, fontStyle = FontStyle.Italic,
                        lineHeight = 17.sp, modifier = Modifier.padding(top = 18.dp),
                    )
                }
                item(key = "nav") {
                    Row(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChapterNavButton(previous, isNext = false, modifier = Modifier.weight(1f)) { go(it) }
                        ChapterNavButton(next, isNext = true, modifier = Modifier.weight(1f)) { go(it) }
                    }
                }
                item(key = "credit") {
                    Text(
                        t["screen.bible.credit"], color = ParchmentMuted, fontSize = 11.sp, fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = 20.dp, bottom = 24.dp),
                    )
                }
            }
        }

        ReaderHeader(
            bookName = book?.name.orEmpty(),
            chapter = chapter,
            onBack = onBack,
            onPickBook = { picker = "" },
            onPickChapter = { picker = bookCode },
            canShrink = fontIndex > 0,
            canGrow = fontIndex < FONT_STEPS.lastIndex,
            onShrink = { fontIndex--; store.setFontScale(FONT_STEPS[fontIndex]) },
            onGrow = { fontIndex++; store.setFontScale(FONT_STEPS[fontIndex]) },
            modifier = Modifier.onGloballyPositioned { if (headerPx != it.size.height) headerPx = it.size.height },
        )
    }

    picker?.let { open ->
        BiblePickerSheet(
            store = store,
            startBook = open.ifEmpty { null },
            current = BibleRef(bookCode, chapter),
            onDismiss = { picker = null },
            onPick = { ref -> picker = null; go(ref) },
        )
    }
}

@Composable
private fun ReaderHeader(
    bookName: String,
    chapter: Int,
    onBack: () -> Unit,
    onPickBook: () -> Unit,
    onPickChapter: () -> Unit,
    canShrink: Boolean,
    canGrow: Boolean,
    onShrink: () -> Unit,
    onGrow: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    Row(
        modifier.fillMaxWidth()
            .shadow(elevation = 14.dp, shape = shape, clip = false)
            .clip(shape)
            .background(ArticleHeader)
            .statusBarsPadding()
            .padding(start = 18.dp, top = 6.dp, end = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CathopediaBackButton(onClick = onBack, contentDescription = s.back)
        Spacer(Modifier.width(10.dp))
        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            HeaderPill(bookName, t["screen.bible.choose_book"], Modifier.weight(1f, fill = false), onPickBook)
            Spacer(Modifier.width(6.dp))
            HeaderPill(chapter.toString(), t["screen.bible.choose_chapter"], Modifier, onPickChapter)
        }
        Spacer(Modifier.width(8.dp))
        HeaderSizeButton("Aa−", t["screen.bible.text_smaller"], enabled = canShrink, onClick = onShrink)
        Spacer(Modifier.width(6.dp))
        HeaderSizeButton("Aa+", t["screen.bible.text_larger"], enabled = canGrow, onClick = onGrow)
    }
}

/** A small text-size button in the reader's header. */
@Composable
private fun HeaderSizeButton(label: String, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Box(
        Modifier.size(width = 46.dp, height = 34.dp).clip(shape)
            .border(1.dp, if (enabled) CardBorder else CardBorder.copy(alpha = 0.3f), shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = if (enabled) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.4f),
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun HeaderPill(label: String, description: String, modifier: Modifier, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(50)
    Row(
        modifier.clip(shape).border(1.5.dp, colors.primary, shape)
            .clickable(role = Role.Button, onClickLabel = description, onClick = onClick)
            .heightIn(min = 40.dp).padding(start = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 17.sp,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false),
        )
        Icon(Icons.Default.ArrowDropDown, null, tint = colors.primary)
    }
}

/**
 * The top of the page: the book's name in small gold capitals over "Chapter 23", with the
 * chapter number repeated large and faint behind it as a watermark.
 */
@Composable
private fun ChapterHeading(bookName: String, chapter: Int) {
    val t = LocalScreenText.current
    val gold = ParchmentGold
    Box(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 6.dp)) {
        Text(
            chapter.toString(),
            color = gold.copy(alpha = 0.10f),
            fontFamily = FontFamily.Serif,
            fontSize = 190.sp,
            lineHeight = 190.sp,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Visible,
            // Takes no space: drawn from the top-right corner, behind the heading and first lines.
            modifier = Modifier.align(Alignment.TopEnd).layout { measurable, _ ->
                val placeable = measurable.measure(Constraints())
                layout(0, 0) { placeable.place(-placeable.width, (-34).dp.roundToPx()) }
            },
        )
        Column {
            Text(
                bookName.uppercase(), color = gold, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.5.sp,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
            Text(
                t.fmt("screen.bible.chapter", "n" to chapter),
                color = ParchmentInk, fontFamily = FontFamily.Serif, fontSize = 34.sp, lineHeight = 40.sp,
            )
        }
    }
}

/**
 * One paragraph as flowing text, verse numbers in small gold superscript. Press and hold a verse
 * to bookmark it; bookmarked verses carry a soft gold wash.
 */
@Composable
private fun ReaderParagraph(
    verses: List<BibleVerse>,
    first: Boolean,
    scale: Float,
    bookmarked: Set<Int>,
    target: Int?,
    onTap: () -> Unit,
    onLongPress: (Int) -> Unit,
) {
    val gold = ParchmentGold
    val (text, ranges) = remember(verses, scale, bookmarked, target, gold) {
        val ranges = mutableListOf<Pair<IntRange, Int>>()
        val text = buildAnnotatedString {
            verses.forEachIndexed { i, v ->
                if (i > 0) append(" ")
                val start = length
                val wash = when (v.number) {
                    target -> gold.copy(alpha = 0.30f)
                    in bookmarked -> gold.copy(alpha = 0.16f)
                    else -> null
                }
                withStyle(
                    SpanStyle(
                        color = gold, fontSize = (11 * scale).sp, fontWeight = FontWeight.SemiBold,
                        baselineShift = BaselineShift.Superscript,
                    ),
                ) { append(v.number.toString()) }
                // A no-break space keeps the number on the same line as its verse.
                append("\u00A0")
                if (wash != null) withStyle(SpanStyle(background = wash)) { append(v.text) } else append(v.text)
                ranges += (start until length) to v.number
            }
        }
        text to ranges
    }
    val style = TextStyle(
        color = ParchmentInk,
        fontFamily = FontFamily.Serif,
        fontSize = (17.5f * scale).sp,
        lineHeight = (29 * scale).sp,
        textAlign = TextAlign.Justify,
        textIndent = if (first) null else TextIndent(firstLine = (18 * scale).sp),
    )
    VerseText(text, ranges, 0, style, Modifier.fillMaxWidth().padding(top = if (first) 12.dp else 10.dp), onTap, onLongPress)
}

/** Paragraph text that maps a long press back to its verse. [base] is where [text] starts in the paragraph. */
@Composable
private fun VerseText(
    text: AnnotatedString,
    ranges: List<Pair<IntRange, Int>>,
    base: Int,
    style: TextStyle,
    modifier: Modifier,
    onTap: () -> Unit,
    onLongPress: (Int) -> Unit,
) {
    val tap by rememberUpdatedState(onTap)
    val press by rememberUpdatedState(onLongPress)
    var layout by remember { mutableStateOf<TextLayoutResult?>(null) }
    Text(
        text,
        style = style,
        onTextLayout = { layout = it },
        modifier = modifier.pointerInput(text, base) {
            detectTapGestures(
                onTap = { tap() },
                onLongPress = { pos ->
                    val offset = (layout?.getOffsetForPosition(pos) ?: return@detectTapGestures) + base
                    ranges.firstOrNull { offset in it.first }?.second?.let(press)
                },
            )
        },
    )
}

@Composable
private fun ChapterNavButton(ref: BibleRef?, isNext: Boolean, modifier: Modifier, onClick: (BibleRef) -> Unit) {
    val t = LocalScreenText.current
    var name by remember(ref) { mutableStateOf("") }
    LaunchedEffect(ref) { name = ref?.let { r -> Bible.book(r.book)?.let { "${it.name} ${r.chapter}" } }.orEmpty() }
    if (ref == null) {
        Spacer(modifier)
        return
    }
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier.clip(shape).background(ParchmentInk.copy(alpha = 0.05f)).border(1.dp, ParchmentGold.copy(alpha = 0.55f), shape)
            .clickable(role = Role.Button) { onClick(ref) }.heightIn(min = 56.dp).padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isNext) Arrangement.End else Arrangement.Start,
    ) {
        if (!isNext) Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = ParchmentGold)
        Column(horizontalAlignment = if (isNext) Alignment.End else Alignment.Start, modifier = Modifier.weight(1f, fill = false)) {
            Text(if (isNext) t["screen.bible.next"] else t["screen.bible.previous"], color = ParchmentMuted, fontSize = 11.sp)
            Text(name, color = ParchmentInk, fontFamily = FontFamily.Serif, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (isNext) Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = ParchmentGold)
    }
}

// ---------------------------------------------------------------- Bookmarks

@Composable
fun BibleBookmarksScreen(repository: CathopediaRepository, onBack: () -> Unit, onOpenChapter: (BibleRef) -> Unit) {
    val s = LocalStrings.current
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val store = remember(repository) { BibleStore(repository) }
    var marks by remember { mutableStateOf<List<Pair<BibleRef, String>>?>(null) }
    var version by remember { mutableIntStateOf(0) }

    LaunchedEffect(version) {
        marks = store.bookmarks().map { ref ->
            val name = Bible.book(ref.book)?.name ?: ref.book
            val text = Bible.chapter(ref.book, ref.chapter).find { it.number == ref.verse }?.text.orEmpty()
            ref to "$name ${ref.chapter}:${ref.verse}\u0000$text"
        }
    }

    ArticleScaffold(
        title = t["screen.bible.bookmarks"],
        subtitle = t["screen.bible.subtitle_short"],
        backDescription = s.back,
        onBack = onBack,
        horizontalPadding = 16.dp,
    ) {
        val list = marks ?: return@ArticleScaffold
        if (list.isEmpty()) {
            item(key = "empty") {
                Text(t["screen.bible.bookmarks_empty"], color = colors.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp))
            }
        }
        list.forEach { (ref, joined) ->
            val (title, text) = joined.split('\u0000', limit = 2)
            item(key = ref.key()) {
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable(role = Role.Button) { onOpenChapter(ref) }
                        .padding(start = 8.dp, top = 10.dp, bottom = 10.dp),
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(title, color = colors.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(2.dp))
                        Text(text, color = colors.onSurface, fontFamily = FontFamily.Serif, fontSize = 16.sp, lineHeight = 23.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(onClick = { store.toggleBookmark(ref); version++ }) {
                        Icon(Icons.Default.BookmarkBorder, t["screen.bible.bookmark_remove"], tint = colors.primary)
                    }
                }
            }
        }
    }
}
