package com.uansari.newswise.feature.headlines.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.uansari.newswise.feature.headlines.component.HeadlinesContent

@Composable
fun HeadlinesScreen(
    onArticleClick: (String) -> Unit, viewModel: HeadlinesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val articles = viewModel.headlines.collectAsLazyPagingItems()
    val currentOnArticleClick by rememberUpdatedState(onArticleClick)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HeadlinesUiEffect.NavigateToDetail -> currentOnArticleClick(effect.url)
            }
        }
    }

    HeadlinesContent(
        uiState = uiState, articles = articles, onEvent = viewModel::onEvent
    )
}

