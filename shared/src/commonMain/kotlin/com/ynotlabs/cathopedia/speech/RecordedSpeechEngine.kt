package com.ynotlabs.cathopedia.speech

/** Plays one recorded part out of a pack file. */
interface SegmentPlayer {
    /**
     * Plays [segment] from [startFraction] of its length, reporting how far it has
     * got (0..1) through [onProgress] on the main thread.
     */
    fun play(
        segment: AudioSegment,
        rate: Float,
        startFraction: Float,
        onProgress: (Float) -> Unit,
        onDone: () -> Unit,
    )
    fun stop()
    fun release()
}

/**
 * Reads each part from the recorded pack when the language has one and the
 * part is in it, and with the device voice otherwise, so the player never
 * goes silent because a recording is missing.
 */
internal class RecordedSpeechEngine(
    private val deviceVoice: SpeechEngine,
    private val player: SegmentPlayer,
) : SpeechEngine {
    override val isReady: Boolean get() = deviceVoice.isReady

    override fun hasVoice(language: String): Boolean =
        PrayerAudioPacks.hasPack(language) || deviceVoice.hasVoice(language)

    override fun speak(
        text: String,
        language: String,
        rate: Float,
        startAt: Int,
        onProgress: (Int) -> Unit,
        onDone: () -> Unit,
    ) {
        stop()
        val segment = PrayerAudioPacks.segment(language, text)
        if (segment == null) {
            deviceVoice.speak(text, language, rate, startAt, onProgress, onDone)
            return
        }
        // A recording has no word timings, so a place in the text maps to the same
        // fraction of the recording's length.
        val length = text.length.coerceAtLeast(1)
        player.play(
            segment,
            rate,
            startFraction = startAt.toFloat() / length,
            onProgress = { onProgress((it * length).toInt().coerceIn(0, text.length)) },
            onDone = onDone,
        )
    }

    override fun stop() {
        player.stop()
        deviceVoice.stop()
    }

    override val canInstallVoices: Boolean get() = deviceVoice.canInstallVoices

    override fun openVoiceInstall() = deviceVoice.openVoiceInstall()

    override fun release() {
        player.release()
        deviceVoice.release()
    }
}
