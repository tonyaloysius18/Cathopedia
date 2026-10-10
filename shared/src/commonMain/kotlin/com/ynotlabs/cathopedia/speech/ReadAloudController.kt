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

    /** Where reading has got to inside the current part, in characters: a pause resumes here. */
    var charOffset by mutableIntStateOf(0)
        private set

    private var unitStarts: List<Int> = emptyList()
    private var totalChars = 0

    /** How far through the whole prayer reading has got, 0..1, for the seek bar. */
    val progress: Float
        get() = if (totalChars == 0 || !hasStarted) 0f
        else ((unitStarts.getOrElse(index) { 0 } + charOffset).toFloat() / totalChars).coerceIn(0f, 1f)

    /** Bumped on every new utterance so a late "done" from an interrupted one is ignored. */
    private var generation = 0

    /** The unit to highlight, or null when nothing has been played. */
    val current: SpeechUnit? get() = if (hasStarted) units.getOrNull(index) else null

    fun load(units: List<SpeechUnit>, language: String) {
        if (units == this.units && language == this.language) return
        stopSpeaking()
        this.units = units
        this.language = language
        unitStarts = units.runningFold(0) { acc, unit -> acc + unit.text.length }.dropLast(1)
        totalChars = units.sumOf { it.text.length }
        index = 0
        charOffset = 0
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

    /**
     * Jumps to [fraction] (0..1) of the whole prayer, to the start of the word
     * there, and keeps reading from it if the voice was reading.
     */
    fun seekTo(fraction: Float) {
        if (units.isEmpty()) return
        val target = (fraction.coerceIn(0f, 1f) * totalChars).toInt()
        val part = unitStarts.indexOfLast { it <= target }.coerceIn(0, units.lastIndex)
        val text = units[part].text
        index = part
        charOffset = wordStart(text, (target - unitStarts[part]).coerceIn(0, text.length))
        hasStarted = true
        if (isPlaying) speakCurrent()
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
        charOffset = 0
        hasStarted = true
        if (isPlaying) speakCurrent()
    }

    /** Speaks the current part from [charOffset], backed up to the start of its word. */
    private fun speakCurrent() {
        val unit = units.getOrNull(index) ?: return
        val from = wordStart(unit.text, charOffset)
        if (from >= unit.text.length) return onUnitDone()
        charOffset = from
        val token = ++generation
        engine.speak(
            text = unit.text,
            language = language,
            rate = SPEEDS[speedIndex],
            startAt = from,
            onProgress = { if (token == generation) charOffset = it },
            onDone = { if (token == generation && isPlaying) onUnitDone() },
        )
    }

    private fun onUnitDone() {
        charOffset = 0
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
        /** Backs [offset] up to the start of the word it falls in, so speech never starts mid-word. */
        internal fun wordStart(text: String, offset: Int): Int {
            var i = offset.coerceIn(0, text.length)
            while (i > 0 && i < text.length && !text[i - 1].isWhitespace()) i--
            return i
        }

        val SPEEDS = listOf(0.75f, 1f, 1.25f)
        const val DEFAULT_SPEED_INDEX = 1

        fun speedLabel(index: Int): String = when (SPEEDS[index]) {
            0.75f -> "0.75×"
            1.25f -> "1.25×"
            else -> "1×"
        }
    }
}
