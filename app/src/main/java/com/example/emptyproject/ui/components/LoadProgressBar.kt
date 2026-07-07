package com.example.emptyproject.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.ProgressBlue
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun LoadProgressBar(
    loadState: PageLoadState,
    progress: Int,
    modifier: Modifier = Modifier,
) {
    if (loadState != PageLoadState.Loading) return

    val targetProgress = (progress / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(
            durationMillis = if (targetProgress >= 1f) 150 else 300,
            easing = FastOutSlowInEasing,
        ),
        label = "loadProgress",
    )

    LinearProgressIndicator(
        progress = { animatedProgress },
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.progressBarHeight),
        color = ProgressBlue,
        trackColor = ProgressBlue.copy(alpha = 0.2f),
        drawStopIndicator = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadProgressBarPreview() {
    SparrowBrowserTheme {
        LoadProgressBar(
            loadState = PageLoadState.Loading,
            progress = 45,
        )
    }
}
