package com.uansari.newswise.feature.headlines.display

import com.uansari.newswise.core.domain.model.NewsCategory

data class HeadlinesUiState(
    val selectedCategory: NewsCategory = NewsCategory.GENERAL,
    val categories: List<NewsCategory> = NewsCategory.entries
)

sealed class HeadlinesUiEvent {
    data class OnCategorySelected(val category: NewsCategory) : HeadlinesUiEvent()
    data class OnBookmarkClick(val url: String) : HeadlinesUiEvent()
    data class OnArticleClick(val url: String) : HeadlinesUiEvent()
}

sealed class HeadlinesUiEffect {
    data class NavigateToDetail(val url: String) : HeadlinesUiEffect()
}
