package com.ynotlabs.cathopedia.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.bible.LocalOpenBible
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.profile.Occasion
import com.ynotlabs.cathopedia.profile.ProfileGreetings
import com.ynotlabs.cathopedia.profile.UserProfile
import com.ynotlabs.cathopedia.profile.greetingMessage
import com.ynotlabs.cathopedia.profile.greetingTitle
import com.ynotlabs.cathopedia.profile.yearsLabel

/** Today's greeting from the user's profile: a title, a line, the years and a WEB verse. */
@Composable
internal fun ProfileGreetingCard(occasion: Occasion, profile: UserProfile, language: String) {
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    val openBible = LocalOpenBible.current
    var verse by remember(occasion) { mutableStateOf<Pair<String, String>?>(null) }
    LaunchedEffect(occasion) {
        verse = ProfileGreetings.verseText(occasion.verse) to ProfileGreetings.citation(occasion.verse)
    }
    val shape = RoundedCornerShape(20.dp)
    Column(
        Modifier.fillMaxWidth().clip(shape)
            .background(Brush.verticalGradient(listOf(colors.primary.copy(alpha = 0.16f), colors.surfaceContainerHigh)))
            .border(1.5.dp, colors.primary.copy(alpha = 0.55f), shape)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("✦", color = colors.primary, fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                t["screen.profile.home_label"].uppercase(),
                color = colors.primary,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            occasion.years?.let { years ->
                Text(
                    yearsLabel(years, language, t::get),
                    color = colors.onPrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(colors.primary)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            greetingTitle(occasion.kind, profile, t::get),
            color = colors.onBackground,
            fontFamily = FontFamily.Serif,
            fontSize = 24.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            greetingMessage(occasion.kind, profile, t::get),
            color = colors.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        verse?.let { (text, citation) ->
            Spacer(Modifier.height(12.dp))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
                    .background(colors.background.copy(alpha = 0.55f))
                    .clickable(enabled = openBible != null) { openBible?.invoke(occasion.verse.bibleRef) }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
            ) {
                // No added quotation marks: some WEB verses already open with one.
                Text(
                    text,
                    color = colors.onBackground,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                )
                Spacer(Modifier.height(6.dp))
                Text(citation, color = colors.primary, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
