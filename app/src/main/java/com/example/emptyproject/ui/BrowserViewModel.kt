package com.example.emptyproject.ui

import androidx.lifecycle.ViewModel
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab
import com.example.emptyproject.util.resolveInputToUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val initialTabId = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(
        BrowserUiState(
            tabs = listOf(Tab(id = initialTabId, isNewTab = true)),
            activeTabId = initialTabId,
            screen = Screen.NewTab,
        ),
    )
    val state: StateFlow<BrowserUiState> = _state.asStateFlow()

    fun onIntent(intent: BrowserIntent) {
        when (intent) {
            is BrowserIntent.SearchSubmitted -> handleSearchSubmitted(intent.query)
            is BrowserIntent.LoadUrlConsumed -> _state.update { it.copy(pendingLoadUrl = null) }
            is BrowserIntent.PageStarted -> handlePageStarted(intent.url)
            is BrowserIntent.PageFinished -> handlePageFinished(intent.url)
            is BrowserIntent.ProgressChanged -> handleProgressChanged(intent.progress)
            is BrowserIntent.TitleChanged -> handleTitleChanged(intent.title)
            is BrowserIntent.FaviconReceived -> _state.update { it.copy(favicon = intent.favicon) }
            is BrowserIntent.LoadFailed -> handleLoadFailed(intent.url)
            is BrowserIntent.NavStateChanged -> {
                _state.update {
                    it.copy(
                        canGoBack = intent.canGoBack,
                        canGoForward = intent.canGoForward,
                    )
                }
            }
            else -> Unit
        }
    }

    private fun handleSearchSubmitted(query: String) {
        val url = resolveInputToUrl(query)
        if (url.isBlank()) return

        updateActiveTab {
            it.copy(
                isNewTab = false,
                url = url,
                loadState = PageLoadState.Loading,
            )
        }
        _state.update {
            it.copy(
                screen = Screen.Browsing,
                omniboxText = url,
                loadState = PageLoadState.Loading,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
                pendingLoadUrl = url,
            )
        }
    }

    private fun handlePageStarted(url: String) {
        updateActiveTab {
            it.copy(url = url, loadState = PageLoadState.Loading)
        }
        _state.update {
            it.copy(
                omniboxText = url,
                loadState = PageLoadState.Loading,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
            )
        }
    }

    private fun handlePageFinished(url: String) {
        updateActiveTab {
            it.copy(url = url, loadState = PageLoadState.Loaded)
        }
        _state.update {
            it.copy(
                omniboxText = url,
                loadState = PageLoadState.Loaded,
                loadProgress = 100,
            )
        }
    }

    private fun handleProgressChanged(progress: Int) {
        _state.update {
            it.copy(
                loadProgress = progress,
                loadState = if (progress < 100) PageLoadState.Loading else it.loadState,
            )
        }
    }

    private fun handleTitleChanged(title: String) {
        if (title.isBlank()) return
        updateActiveTab { it.copy(title = title) }
    }

    private fun handleLoadFailed(url: String) {
        updateActiveTab {
            it.copy(url = url, loadState = PageLoadState.Error(url))
        }
        _state.update {
            it.copy(
                omniboxText = url,
                loadState = PageLoadState.Error(url),
                showError = true,
                errorUrl = url,
            )
        }
    }

    private fun updateActiveTab(transform: (Tab) -> Tab) {
        _state.update { current ->
            current.copy(
                tabs = current.tabs.map { tab ->
                    if (tab.id == current.activeTabId) transform(tab) else tab
                },
            )
        }
    }
}
