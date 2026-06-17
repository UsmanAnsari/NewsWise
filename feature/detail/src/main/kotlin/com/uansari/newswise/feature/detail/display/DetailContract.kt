package com.uansari.newswise.feature.detail.display

import com.uansari.newswise.core.domain.model.Article

data class DetailUiState(
    val article: Article? = null, val isLoading: Boolean = true, val hasError: Boolean = false,
)

sealed class DetailUiEvent {
    data object OnBookmarkClick : DetailUiEvent()
    data object OnBackClick : DetailUiEvent()
}

sealed class DetailUiEffect {
    data object NavigateBack : DetailUiEffect()
}