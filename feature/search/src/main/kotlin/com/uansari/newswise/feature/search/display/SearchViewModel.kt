package com.uansari.newswise.feature.search.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.usecase.GetBookmarksUseCase
import com.uansari.newswise.core.domain.usecase.SearchArticlesUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.domain.usecase.UpsertArticleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchArticles: SearchArticlesUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase,
    private val upsertArticle: UpsertArticleUseCase,
    getBookmarks: GetBookmarksUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<SearchUiEffect>()
    val uiEffect: SharedFlow<SearchUiEffect> = _uiEffect.asSharedFlow()

    val bookmarkedUrls: StateFlow<Set<String>> =
        getBookmarks().map { articles -> articles.map { it.url }.toSet() }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = emptySet()
        )

    @OptIn(FlowPreview::class)
    val searchResults: Flow<PagingData<Article>> =
        _uiState.map { it.query }.distinctUntilChanged().debounce(500L).flatMapLatest { query ->
            if (query.isBlank()) flowOf(PagingData.empty())
            else searchArticles(query)
        }.cachedIn(viewModelScope)

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChanged -> onQueryChanged(event.query)
            is SearchUiEvent.OnArticleClick -> onArticleClick(event.article)
            is SearchUiEvent.OnBookmarkClick -> onBookmarkClick(event.article)
            is SearchUiEvent.OnClearQuery -> onQueryChanged("")
        }
    }

    private fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    private fun onArticleClick(article: Article) {
        viewModelScope.launch {
            upsertArticle(article)
            _uiEffect.emit(SearchUiEffect.NavigateToDetail(article.url))
        }
    }

    private fun onBookmarkClick(article: Article) {
        viewModelScope.launch {
            upsertArticle(article)
            toggleBookmark(article.url)
        }
    }
}