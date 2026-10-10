package com.ynotlabs.cathopedia.speech

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ReadAloudTest {
    private class FakeEngine : SpeechEngine {
        val spoken = mutableListOf<Pair<String, String>>()
        var pendingDone: (() -> Unit)? = null
        var pendingProgress: ((Int) -> Unit)? = null
        val starts = mutableListOf<Int>()
        override val isReady = true
        override fun hasVoice(language: String) = true
        override fun speak(
            text: String,
            language: String,
            rate: Float,
            startAt: Int,
            onProgress: (Int) -> Unit,
            onDone: () -> Unit,
        ) {
            spoken += text to language
            starts += startAt
            pendingDone = onDone
            pendingProgress = onProgress
        }
        override fun stop() = Unit
        override val canInstallVoices = false
        override fun openVoiceInstall() = Unit
        override fun release() = Unit
        fun finish() = pendingDone!!.invoke()
    }

    @Test
    fun scriptReadsOnlyThePrayerWithoutMarkdownOrVersicleMarks() {
        val script = prayerSpeechScript(
            listOf(null to "> V. The Angel of the Lord declared to Mary,\n> R. And she conceived.\n\n**Hail Mary**, full of grace"),
        )
        assertEquals(
            listOf(
                SpeechUnit("The Angel of the Lord declared to Mary,\nAnd she conceived.", 0, 0),
                SpeechUnit("Hail Mary, full of grace", 0, 1),
            ),
            script,
        )
    }

    @Test
    fun headingsAreReadAsTheirOwnPart() {
        val script = prayerSpeechScript(listOf(null to "Opening", "Day 1" to "Pray."))
        assertEquals(listOf(0 to 0, 1 to -1, 1 to 0), script.map { it.section to it.paragraph })
    }

    @Test
    fun playsEachPartInTurnAndStopsAtTheEnd() {
        val engine = FakeEngine()
        val reader = ReadAloudController(engine)
        reader.load(prayerSpeechScript(listOf(null to "One\n\nTwo\n\nThree")), "la")
        reader.play()
        engine.finish()
        engine.finish()
        assertTrue(reader.isPlaying)
        engine.finish()
        assertEquals(listOf("One", "Two", "Three"), engine.spoken.map { it.first })
        assertTrue(engine.spoken.all { it.second == "la" })
        assertFalse(reader.isPlaying)
        assertNull(reader.current)
    }

    @Test
    fun aLateDoneFromAnInterruptedPartIsIgnored() {
        val engine = FakeEngine()
        val reader = ReadAloudController(engine)
        reader.load(prayerSpeechScript(listOf(null to "One\n\nTwo\n\nThree")), "en")
        reader.play()
        val stale = engine.pendingDone!!
        reader.next()
        stale()
        assertEquals(1, reader.index)
        reader.pause()
        engine.finish()
        assertEquals(1, reader.index)
        assertFalse(reader.isPlaying)
    }

    @Test
    fun latinIsReadWithAnItalianVoice() {
        assertEquals("it-IT", SpeechVoices.localeFor("la"))
        assertEquals("pt-BR", SpeechVoices.localeFor("pt"))
        assertEquals("ta-IN", SpeechVoices.localeFor("ta"))
    }

    @Test
    fun pauseResumesFromTheWordWhereItStopped() {
        val engine = FakeEngine()
        val reader = ReadAloudController(engine)
        reader.load(prayerSpeechScript(listOf(null to "Hail Mary, full of grace")), "en")
        reader.play()
        engine.pendingProgress!!.invoke(13) // "full" starts at 11; the engine reported mid-word
        reader.pause()
        reader.play()
        assertEquals(listOf(0, 11), engine.starts)
        assertEquals(0, reader.index)
    }

    @Test
    fun seekingLandsOnTheRightPartAndWord() {
        val engine = FakeEngine()
        val reader = ReadAloudController(engine)
        // Parts "One two" (7 chars) and "Three four" (10 chars): 17 in all.
        reader.load(prayerSpeechScript(listOf(null to "One two\n\nThree four")), "en")
        reader.seekTo(14f / 17f) // inside "four"
        assertEquals(1, reader.index)
        assertEquals(6, reader.charOffset)
        reader.play()
        assertEquals(listOf(6), engine.starts)
        assertEquals(13f / 17f, reader.progress)
    }
}
