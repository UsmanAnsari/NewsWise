package com.uansari.newswise.feature.search.display

data class SearchUiState(
    val query: String = ""
)

sealed class SearchUiEvent {
    data class OnQueryChanged(val query: String) : SearchUiEvent()
    data class OnArticleClick(val url: String) : SearchUiEvent()
    data class OnBookmarkClick(val url: String) : SearchUiEvent()
    data object OnClearQuery : SearchUiEvent()
}

sealed class SearchUiEffect {
    data class NavigateToDetail(val url: String) : SearchUiEffect()
}