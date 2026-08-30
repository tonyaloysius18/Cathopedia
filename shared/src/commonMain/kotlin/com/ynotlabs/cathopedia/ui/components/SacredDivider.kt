package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.cross_divider
import org.jetbrains.compose.resources.painterResource

@Composable
fun SacredDivider(
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(Res.drawable.cross_divider),
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth()
            .height(35.dp)
            .padding(horizontal = 8.dp),
        contentScale = ContentScale.FillWidth
    )
}
