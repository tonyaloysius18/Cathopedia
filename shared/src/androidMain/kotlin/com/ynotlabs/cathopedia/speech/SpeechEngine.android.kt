package com.ynotlabs.cathopedia.speech

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

@Composable
actual fun rememberSpeechEngine(): SpeechEngine {
    val context = LocalContext.current
    val engine = remember { AndroidSpeechEngine(context.applicationContext) }
    DisposableEffect(engine) { onDispose { engine.release() } }
    return engine
}

/** Normal speed for prayer: a little slower than the engine's conversational default. */
private const val PRAYER_PACE = 0.9f

private class AndroidSpeechEngine(private val context: Context) : SpeechEngine {
    private val main = Handler(Looper.getMainLooper())
    private val callbacks = ConcurrentHashMap<String, () -> Unit>()
    private var counter = 0
    private var pending: (() -> Unit)? = null
    private var voiceTag: String? = null
    private var released = false

    override var isReady by mutableStateOf(false)
        private set

    private val tts: TextToSpeech = TextToSpeech(context) { status ->
        main.post {
            if (released) return@post
            isReady = status == TextToSpeech.SUCCESS
            pending?.takeIf { isReady }?.invoke()
            pending = null
        }
    }

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) = Unit
            override fun onDone(utteranceId: String) = finish(utteranceId)

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) = finish(utteranceId)
            override fun onError(utteranceId: String, errorCode: Int) = finish(utteranceId)
        })
    }

    private fun finish(utteranceId: String) {
        callbacks.remove(utteranceId)?.let { main.post(it) }
    }

    /** The best installed voice with the language's own accent (language and country both match). */
    private fun bestVoice(tag: String): Voice? {
        val want = Locale.forLanguageTag(tag)
        return runCatching { tts.voices }.getOrNull().orEmpty()
            .filter { it.locale.language == want.language && it.locale.country == want.country }
            .filterNot { TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED in it.features.orEmpty() }
            .sortedWith(compareBy<Voice> { it.isNetworkConnectionRequired }.thenByDescending { it.quality })
            .firstOrNull()
    }

    override fun hasVoice(language: String): Boolean {
        if (!isReady) return true
        val tag = SpeechVoices.localeFor(language)
        return bestVoice(tag) != null ||
            tts.isLanguageAvailable(Locale.forLanguageTag(tag)) >= TextToSpeech.LANG_AVAILABLE
    }

    override fun speak(text: String, language: String, rate: Float, onDone: () -> Unit) {
        if (released) return
        if (!isReady) {
            pending = { speak(text, language, rate, onDone) }
            return
        }
        val tag = SpeechVoices.localeFor(language)
        if (tag != voiceTag) {
            val voice = bestVoice(tag)
            if (voice != null) tts.voice = voice else tts.language = Locale.forLanguageTag(tag)
            voiceTag = tag
        }
        tts.setSpeechRate(rate * PRAYER_PACE)
        callbacks.clear()
        val id = "prayer-${++counter}"
        callbacks[id] = onDone
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, id)
    }

    override fun stop() {
        pending = null
        callbacks.clear()
        if (isReady) tts.stop()
    }

    override val canInstallVoices: Boolean = true

    override fun openVoiceInstall() {
        val install = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val settings = Intent("com.android.settings.TTS_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(install)
        } catch (_: ActivityNotFoundException) {
            runCatching { context.startActivity(settings) }
        }
    }

    override fun release() {
        released = true
        stop()
        tts.shutdown()
    }
}
