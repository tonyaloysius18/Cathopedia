package com.ynotlabs.cathopedia.ui.screens.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.ynotlabs.cathopedia.i18n.LocalStrings
import com.ynotlabs.cathopedia.ui.components.CathopediaBackButton

/** Full-screen, swipeable image viewer with pinch zoom and a persistent caption area. */
@Composable
internal fun FullscreenImageViewer(
    pageCount: Int,
    startIndex: Int = 0,
    title: String,
    captionForPage: (Int) -> String,
    painterForPage: @Composable (Int) -> Painter?,
    onDismiss: () -> Unit,
) {
    if (pageCount <= 0) return

    val s = LocalStrings.current
    val pagerState = rememberPagerState(
        initialPage = startIndex.coerceIn(0, pageCount - 1),
    ) { pageCount }

    Dialog(
        onDismissRequest = onDismiss,
        properties = fullScreenDialogProperties(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF030605)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(top = 60.dp, bottom = 8.dp),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                    ) { page ->
                        painterForPage(page)?.let { painter ->
                            ZoomablePageImage(
                                painter = painter,
                                contentDescription = captionForPage(page).ifBlank { title },
                                page = page,
                            )
                        }
                    }
                }

                ViewerFooter(
                    caption = captionForPage(pagerState.currentPage),
                    title = title,
                    currentPage = pagerState.currentPage,
                    pageCount = pageCount,
                )
            }

            CathopediaBackButton(
                onClick = onDismiss,
                contentDescription = s.closeFullImage,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun ZoomablePageImage(
    painter: Painter,
    contentDescription: String,
    page: Int,
) {
    var scale by remember(page) { mutableStateOf(1f) }
    var pan by remember(page) { mutableStateOf(Offset.Zero) }
    var viewport by remember(page) { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { viewport = it }
            .pointerInput(page, viewport) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    var isTransforming = false
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.count { it.pressed } >= 2) {
                            isTransforming = true
                        }

                        // Leave an unzoomed one-finger drag to HorizontalPager. Once a pinch
                        // begins (or the image is zoomed), this child owns the pan gesture.
                        if (isTransforming || scale > 1f) {
                            val nextScale = (scale * event.calculateZoom()).coerceIn(1f, 5f)
                            val maxPanX = viewport.width * (nextScale - 1f) / 2f
                            val maxPanY = viewport.height * (nextScale - 1f) / 2f
                            val nextPan = if (nextScale == 1f) Offset.Zero else pan + event.calculatePan()
                            scale = nextScale
                            pan = Offset(
                                x = nextPan.x.coerceIn(-maxPanX, maxPanX),
                                y = nextPan.y.coerceIn(-maxPanY, maxPanY),
                            )
                            event.changes.forEach { it.consume() }
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = pan.x
                    translationY = pan.y
                },
        )
    }
}

@Composable
private fun ViewerFooter(
    caption: String,
    title: String,
    currentPage: Int,
    pageCount: Int,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF07100D))
            .padding(horizontal = 20.dp, vertical = 14.dp),
    ) {
        if (caption.isNotBlank()) {
            Text(
                text = caption,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(8.dp))
        }

        if (pageCount > 1) {
            ScrollablePageDots(currentPage = currentPage, pageCount = pageCount)
            Spacer(Modifier.height(6.dp))
        }

        Text(
            text = "$title · ${currentPage + 1}/$pageCount",
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 10.sp,
            letterSpacing = 0.45.sp,
        )
    }
}

@Composable
private fun ScrollablePageDots(currentPage: Int, pageCount: Int) {
    val listState = rememberLazyListState()
    LaunchedEffect(currentPage) {
        listState.animateScrollToItem(currentPage)
    }

    LazyRow(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp),
        contentPadding = PaddingValues(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items((0 until pageCount).toList()) { index ->
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .height(28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .width(if (index == currentPage) 18.dp else 7.dp)
                        .height(7.dp)
                        .background(
                            color = if (index == currentPage) {
                                Color(0xFFD6AE3D)
                            } else {
                                Color.White.copy(alpha = 0.34f)
                            },
                            shape = if (index == currentPage) RoundedCornerShape(50) else CircleShape,
                        ),
                )
            }
        }
    }
}
