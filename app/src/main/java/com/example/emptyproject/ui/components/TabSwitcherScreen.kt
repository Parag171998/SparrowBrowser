package com.example.emptyproject.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.model.Tab
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.BrowserUiState
import com.example.emptyproject.ui.preview.BrowserPreviewData
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.SparrowBrowserTheme
import com.example.emptyproject.ui.theme.TabCardSelectedBorder
import com.example.emptyproject.util.extractDomain

@Composable
fun TabSwitcherScreen(
    state: BrowserUiState,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.97f)),
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.tabs, key = { it.id }) { tab ->
                TabCard(
                    tab = tab,
                    isSelected = tab.id == state.activeTabId,
                    onSelect = { onIntent(BrowserIntent.SwitchTab(tab.id)) },
                    onClose = { onIntent(BrowserIntent.CloseTab(tab.id)) },
                )
            }
        }

        FloatingActionButton(
            onClick = { onIntent(BrowserIntent.NewTab) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New tab",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@Composable
fun TabCard(
    tab: Tab,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(Dimens.tabCardCornerRadius)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, TabCardSelectedBorder, shape)
                } else {
                    Modifier
                },
            )
            .clickable(onClick = onSelect),
        shape = shape,
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.tabCardElevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TabFavicon(favicon = tab.favicon)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = extractDomain(tab.url).ifBlank { "New tab" },
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close tab",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.75f)
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (tab.thumbnail != null) {
                    Image(
                        bitmap = tab.thumbnail.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Text(
                        text = tab.title.ifBlank { tab.url.ifBlank { "New tab" } },
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
            Text(
                text = tab.title.ifBlank { extractDomain(tab.url) },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun TabFavicon(favicon: Bitmap?) {
    if (favicon != null) {
        Image(
            bitmap = favicon.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(2.dp)),
        )
    } else {
        Icon(
            imageVector = Icons.Default.Language,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

@Preview(name = "Tab Switcher", showBackground = true)
@Composable
private fun TabSwitcherScreenPreview() {
    SparrowBrowserTheme {
        TabSwitcherScreen(
            state = BrowserPreviewData.tabSwitcher,
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TabCardPreview() {
    SparrowBrowserTheme {
        TabCard(
            tab = BrowserPreviewData.browsing.tabs.first(),
            isSelected = true,
            onSelect = {},
            onClose = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
