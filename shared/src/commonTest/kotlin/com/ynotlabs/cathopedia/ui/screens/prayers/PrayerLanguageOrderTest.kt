package com.ynotlabs.cathopedia.ui.screens.prayers

import kotlin.test.Test
import kotlin.test.assertEquals

class PrayerLanguageOrderTest {
    private val all = listOf("en", "la", "fr", "it", "es", "pt", "de", "nl", "pl", "ta")

    @Test
    fun appLanguageComesFirstThenEnglishAndLatinThenTheRest() {
        assertEquals(
            listOf("ta", "en", "la", "fr", "it", "es", "pt", "de", "nl", "pl"),
            orderReadingLanguages(all.shuffled(), "ta"),
        )
        assertEquals(
            listOf("en", "la", "fr", "it", "es", "pt", "de", "nl", "pl", "ta"),
            orderReadingLanguages(all, "en"),
        )
    }

    @Test
    fun onlyLanguagesThePrayerHasAreOffered() {
        assertEquals(listOf("de", "en", "fr"), orderReadingLanguages(listOf("fr", "en", "de"), "de"))
        assertEquals(listOf("en", "fr", "it"), orderReadingLanguages(listOf("it", "fr", "en"), "es"))
    }
}
