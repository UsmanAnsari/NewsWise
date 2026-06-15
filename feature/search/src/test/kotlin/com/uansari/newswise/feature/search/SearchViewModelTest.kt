package com.uansari.newswise.feature.search

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.usecase.SearchArticlesUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.testing.fake.FakeArticleRepository
import com.uansari.newswise.core.testing.rule.TestDispatcherRule
import com.uansari.newswise.feature.search.display.SearchUiEffect
import com.uansari.newswise.feature.search.display.SearchUiEvent
import com.uansari.newswise.feature.search.display.SearchViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var fakeRepository: FakeArticleRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeArticleRepository()
        viewModel = SearchViewModel(
            searchArticles = SearchArticlesUseCase(fakeRepository),
            toggleBookmark = ToggleBookmarkUseCase(fakeRepository)
        )
    }

    @Test
    fun `initial state has empty query`() {
        assertThat(viewModel.uiState.value.query).isEmpty()
    }

    @Test
    fun `OnQueryChanged event updates query`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().query).isEmpty()

            viewModel.onEvent(SearchUiEvent.OnQueryChanged("kotlin"))
            assertThat(awaitItem().query).isEqualTo("kotlin")

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnClearQuery event resets query to empty`() = runTest {
        viewModel.onEvent(SearchUiEvent.OnQueryChanged("kotlin"))
        viewModel.onEvent(SearchUiEvent.OnClearQuery)

        assertThat(viewModel.uiState.value.query).isEmpty()
    }

    @Test
    fun `OnClearQuery is equivalent to OnQueryChanged with empty string`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().query).isEmpty()

            viewModel.onEvent(SearchUiEvent.OnQueryChanged("android"))
            assertThat(awaitItem().query).isEqualTo("android")

            viewModel.onEvent(SearchUiEvent.OnClearQuery)
            assertThat(awaitItem().query).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnArticleClick event emits NavigateToDetail effect`() = runTest {
        val url = "https://example.com/article"

        viewModel.uiEffect.test {
            viewModel.onEvent(SearchUiEvent.OnArticleClick(url))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(SearchUiEffect.NavigateToDetail::class.java)
            assertThat((effect as SearchUiEffect.NavigateToDetail).url).isEqualTo(url)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBookmarkClick event adds article to bookmarks`() = runTest {
        val article = ArticleFactory.makeArticle()
        fakeRepository.setArticles(listOf(article))

        viewModel.onEvent(SearchUiEvent.OnBookmarkClick(article.url))

        val bookmarks = fakeRepository.getBookmarks().first()
        assertThat(bookmarks).hasSize(1)
        assertThat(bookmarks.first().url).isEqualTo(article.url)
    }

    @Test
    fun `OnBookmarkClick event removes article when already bookmarked`() = runTest {
        val article = ArticleFactory.makeArticle(isBookmarked = true)
        fakeRepository.setArticles(listOf(article))
        fakeRepository.setBookmarks(listOf(article))

        viewModel.onEvent(SearchUiEvent.OnBookmarkClick(article.url))

        assertThat(fakeRepository.getBookmarks().first()).isEmpty()
    }

    @Test
    fun `multiple query changes emit in order`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().query).isEmpty()

            listOf("a", "an", "android").forEach { query ->
                viewModel.onEvent(SearchUiEvent.OnQueryChanged(query))
                assertThat(awaitItem().query).isEqualTo(query)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}