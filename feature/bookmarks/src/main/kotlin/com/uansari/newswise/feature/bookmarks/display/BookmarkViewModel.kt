package com.uansari.newswise.feature.bookmarks.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.newswise.core.domain.usecase.GetBookmarksUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    getBookmarks: GetBookmarksUseCase, private val toggleBookmark: ToggleBookmarkUseCase
) : ViewModel() {

    val uiState: StateFlow<BookmarkUiState> =
        getBookmarks().map { articles -> BookmarkUiState(articles = articles) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = BookmarkUiState()
        )

    private val _uiEffect = MutableSharedFlow<BookmarkUiEffect>()
    val uiEffect: SharedFlow<BookmarkUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: BookmarkUiEvent) {
        when (event) {
            is BookmarkUiEvent.OnArticleClick -> onArticleClick(event.url)
            is BookmarkUiEvent.OnBookmarkClick -> onBookmarkClick(event.url)
        }
    }

    private fun onArticleClick(url: String) {
        viewModelScope.launch {
            _uiEffect.emit(BookmarkUiEffect.NavigateToDetail(url))
        }
    }

    private fun onBookmarkClick(url: String) {
        viewModelScope.launch {
            toggleBookmark(url)
        }
    }
}