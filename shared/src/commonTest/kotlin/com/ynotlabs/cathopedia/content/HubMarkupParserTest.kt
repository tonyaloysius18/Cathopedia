package com.ynotlabs.cathopedia.content

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HubMarkupParserTest {

    @Test
    fun plainTextPassesThrough() {
        val result = parseHubMarkup("no markup here")
        assertEquals("no markup here", result.text)
        assertTrue(result.spanStyles.isEmpty())
    }

    @Test
    fun boldAndItalicProduceTheExpectedSpans() {
        val bold = parseHubMarkup("**Vatican City**")
        assertEquals("Vatican City", bold.text)
        assertTrue(bold.spanStyles.any { it.item.fontWeight == FontWeight.Bold && it.start == 0 && it.end == bold.text.length })

        val italic = parseHubMarkup("*sede vacante*")
        assertEquals("sede vacante", italic.text)
        assertTrue(italic.spanStyles.any { it.item.fontStyle == FontStyle.Italic })
    }

    @Test
    fun nestedItalicInsideBoldComposesBothStyles() {
        val result = parseHubMarkup("**bold *and italic* text**")
        assertEquals("bold and italic text", result.text)

        // The "and italic" run should carry both an enclosing bold span and its own italic span.
        val italicStart = result.text.indexOf("and italic")
        val italicEnd = italicStart + "and italic".length
        assertTrue(result.spanStyles.any { it.item.fontWeight == FontWeight.Bold && it.start <= italicStart && it.end >= italicEnd })
        assertTrue(result.spanStyles.any { it.item.fontStyle == FontStyle.Italic && it.start == italicStart && it.end == italicEnd })
    }

    @Test
    fun entityLinkProducesAClickableAnnotationOverTheLabelOnly() {
        val result = parseHubMarkup("See [[pope:pius_ix|Pius IX]] for more.")
        assertEquals("See Pius IX for more.", result.text)

        val labelStart = result.text.indexOf("Pius IX")
        val labelEnd = labelStart + "Pius IX".length
        val annotations = result.getStringAnnotations(HUB_ENTITY_LINK_TAG, 0, result.text.length)
        assertEquals(1, annotations.size)
        assertEquals("pope:pius_ix", annotations.single().item)
        assertEquals(labelStart, annotations.single().start)
        assertEquals(labelEnd, annotations.single().end)
    }

    @Test
    fun entityLinkInsideBoldKeepsBothTheStyleAndTheAnnotation() {
        val result = parseHubMarkup("**[[pope:pius_ix|Pius IX]]**")
        assertEquals("Pius IX", result.text)
        assertTrue(result.spanStyles.any { it.item.fontWeight == FontWeight.Bold })
        assertEquals(1, result.getStringAnnotations(HUB_ENTITY_LINK_TAG, 0, result.text.length).size)
    }

    @Test
    fun unterminatedBoldMarkerIsEmittedLiterally() {
        val result = parseHubMarkup("this **never closes")
        assertEquals("this **never closes", result.text)
        assertTrue(result.spanStyles.none { it.item.fontWeight == FontWeight.Bold })
    }

    @Test
    fun unterminatedItalicMarkerIsEmittedLiterally() {
        val result = parseHubMarkup("a single * with no partner")
        assertEquals("a single * with no partner", result.text)
        assertTrue(result.spanStyles.none { it.item.fontStyle == FontStyle.Italic })
    }

    @Test
    fun malformedLinkMissingLabelIsEmittedLiterally() {
        val result = parseHubMarkup("[[pope:pius_ix]] has no label")
        assertEquals("[[pope:pius_ix]] has no label", result.text)
        assertTrue(result.getStringAnnotations(HUB_ENTITY_LINK_TAG, 0, result.text.length).isEmpty())
    }

    @Test
    fun malformedLinkMissingTypeColonIsEmittedLiterally() {
        val result = parseHubMarkup("[[pius_ix|Pius IX]] no type prefix")
        assertEquals("[[pius_ix|Pius IX]] no type prefix", result.text)
        assertTrue(result.getStringAnnotations(HUB_ENTITY_LINK_TAG, 0, result.text.length).isEmpty())
    }

    @Test
    fun unclosedLinkBracketIsEmittedLiterally() {
        val result = parseHubMarkup("[[pope:pius_ix|Pius IX never closes")
        assertEquals("[[pope:pius_ix|Pius IX never closes", result.text)
        assertTrue(result.getStringAnnotations(HUB_ENTITY_LINK_TAG, 0, result.text.length).isEmpty())
    }

    @Test
    fun emptyStringDoesNotThrow() {
        assertEquals("", parseHubMarkup("").text)
    }

    @Test
    fun consecutiveMarkersDoNotCrashTheParser() {
        val result = parseHubMarkup("****[[]]***")
        // No well-formed spans possible here; the important thing is it never throws.
        assertTrue(result.text.isNotEmpty() || result.text.isEmpty())
    }
}
