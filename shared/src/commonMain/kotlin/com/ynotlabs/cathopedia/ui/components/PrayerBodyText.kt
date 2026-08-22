package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.ui.theme.CathopediaTypography

/**
 * Renders prayer_text.body_md per content/README.md's markdown convention:
 * blank lines separate stanzas, and a `> ` prefix marks a versicle/response
 * line, rendered in italics. Not a general markdown engine — prayer bodies
 * don't use bold/links/headings, so a full parser would be unused surface.
 */
@Composable
fun PrayerBodyText(
    bodyMd: String,
    color: Color,
    fontScale: Float = 1f,
    modifier: Modifier = Modifier,
) {
    val paragraphs = remember(bodyMd) { bodyMd.trim().split(Regex("\n\\s*\n")).filter { it.isNotBlank() } }
    val bodySize = (CathopediaTypography.bodyLarge.fontSize.value * fontScale).sp
    val lineHeight = (CathopediaTypography.bodyLarge.lineHeight.value * fontScale).sp

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(18.dp)) {
        paragraphs.forEach { paragraph ->
            val lines = paragraph.lines()
            val isVersicle = lines.all { it.isBlank() || it.trimStart().startsWith(">") }
            val text = lines.joinToString("\n") { it.trimStart().removePrefix(">").trim() }

            Text(
                text = text,
                fontSize = bodySize,
                lineHeight = lineHeight,
                fontStyle = if (isVersicle) FontStyle.Italic else FontStyle.Normal,
                color = if (isVersicle) color.copy(alpha = 0.82f) else color,
                textAlign = if (isVersicle) TextAlign.Center else TextAlign.Start,
            )
        }
    }
}
