package com.uansari.newswise.feature.search.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.uansari.newswise.feature.search.component.SearchContent

@Composable
fun SearchScreen(
    onArticleClick: (String) -> Unit, viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val currentOnArticleClick by rememberUpdatedState(onArticleClick)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is SearchUiEffect.NavigateToDetail -> currentOnArticleClick(effect.url)
            }
        }
    }

    SearchContent(
        uiState = uiState, searchResults = searchResults, onEvent = viewModel::onEvent
    )
}