package com.example.emptyproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Theme", showBackground = true)
@Composable
private fun SparrowBrowserThemePreview() {
    SparrowBrowserTheme {
        Text(
            text = "Sparrow Browser",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
