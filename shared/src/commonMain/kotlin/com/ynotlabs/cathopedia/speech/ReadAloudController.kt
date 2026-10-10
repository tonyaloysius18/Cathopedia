package com.ynotlabs.cathopedia.speech

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Plays a prayer's [SpeechUnit]s one after another through a [SpeechEngine],
 * with pause, previous/next and a reading speed. Compose reads its state directly.
 */
class ReadAloudController(private val engine: SpeechEngine) {
    var units by mutableStateOf<List<SpeechUnit>>(emptyList())
        private set
    var language by mutableStateOf("en")
        private set
    var index by mutableIntStateOf(0)
        private set
    var isPlaying by mutableStateOf(false)
        private set

    /** True from the first play until the prayer ends or is reloaded, so the place is kept on pause. */
    var hasStarted by mutableStateOf(false)
        private set
    var speedIndex by mutableIntStateOf(DEFAULT_SPEED_INDEX)
        private set

    /** Bumped on every new utterance so a late "done" from an interrupted one is ignored. */
    private var generation = 0

    /** The unit to highlight, or null when nothing has been played. */
    val current: SpeechUnit? get() = if (hasStarted) units.getOrNull(index) else null

    fun load(units: List<SpeechUnit>, language: String) {
        if (units == this.units && language == this.language) return
        stopSpeaking()
        this.units = units
        this.language = language
        index = 0
        isPlaying = false
        hasStarted = false
    }

    fun toggle() = if (isPlaying) pause() else play()

    fun play() {
        if (units.isEmpty()) return
        if (index !in units.indices) index = 0
        isPlaying = true
        hasStarted = true
        speakCurrent()
    }

    fun pause() {
        isPlaying = false
        stopSpeaking()
    }

    fun next() = moveTo(index + 1)

    fun previous() = moveTo(index - 1)

    fun cycleSpeed() {
        speedIndex = (speedIndex + 1) % SPEEDS.size
        if (isPlaying) speakCurrent()
    }

    fun release() {
        stopSpeaking()
        isPlaying = false
    }

    private fun moveTo(target: Int) {
        if (units.isEmpty()) return
        index = target.coerceIn(0, units.lastIndex)
        hasStarted = true
        if (isPlaying) speakCurrent()
    }

    private fun speakCurrent() {
        val unit = units.getOrNull(index) ?: return
        val token = ++generation
        engine.speak(unit.text, language, SPEEDS[speedIndex]) {
            if (token == generation && isPlaying) onUnitDone()
        }
    }

    private fun onUnitDone() {
        if (index < units.lastIndex) {
            index++
            speakCurrent()
        } else {
            isPlaying = false
            hasStarted = false
            index = 0
        }
    }

    private fun stopSpeaking() {
        generation++
        engine.stop()
    }

    companion object {
        val SPEEDS = listOf(0.75f, 1f, 1.25f)
        const val DEFAULT_SPEED_INDEX = 1

        fun speedLabel(index: Int): String = when (SPEEDS[index]) {
            0.75f -> "0.75×"
            1.25f -> "1.25×"
            else -> "1×"
        }
    }
}
