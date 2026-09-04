package com.ynotlabs.cathopedia.content

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

const val HUB_ENTITY_LINK_TAG = "hub_entity_link"

fun parseHubMarkup(text: String): AnnotatedString = buildAnnotatedString { appendMarkup(text) }

private fun AnnotatedString.Builder.appendMarkup(text: String) {
    var i = 0
    val n = text.length
    while (i < n) {
        when {
            text.startsWith("[[", i) -> {
                val close = text.indexOf("]]", i + 2)
                val inner = if (close != -1) text.substring(i + 2, close) else null
                val pipe = inner?.indexOf('|') ?: -1
                val colon = if (pipe > 0) inner!!.indexOf(':') else -1
                if (inner != null && pipe > 0 && colon in 1 until pipe) {
                    val type = inner.substring(0, colon)
                    val id = inner.substring(colon + 1, pipe)
                    val label = inner.substring(pipe + 1)
                    if (type.isNotEmpty() && id.isNotEmpty() && label.isNotEmpty()) {
                        pushStringAnnotation(tag = HUB_ENTITY_LINK_TAG, annotation = "$type:$id")
                        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) {
                            appendMarkup(label)
                        }
                        pop()
                        i = close + 2
                        continue
                    }
                }
                // Malformed link — emit the opening marker literally and keep scanning from
                // just past it, so the rest of the string still gets parsed normally.
                append("[[")
                i += 2
            }

            text.startsWith("**", i) -> {
                val close = text.indexOf("**", i + 2)
                if (close == -1) {
                    append("**")
                    i += 2
                } else {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        appendMarkup(text.substring(i + 2, close))
                    }
                    i = close + 2
                }
            }

            text[i] == '*' -> {
                val close = text.indexOf("*", i + 1)
                if (close == -1) {
                    append("*")
                    i += 1
                } else {
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        appendMarkup(text.substring(i + 1, close))
                    }
                    i = close + 1
                }
            }

            else -> {
                append(text[i])
                i += 1
            }
        }
    }
}
