package com.uansari.newswise.feature.bookmarks.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.newswise.feature.bookmarks.component.BookmarkContent

@Composable
fun BookmarkScreen(
    onArticleClick: (String) -> Unit, viewModel: BookmarkViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnArticleClick by rememberUpdatedState(onArticleClick)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is BookmarkUiEffect.NavigateToDetail -> currentOnArticleClick(effect.url)
            }
        }
    }

    BookmarkContent(
        uiState = uiState, onEvent = viewModel::onEvent
    )
}