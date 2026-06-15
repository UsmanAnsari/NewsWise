package com.uansari.newswise.feature.search.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.usecase.SearchArticlesUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArticles: SearchArticlesUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchUiEffect>()
    val uiEffect: SharedFlow<SearchUiEffect> = _uiEffect.asSharedFlow()

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<Article>> =
        _uiState.map { it.query }.distinctUntilChanged().debounce(500L).flatMapLatest { query ->
            if (query.isBlank()) flowOf(PagingData.empty())
            else searchArticles(query)
        }.cachedIn(viewModelScope)

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChanged -> onQueryChanged(event.query)
            is SearchUiEvent.OnArticleClick -> onArticleClick(event.url)
            is SearchUiEvent.OnBookmarkClick -> onBookmarkClick(event.url)
            is SearchUiEvent.OnClearQuery -> onQueryChanged("")
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun onBookmarkClick(url: String) {
        viewModelScope.launch {
            toggleBookmark(url)
        }
    }

    private fun onArticleClick(url: String) {
        viewModelScope.launch {
            _uiEffect.emit(SearchUiEffect.NavigateToDetail(url))
        }
    }
}