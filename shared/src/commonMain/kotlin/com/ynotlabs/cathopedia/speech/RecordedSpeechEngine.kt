package com.ynotlabs.cathopedia.speech

/** Plays one recorded part out of a pack file. */
interface SegmentPlayer {
    fun play(segment: AudioSegment, rate: Float, onDone: () -> Unit)
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

    override fun speak(text: String, language: String, rate: Float, onDone: () -> Unit) {
        stop()
        val segment = PrayerAudioPacks.segment(language, text)
        if (segment != null) player.play(segment, rate, onDone) else deviceVoice.speak(text, language, rate, onDone)
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
