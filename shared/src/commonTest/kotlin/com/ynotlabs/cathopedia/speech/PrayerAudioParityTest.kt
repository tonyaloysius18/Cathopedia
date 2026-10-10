package com.ynotlabs.cathopedia.speech

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * The recorded audio is looked up by the key of each part's text, so the app
 * and tools/audio/generate_prayer_audio.py must cut prayers into exactly the
 * same parts. The fixture holds real prayers and the keys Python computed.
 */
class PrayerAudioParityTest {
    @Test
    fun appCutsTheSamePartsAsTheAudioGenerator() {
        assertTrue(PrayerAudioParityCases.size > 20)
        for (case in PrayerAudioParityCases) {
            val sections = prayerSections(case.bodyMd)
            val keys = prayerSpeechScript(sections).map { speechKey(case.language, it.text) }
            assertEquals(case.keys, keys, "${case.language}: ${case.title}")
        }
    }

    @Test
    fun keyIsFnv1a64OfLanguageAndText() {
        // FNV-1a 64 of "en\nAmen." worked by hand in Python; pins the algorithm itself.
        assertEquals(PYTHON_AMEN_KEY, speechKey("en", "Amen."))
        assertTrue(speechKey("en", "Amen.") != speechKey("la", "Amen."))
    }
}

private const val PYTHON_AMEN_KEY = "9899057863f916d5"
