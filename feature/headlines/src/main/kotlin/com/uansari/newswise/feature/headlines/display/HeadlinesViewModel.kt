package com.uansari.newswise.feature.headlines.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.usecase.GetHeadlinesByCategoryUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HeadlinesViewModel @Inject constructor(
    private val getHeadlinesByCategory: GetHeadlinesByCategoryUseCase,
    private val toggleBookmark: ToggleBookmarkUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HeadlinesUiState())
    val uiState: StateFlow<HeadlinesUiState> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<HeadlinesUiEffect>()
    val uiEffect: SharedFlow<HeadlinesUiEffect> = _uiEffect.asSharedFlow()

    val headlines: Flow<PagingData<Article>> =
        _uiState.map { it.selectedCategory }.distinctUntilChanged()
            .flatMapLatest { category -> getHeadlinesByCategory(category) }.cachedIn(viewModelScope)

    fun onEvent(event: HeadlinesUiEvent) {
        when (event) {
            is HeadlinesUiEvent.OnCategorySelected -> onCategorySelected(event.category)
            is HeadlinesUiEvent.OnBookmarkClick -> onBookmarkClick(event.url)
            is HeadlinesUiEvent.OnArticleClick -> onArticleClick(event.url)
        }
    }

    private fun onCategorySelected(category: NewsCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun onBookmarkClick(url: String) {
        viewModelScope.launch {
            toggleBookmark(url)
        }
    }

    private fun onArticleClick(url: String) {
        viewModelScope.launch {
            _uiEffect.emit(HeadlinesUiEffect.NavigateToDetail(url))
        }
    }
}