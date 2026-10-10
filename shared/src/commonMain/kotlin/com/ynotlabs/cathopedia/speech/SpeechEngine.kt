package com.ynotlabs.cathopedia.speech

import androidx.compose.runtime.Composable

/**
 * The device's own text-to-speech, one utterance at a time. Android wraps
 * TextToSpeech and iOS wraps AVSpeechSynthesizer. Both pick the voice through
 * [SpeechVoices], so each language is read with its own accent.
 */
interface SpeechEngine {
    /** False while the platform engine is still starting (Android binds a service). */
    val isReady: Boolean

    /** Whether this device has a voice for [language]. */
    fun hasVoice(language: String): Boolean

    /**
     * Reads [text] aloud, replacing anything already being read. [rate] is a
     * multiplier on the app's prayer pace (1 = normal). [onDone] runs on the main
     * thread when the text finishes, never after [stop].
     */
    fun speak(text: String, language: String, rate: Float, onDone: () -> Unit)

    fun stop()

    /** True where the app can send the user to install a missing voice (Android). */
    val canInstallVoices: Boolean

    fun openVoiceInstall()

    fun release()
}

/** The platform engine, alive while the calling composable is. */
@Composable
expect fun rememberSpeechEngine(): SpeechEngine

object SpeechVoices {
    /**
     * The voice locale for each content language, chosen for the accent of the
     * texts the app ships: Brazilian Portuguese (the pt prayers are the texts in use
     * in Brazil), Spanish from Spain (the es prayers follow the Spanish Compendium),
     * Tamil from India. Latin has no voice on any platform, so it is read by an
     * Italian voice, which is close to the Church's (ecclesiastical) pronunciation.
     */
    fun localeFor(language: String): String = when (language.lowercase()) {
        "en" -> "en-US"
        "la", "it" -> "it-IT"
        "fr" -> "fr-FR"
        "es" -> "es-ES"
        "pt" -> "pt-BR"
        "de" -> "de-DE"
        "nl" -> "nl-NL"
        "pl" -> "pl-PL"
        "ta" -> "ta-IN"
        else -> language
    }
}
