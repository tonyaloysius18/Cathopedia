package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GoldCardAccent(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFD6AE3D), // HubGold / MarksGold standard
    height: androidx.compose.ui.unit.Dp = 54.dp
) {
    Box(
        modifier = modifier
            .width(3.dp)
            .height(height)
            .clip(RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
            .background(color),
    )
}
