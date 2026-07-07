package com.example.emptyproject.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emptyproject.model.PageLoadState
import com.example.emptyproject.model.Tab
import com.example.emptyproject.util.resolveInputToUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val initialTabId = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(
        BrowserUiState(
            tabs = listOf(
                Tab(
                    id = initialTabId,
                    url = DEFAULT_HOME_URL,
                    loadState = PageLoadState.Loading,
                    isNewTab = false,
                ),
            ),
            activeTabId = initialTabId,
            screen = Screen.Browsing,
            omniboxText = DEFAULT_HOME_URL,
            loadState = PageLoadState.Loading,
            pendingLoadUrl = DEFAULT_HOME_URL,
        ),
    )
    val state: StateFlow<BrowserUiState> = _state.asStateFlow()

    private val _webViewCommands = MutableSharedFlow<WebViewCommand>(extraBufferCapacity = 1)
    val webViewCommands: SharedFlow<WebViewCommand> = _webViewCommands.asSharedFlow()

    fun onIntent(intent: BrowserIntent) {
        when (intent) {
            is BrowserIntent.SearchSubmitted -> handleSearchSubmitted(intent.query)
            is BrowserIntent.OmniboxChanged -> {
                _state.update { it.copy(omniboxText = intent.text) }
            }
            is BrowserIntent.OmniboxFocused -> {
                _state.update { it.copy(isOmniboxFocused = true) }
            }
            is BrowserIntent.OmniboxBlurred -> {
                _state.update { it.copy(isOmniboxFocused = false) }
            }
            is BrowserIntent.GoHome -> handleGoHome()
            is BrowserIntent.GoBack -> emitWebViewCommand(WebViewCommand.GoBack)
            is BrowserIntent.GoForward -> emitWebViewCommand(WebViewCommand.GoForward)
            is BrowserIntent.Reload -> emitWebViewCommand(WebViewCommand.Reload)
            is BrowserIntent.StopLoading -> handleStopLoading()
            is BrowserIntent.OpenTabSwitcher -> {
                _state.update { it.copy(screen = Screen.TabSwitcher) }
            }
            is BrowserIntent.CloseTabSwitcher -> {
                _state.update { current ->
                    if (current.screen == Screen.TabSwitcher) {
                        current.copy(screen = Screen.Browsing)
                    } else {
                        current
                    }
                }
            }
            is BrowserIntent.NewTab -> Unit // Phase 6
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

    fun handleSystemBack(): Boolean {
        val current = _state.value
        return when {
            current.screen == Screen.TabSwitcher -> {
                onIntent(BrowserIntent.CloseTabSwitcher)
                true
            }
            current.screen == Screen.Browsing && current.canGoBack -> {
                onIntent(BrowserIntent.GoBack)
                true
            }
            else -> false
        }
    }

    private fun handleGoHome() {
        updateActiveTab {
            it.copy(
                url = DEFAULT_HOME_URL,
                loadState = PageLoadState.Loading,
                isNewTab = false,
            )
        }
        _state.update {
            it.copy(
                screen = Screen.Browsing,
                omniboxText = DEFAULT_HOME_URL,
                favicon = null,
                isOmniboxFocused = false,
                pendingLoadUrl = DEFAULT_HOME_URL,
                loadState = PageLoadState.Loading,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
            )
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
                isOmniboxFocused = false,
            )
        }
    }

    private fun handlePageStarted(url: String) {
        updateActiveTab {
            it.copy(url = url, loadState = PageLoadState.Loading, isNewTab = false)
        }
        _state.update {
            it.copy(
                omniboxText = url,
                loadState = PageLoadState.Loading,
                loadProgress = 0,
                showError = false,
                errorUrl = null,
                isOmniboxFocused = false,
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
                isOmniboxFocused = false,
            )
        }
    }

    private fun handleStopLoading() {
        emitWebViewCommand(WebViewCommand.StopLoading)
        updateActiveTab { tab ->
            if (tab.loadState is PageLoadState.Loading) {
                tab.copy(loadState = PageLoadState.Loaded)
            } else {
                tab
            }
        }
        _state.update {
            it.copy(
                loadState = PageLoadState.Loaded,
                loadProgress = 0,
            )
        }
    }

    private fun handleProgressChanged(progress: Int) {
        _state.update {
            it.copy(
                loadProgress = progress,
                loadState = when {
                    progress >= 100 -> PageLoadState.Loaded
                    progress > 0 -> PageLoadState.Loading
                    else -> it.loadState
                },
            )
        }
        if (progress >= 100) {
            updateActiveTab { tab ->
                if (tab.loadState is PageLoadState.Loading) {
                    tab.copy(loadState = PageLoadState.Loaded)
                } else {
                    tab
                }
            }
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
                isOmniboxFocused = false,
            )
        }
    }

    private fun emitWebViewCommand(command: WebViewCommand) {
        viewModelScope.launch {
            _webViewCommands.emit(command)
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

    companion object {
        private const val DEFAULT_HOME_URL = "https://www.google.com"
    }
}
