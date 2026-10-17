package com.ynotlabs.cathopedia.ui.screens.holymass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.i18n.LocalScreenText
import com.ynotlabs.cathopedia.liturgical.MassPosture
import com.ynotlabs.cathopedia.liturgical.OrdoProfile
import com.ynotlabs.cathopedia.liturgical.massPostureCues
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.mass_posture_standing
import com.ynotlabs.cathopedia.resources.mass_posture_sitting
import com.ynotlabs.cathopedia.resources.mass_posture_kneeling
import com.ynotlabs.cathopedia.resources.mass_posture_bowing
import com.ynotlabs.cathopedia.resources.mass_posture_genuflect
import com.ynotlabs.cathopedia.resources.mass_posture_sign_cross
import org.jetbrains.compose.resources.painterResource

/** Small cues remain separate from the speaker label and wrap at larger text sizes. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun MassPostureBadges(
    partId: String,
    profile: OrdoProfile,
    celebrationKey: String,
    previousPartId: String? = null,
) {
    val cues = massPostureCues(partId, profile, celebrationKey).filter { cue ->
        previousPartId == null || cue !in massPostureCues(previousPartId, profile, celebrationKey)
    }
    if (cues.isEmpty()) return
    val t = LocalScreenText.current
    val colors = MaterialTheme.colorScheme
    Column {
        Spacer(Modifier.height(10.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            cues.forEach { cue ->
                val image = when (cue.posture) {
                    MassPosture.STAND -> Res.drawable.mass_posture_standing
                    MassPosture.SIT -> Res.drawable.mass_posture_sitting
                    MassPosture.KNEEL -> Res.drawable.mass_posture_kneeling
                    MassPosture.BOW -> Res.drawable.mass_posture_bowing
                    MassPosture.GENUFLECT -> Res.drawable.mass_posture_genuflect
                    MassPosture.SIGN_OF_CROSS -> Res.drawable.mass_posture_sign_cross
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clip(RoundedCornerShape(22.dp)).background(colors.surfaceContainerHigh)
                            .border(1.dp, colors.primary.copy(alpha = 0.85f), RoundedCornerShape(22.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) {
                        Image(painterResource(image), contentDescription = null,
                            colorFilter = ColorFilter.tint(colors.primary), modifier = Modifier.size(30.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(t["screen.mass.postures.${cue.posture.key}.name"], color = colors.onSurface,
                            fontSize = 13.sp, lineHeight = 18.sp)
                    }
                    cue.detailKey?.let { key ->
                        Text(t[key], color = colors.onSurfaceVariant, fontSize = 12.sp, lineHeight = 17.sp,
                            modifier = Modifier.padding(top = 5.dp, bottom = 2.dp))
                    }
                }
            }
        }
    }
}
