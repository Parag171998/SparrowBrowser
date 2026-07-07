package com.example.emptyproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.R
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.OmniboxBorder
import com.example.emptyproject.ui.theme.SparrowBrowserTheme
import com.example.emptyproject.ui.theme.ToolbarBackground

@Composable
fun BrowserBottomBar(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showAboutDialog by remember { mutableStateOf(false) }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(text = stringResource(R.string.menu_about)) },
            text = { Text(text = stringResource(R.string.menu_about_message)) },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(text = "OK")
                }
            },
        )
    }

    ColumnWithTopDivider(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ToolbarBackground)
                .navigationBarsPadding()
                .height(Dimens.toolbarHeight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onIntent(BrowserIntent.GoHome) }) {
                BrowserIcon(
                    resId = R.drawable.ic_home,
                    contentDescription = stringResource(R.string.cd_home),
                    modifier = Modifier.size(Dimens.bottomBarIconSize),
                )
            }
            IconButton(onClick = { onIntent(BrowserIntent.NewTab) }) {
                BrowserIcon(
                    resId = R.drawable.ic_new_tab,
                    contentDescription = stringResource(R.string.cd_new_tab),
                    modifier = Modifier.size(Dimens.bottomBarIconSize),
                )
            }
            IconButton(onClick = { onIntent(BrowserIntent.OpenTabSwitcher) }) {
                TabCountIcon(tabCount = state.tabs.size)
            }
            IconButton(onClick = { showAboutDialog = true }) {
                BrowserIcon(
                    resId = R.drawable.ic_menu,
                    contentDescription = stringResource(R.string.cd_menu),
                    modifier = Modifier.size(Dimens.bottomBarIconSize),
                )
            }
        }
    }
}

@Composable
private fun ColumnWithTopDivider(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = OmniboxBorder, thickness = 1.dp)
        content()
    }
}

@Composable
private fun TabCountIcon(tabCount: Int) {
    Box(
        modifier = Modifier.size(Dimens.bottomBarIconSize),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(Dimens.tabCountBadgeSize + 2.dp)
                .clip(RoundedCornerShape(Dimens.tabCountBadgeCornerRadius))
                .border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(Dimens.tabCountBadgeCornerRadius),
                )
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = tabCount.coerceAtMost(99).toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Preview(name = "Bottom Bar", showBackground = true)
@Composable
private fun BrowserBottomBarPreview() {
    SparrowBrowserTheme {
        BrowserBottomBar(
            state = BrowserPreviewData.home,
            onIntent = {},
        )
    }
}
