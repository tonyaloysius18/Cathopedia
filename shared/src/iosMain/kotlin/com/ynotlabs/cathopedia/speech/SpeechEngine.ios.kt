package com.ynotlabs.cathopedia.speech

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
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
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberSpeechEngine(): SpeechEngine {
    val engine = remember { IosSpeechEngine() }
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
    private var sessionActive = false

    // The synthesizer holds its delegate weakly, so the engine keeps it alive.
    private val delegate = object : NSObject(), AVSpeechSynthesizerDelegateProtocol {
        override fun speechSynthesizer(
            synthesizer: AVSpeechSynthesizer,
            didFinishSpeechUtterance: AVSpeechUtterance,
        ) {
            if (didFinishSpeechUtterance != current) return
            val callback = onDone ?: return
            onDone = null
            dispatch_async(dispatch_get_main_queue()) { callback() }
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

    override fun speak(text: String, language: String, rate: Float, onDone: () -> Unit) {
        activateSession()
        stop()
        val utterance = AVSpeechUtterance(string = text).apply {
            voice = bestVoice(SpeechVoices.localeFor(language))
            this.rate = (AVSpeechUtteranceDefaultSpeechRate * PRAYER_PACE * rate)
            postUtteranceDelay = 0.25
        }
        current = utterance
        this.onDone = onDone
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        onDone = null
        current = null
        if (synthesizer.speaking) synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    // iOS gives apps no way to open the voice download screen.
    override val canInstallVoices: Boolean = false

    override fun openVoiceInstall() = Unit

    /** Plays through the silent switch, the way spoken-word apps do, and ducks other audio. */
    private fun activateSession() {
        if (sessionActive) return
        val session = AVAudioSession.sharedInstance()
        session.setCategory(
            AVAudioSessionCategoryPlayback,
            mode = AVAudioSessionModeSpokenAudio,
            options = AVAudioSessionCategoryOptionDuckOthers,
            error = null,
        )
        sessionActive = session.setActive(true, error = null)
    }

    override fun release() {
        stop()
        if (sessionActive) {
            AVAudioSession.sharedInstance().setActive(
                false,
                withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
                error = null,
            )
            sessionActive = false
        }
    }
}
