package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.currentItem
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.volume
import platform.CoreGraphics.CGRectZero
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIColor
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformStartupVideo(
    uri: String,
    modifier: Modifier,
    onFinished: () -> Unit,
) {
    val currentOnFinished = rememberUpdatedState(onFinished)

    val player = remember(uri) {
        AVPlayer(uRL = NSURL(string = uri)).apply {
            volume = 0f
        }
    }

    val playerLayer = remember(player) {
        AVPlayerLayer().apply {
            this.player = player
            // Scale the video to cover the full display without stretching
            // (Aspect Fill). This removes the green bars above/below.
            // Preserve the full frame rather than cropping its sides on tall screens.
            videoGravity = AVLayerVideoGravityResizeAspect
        }
    }

    DisposableEffect(player) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = AVPlayerItemDidPlayToEndTimeNotification,
            `object` = player.currentItem,
            queue = NSOperationQueue.mainQueue,
        ) { _ ->
            currentOnFinished.value()
        }

        player.play()

        onDispose {
            player.pause()
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            val container = object : UIView(frame = CGRectZero.readValue()) {
                override fun layoutSubviews() {
                    super.layoutSubviews()
                    CATransaction.begin()
                    CATransaction.setValue(true, kCATransactionDisableActions)
                    playerLayer.setFrame(bounds)
                    CATransaction.commit()
                }
            }

            container.backgroundColor = UIColor.colorWithRed(
                red = 6.0 / 255.0,
                green = 26.0 / 255.0,
                blue = 19.0 / 255.0,
                alpha = 1.0,
            )
            container.layer.addSublayer(playerLayer)
            container
        },
        update = {
            playerLayer.player = player
        },
    )
}
