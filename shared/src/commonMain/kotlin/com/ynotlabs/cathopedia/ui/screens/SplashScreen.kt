package com.ynotlabs.cathopedia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import com.ynotlabs.cathopedia.data.CathopediaRepository

/**
 * Opens the bundled database, loads the compiled content bundle on first run
 * (see ContentLoader), and checks the onboarding flag. [onReady] fires once
 * with whether this is the very first launch.
 */
@Composable
fun SplashScreen(isFirstRun: Boolean, repository: CathopediaRepository, onReady: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        repository.ensureContentLoaded()
        onReady(isFirstRun)
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "✝ Cathopedia",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Loading",
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
