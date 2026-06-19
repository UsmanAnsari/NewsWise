package com.uansari.newswise.feature.detail.display

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uansari.newswise.core.domain.usecase.GetArticleByUrlUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.navigation.ArticleDetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getArticleByUrl: GetArticleByUrlUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase
) : ViewModel() {

    private val articleUrl: String = savedStateHandle.get<String>(ArticleDetailRoute.ARG_URL)
        ?: error("Missing navigation argument: ${ArticleDetailRoute.ARG_URL}")

    val uiState: StateFlow<DetailUiState> = getArticleByUrl(articleUrl).map { article ->
        if (article != null) {
            DetailUiState(article = article, isLoading = false)
        } else {
            DetailUiState(isLoading = false, hasError = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = DetailUiState()
    )

    private val _uiEffect = MutableSharedFlow<DetailUiEffect>()
    val uiEffect: SharedFlow<DetailUiEffect> = _uiEffect.asSharedFlow()

    fun onEvent(event: DetailUiEvent) {
        when (event) {
            is DetailUiEvent.OnBookmarkClick -> onBookmarkClick()
            is DetailUiEvent.OnBackClick -> onBackClick()
            is DetailUiEvent.OnShareClick -> onShareClick()
            is DetailUiEvent.OnViewSourceClick -> onViewSourceClick()
        }
    }

    private fun onBookmarkClick() {
        viewModelScope.launch {
            uiState.value.article?.let { toggleBookmark(it.url) }
        }
    }

    private fun onBackClick() {
        viewModelScope.launch {
            _uiEffect.emit(DetailUiEffect.NavigateBack)
        }
    }

    private fun onShareClick() {
        viewModelScope.launch {
            uiState.value.article?.let { article ->
                _uiEffect.emit(
                    DetailUiEffect.ShareArticle(
                        title = article.title, url = article.url
                    )
                )
            }
        }
    }

    private fun onViewSourceClick() {
        viewModelScope.launch {
            uiState.value.article?.let { article ->
                _uiEffect.emit(DetailUiEffect.OpenInBrowser(article.url))
            }
        }
    }
}