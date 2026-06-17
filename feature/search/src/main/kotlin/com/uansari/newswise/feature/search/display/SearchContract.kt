package com.uansari.newswise.feature.search.display

import com.uansari.newswise.core.domain.model.Article

data class SearchUiState(
    val query: String = ""
)

sealed class SearchUiEvent {
    data class OnQueryChanged(val query: String) : SearchUiEvent()
    data class OnArticleClick(val article: Article) : SearchUiEvent()
    data class OnBookmarkClick(val article: Article) : SearchUiEvent()
    data object OnClearQuery : SearchUiEvent()
}

sealed class SearchUiEffect {
    data class NavigateToDetail(val url: String) : SearchUiEffect()
}