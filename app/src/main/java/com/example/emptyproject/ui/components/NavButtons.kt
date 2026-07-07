package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.DisabledIconAlpha

@Composable
fun NavButtons(
    canGoBack: Boolean,
    canGoForward: Boolean,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = { onIntent(BrowserIntent.GoBack) },
            enabled = canGoBack,
            modifier = Modifier.size(Dimens.toolbarIconButtonSize),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(Dimens.toolbarIconSize)
                    .alpha(if (canGoBack) 1f else DisabledIconAlpha),
            )
        }
        IconButton(
            onClick = { onIntent(BrowserIntent.GoForward) },
            enabled = canGoForward,
            modifier = Modifier.size(Dimens.toolbarIconButtonSize),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Forward",
                modifier = Modifier
                    .size(Dimens.toolbarIconSize)
                    .alpha(if (canGoForward) 1f else DisabledIconAlpha),
            )
        }
    }
}
