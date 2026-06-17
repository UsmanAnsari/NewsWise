package com.uansari.newswise.feature.detail.display

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.newswise.feature.detail.component.DetailContent

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit, viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DetailUiEffect.NavigateBack -> currentOnNavigateBack()
            }
        }
    }

    DetailContent(
        uiState = uiState, onEvent = viewModel::onEvent
    )
}