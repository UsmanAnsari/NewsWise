package com.uansari.newswise.feature.bookmarks.display

import com.uansari.newswise.core.domain.model.Article

data class BookmarkUiState(
    val articles: List<Article> = emptyList()
)

sealed class BookmarkUiEvent {
    data class OnArticleClick(val url: String) : BookmarkUiEvent()
    data class OnBookmarkClick(val url: String) : BookmarkUiEvent()
}

sealed class BookmarkUiEffect {
    data class NavigateToDetail(val url: String) : BookmarkUiEffect()
}