package com.ynotlabs.cathopedia.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ynotlabs.cathopedia.resources.Res
import com.ynotlabs.cathopedia.resources.back_arrow
import org.jetbrains.compose.resources.painterResource

@Composable
fun CathopediaBackButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(48.dp),
    ) {
        Image(
            painter = painterResource(Res.drawable.back_arrow),
            contentDescription = contentDescription,
            modifier = Modifier.size(38.dp)
        )
    }
}
