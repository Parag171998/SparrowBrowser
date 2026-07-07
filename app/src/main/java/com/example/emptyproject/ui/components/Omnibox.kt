package com.example.emptyproject.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.emptyproject.ui.BrowserIntent
import com.example.emptyproject.ui.theme.Dimens
import com.example.emptyproject.ui.theme.OmniboxBackground
import com.example.emptyproject.ui.theme.OmniboxBorder
import com.example.emptyproject.ui.theme.SparrowBrowserTheme

@Composable
fun Omnibox(
    text: String,
    isFocused: Boolean,
    favicon: Bitmap?,
    onIntent: (BrowserIntent) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search or type URL",
) {
    var fieldValue by remember { mutableStateOf(TextFieldValue(text)) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(text, isFocused) {
        if (!isFocused) {
            fieldValue = fieldValue.copy(text = text, selection = TextRange(text.length))
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.omniboxHeight)
            .background(OmniboxBackground, RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = OmniboxBorder,
                shape = RoundedCornerShape(24.dp),
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OmniboxLeadingIcon(
            favicon = favicon,
            url = text,
        )
        BasicTextField(
            value = fieldValue,
            onValueChange = { updated ->
                fieldValue = updated
                onIntent(BrowserIntent.OmniboxChanged(updated.text))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        onIntent(BrowserIntent.OmniboxFocused)
                        fieldValue = fieldValue.copy(
                            selection = TextRange(0, fieldValue.text.length),
                        )
                    } else {
                        onIntent(BrowserIntent.OmniboxBlurred)
                    }
                },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
            keyboardActions = KeyboardActions(
                onGo = {
                    val query = fieldValue.text.trim()
                    if (query.isNotEmpty()) {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onIntent(BrowserIntent.SearchSubmitted(query))
                    }
                },
            ),
            decorationBox = { innerTextField ->
                if (fieldValue.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    )
                }
                innerTextField()
            },
        )
    }
}

@Composable
private fun OmniboxLeadingIcon(
    favicon: Bitmap?,
    url: String,
) {
    when {
        favicon != null -> {
            Image(
                bitmap = favicon.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
        url.startsWith("https://") -> {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OmniboxEmptyPreview() {
    SparrowBrowserTheme {
        Omnibox(
            text = "",
            isFocused = false,
            favicon = null,
            onIntent = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OmniboxWithUrlPreview() {
    SparrowBrowserTheme {
        Omnibox(
            text = "https://en.wikipedia.org",
            isFocused = false,
            favicon = null,
            onIntent = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}
