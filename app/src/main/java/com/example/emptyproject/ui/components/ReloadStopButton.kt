package com.example.emptyproject.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.Dimens

@Composable
fun ReloadStopButton(
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = { onIntent(BrowserIntent.Reload) },
        modifier = modifier.size(Dimens.toolbarIconButtonSize),
    ) {
        Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reload",
            modifier = Modifier.size(Dimens.toolbarIconSize),
        )
    }
}
