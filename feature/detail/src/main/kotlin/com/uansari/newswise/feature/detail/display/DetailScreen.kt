package com.uansari.newswise.feature.detail.display

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.uansari.newswise.feature.detail.component.DetailContent

@Composable
fun DetailScreen(
    onNavigateBack: () -> Unit, viewModel: DetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is DetailUiEffect.NavigateBack -> currentOnNavigateBack()
                is DetailUiEffect.ShareArticle -> {
                    val shareIntent = Intent.createChooser(
                        Intent().apply {
                            action = Intent.ACTION_SEND
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, effect.title)
                            putExtra(Intent.EXTRA_TEXT, "${effect.title}\n\n${effect.url}")
                        }, null
                    )
                    context.startActivity(shareIntent)

                }

                is DetailUiEffect.OpenInBrowser -> {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, effect.url.toUri())
                    )
                }
            }
        }
    }

    DetailContent(
        uiState = uiState, onEvent = viewModel::onEvent
    )
}