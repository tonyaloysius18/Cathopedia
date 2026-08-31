package com.ynotlabs.cathopedia.ui.screens.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ynotlabs.cathopedia.data.CathopediaRepository
import com.ynotlabs.cathopedia.resources.Res
import org.jetbrains.compose.resources.ExperimentalResourceApi

private val CathopediaSplashGreen = Color(0xFF061A13)

/**
 * Plays the Cathopedia startup animation while the bundled content/database
 * initializes in parallel.
 *
 * Navigation continues only after BOTH:
 * 1. repository.ensureContentLoaded() has finished, and
 * 2. startup_cathopedia.mp4 has finished playing.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SplashScreen(
    isFirstRun: Boolean,
    repository: CathopediaRepository,
    onReady: (Boolean) -> Unit,
) {
    var contentReady by remember { mutableStateOf(false) }
    var videoFinished by remember { mutableStateOf(false) }
    var navigationTriggered by remember { mutableStateOf(false) }
    val videoUri = Res.getUri("files/startup_cathopedia.mp4")

    // Load/prepare Cathopedia data while the video is playing.
    LaunchedEffect(repository) {
        repository.ensureContentLoaded()
        contentReady = true
    }

    // Continue only when initialization AND the startup animation are complete.
    LaunchedEffect(contentReady, videoFinished) {
        if (contentReady && videoFinished && !navigationTriggered) {
            navigationTriggered = true
            onReady(isFirstRun)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CathopediaSplashGreen),
    ) {
        PlatformStartupVideo(
            uri = videoUri,
            modifier = Modifier.fillMaxSize(),
            onFinished = {
                videoFinished = true
            },
        )
    }
}

/**
 * Platform implementation of the startup video.
 *
 * Android implementation:
 * androidMain/.../ui/screens/PlatformStartupVideo.android.kt
 */
@Composable
expect fun PlatformStartupVideo(
    uri: String,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit,
)
