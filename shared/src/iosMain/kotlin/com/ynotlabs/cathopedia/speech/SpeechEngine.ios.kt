package com.ynotlabs.cathopedia.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDuckOthers
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeSpokenAudio
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.AVSpeechBoundary
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesisVoiceQualityDefault
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechSynthesizerDelegateProtocol
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechUtteranceDefaultSpeechRate
import platform.AVFAudio.setActive
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioPlayerDelegateProtocol
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSDataReadingMappedIfSafe
import platform.Foundation.NSMakeRange
import platform.Foundation.NSRange
import platform.Foundation.NSTimer
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.subdataWithRange
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberSpeechEngine(): SpeechEngine {
    val engine = remember {
        PrayerAudioPacks.directory = applicationSupportDirectory() + "/prayer_audio"
        RecordedSpeechEngine(IosSpeechEngine(), IosSegmentPlayer())
    }
    DisposableEffect(engine) { onDispose { engine.release() } }
    return engine
}

/** Normal speed for prayer: a little slower than AVSpeech's default rate. */
private const val PRAYER_PACE = 0.88f

@OptIn(ExperimentalForeignApi::class)
private class IosSpeechEngine : SpeechEngine {
    private val synthesizer = AVSpeechSynthesizer()
    private var current: AVSpeechUtterance? = null
    private var onDone: (() -> Unit)? = null
    private var onProgress: ((Int) -> Unit)? = null
    /** Where in the part's text the spoken substring starts. */
    private var base = 0

    // The synthesizer holds its delegate weakly, so the engine keeps it alive.
    private val delegate = object : NSObject(), AVSpeechSynthesizerDelegateProtocol {
        override fun speechSynthesizer(
            synthesizer: AVSpeechSynthesizer,
            didFinishSpeechUtterance: AVSpeechUtterance,
        ) {
            if (didFinishSpeechUtterance != current) return
            val callback = onDone ?: return
            onDone = null
            onProgress = null
            dispatch_async(dispatch_get_main_queue()) { callback() }
        }

        // Called as each word starts, with its range in the spoken substring.
        override fun speechSynthesizer(
            synthesizer: AVSpeechSynthesizer,
            willSpeakRangeOfSpeechString: CValue<NSRange>,
            utterance: AVSpeechUtterance,
        ) {
            if (utterance != current) return
            val progress = onProgress ?: return
            val offset = base + willSpeakRangeOfSpeechString.useContents { location.toInt() }
            dispatch_async(dispatch_get_main_queue()) { if (onProgress === progress) progress(offset) }
        }
    }

    init {
        synthesizer.delegate = delegate
    }

    override val isReady: Boolean = true

    /**
     * The best voice with the language's own accent: an enhanced or premium voice
     * when the user has downloaded one, else the system's default for that locale.
     */
    private fun bestVoice(tag: String): AVSpeechSynthesisVoice? {
        val voices = AVSpeechSynthesisVoice.speechVoices()
            .filterIsInstance<AVSpeechSynthesisVoice>()
            .filter { it.language.equals(tag, ignoreCase = true) }
        return voices.filter { it.quality != AVSpeechSynthesisVoiceQualityDefault }.maxByOrNull { it.quality }
            ?: AVSpeechSynthesisVoice.voiceWithLanguage(tag)
            ?: voices.firstOrNull()
    }

    override fun hasVoice(language: String): Boolean = bestVoice(SpeechVoices.localeFor(language)) != null

    override fun speak(
        text: String,
        language: String,
        rate: Float,
        startAt: Int,
        onProgress: (Int) -> Unit,
        onDone: () -> Unit,
    ) {
        SpokenAudioSession.activate()
        stop()
        base = startAt.coerceIn(0, text.length)
        val utterance = AVSpeechUtterance(string = text.substring(base)).apply {
            voice = bestVoice(SpeechVoices.localeFor(language))
            this.rate = (AVSpeechUtteranceDefaultSpeechRate * PRAYER_PACE * rate)
            postUtteranceDelay = 0.25
        }
        current = utterance
        this.onDone = onDone
        this.onProgress = onProgress
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        onDone = null
        onProgress = null
        current = null
        if (synthesizer.speaking) synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    // iOS gives apps no way to open the voice download screen.
    override val canInstallVoices: Boolean = false

    override fun openVoiceInstall() = Unit

    override fun release() {
        stop()
        SpokenAudioSession.deactivate()
    }
}

/** Plays through the silent switch, the way spoken-word apps do, and ducks other audio. */
@OptIn(ExperimentalForeignApi::class)
private object SpokenAudioSession {
    private var active = false

    fun activate() {
        if (active) return
        val session = AVAudioSession.sharedInstance()
        session.setCategory(
            AVAudioSessionCategoryPlayback,
            mode = AVAudioSessionModeSpokenAudio,
            options = AVAudioSessionCategoryOptionDuckOthers,
            error = null,
        )
        active = session.setActive(true, error = null)
    }

    fun deactivate() {
        if (!active) return
        AVAudioSession.sharedInstance().setActive(
            false,
            withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
            error = null,
        )
        active = false
    }
}

/** Plays a recorded part: the pack is memory-mapped and only the part's bytes are handed to AVAudioPlayer. */
@OptIn(ExperimentalForeignApi::class)
private class IosSegmentPlayer : SegmentPlayer {
    private var player: AVAudioPlayer? = null
    private var onDone: (() -> Unit)? = null
    private var timer: NSTimer? = null

    private val delegate = object : NSObject(), AVAudioPlayerDelegateProtocol {
        override fun audioPlayerDidFinishPlaying(player: AVAudioPlayer, successfully: Boolean) {
            if (player != this@IosSegmentPlayer.player) return
            finish()
        }
    }

    private fun finish() {
        val callback = onDone ?: return
        onDone = null
        player = null
        timer?.invalidate()
        timer = null
        dispatch_async(dispatch_get_main_queue()) { callback() }
    }

    override fun play(
        segment: AudioSegment,
        rate: Float,
        startFraction: Float,
        onProgress: (Float) -> Unit,
        onDone: () -> Unit,
    ) {
        stop()
        SpokenAudioSession.activate()
        this.onDone = onDone
        val data = NSData.dataWithContentsOfFile(segment.path, NSDataReadingMappedIfSafe, null)
        val slice = data?.subdataWithRange(NSMakeRange(segment.offset.toULong(), segment.length.toULong()))
        val audio = slice?.let { AVAudioPlayer(data = it, error = null) }
        if (audio == null) return finish()
        audio.delegate = delegate
        audio.enableRate = true
        audio.rate = rate
        if (startFraction > 0f) audio.currentTime = audio.duration * startFraction
        player = audio
        if (!audio.play()) return finish()
        // AVAudioPlayer has no position callback, so poll it a few times a second.
        timer = NSTimer.scheduledTimerWithTimeInterval(0.2, repeats = true) { _ ->
            val playing = player ?: return@scheduledTimerWithTimeInterval
            if (playing.duration > 0) onProgress((playing.currentTime / playing.duration).toFloat())
        }
    }

    override fun stop() {
        onDone = null
        timer?.invalidate()
        timer = null
        player?.stop()
        player = null
    }

    override fun release() = stop()
}

private fun applicationSupportDirectory(): String =
    (NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, true).firstOrNull() as? String)
        ?: NSTemporaryDirectory()
