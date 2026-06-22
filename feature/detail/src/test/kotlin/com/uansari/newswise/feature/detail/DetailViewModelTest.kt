package com.uansari.newswise.feature.detail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.TurbineTestContext
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.usecase.GetArticleByUrlUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.navigation.ArticleDetailRoute
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.testing.fake.FakeArticleRepository
import com.uansari.newswise.core.testing.rule.TestDispatcherRule
import com.uansari.newswise.feature.detail.display.DetailUiEffect
import com.uansari.newswise.feature.detail.display.DetailUiEvent
import com.uansari.newswise.feature.detail.display.DetailUiState
import com.uansari.newswise.feature.detail.display.DetailViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class DetailViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var fakeRepository: FakeArticleRepository
    private lateinit var viewModel: DetailViewModel

    private val testUrl = "https://example.com/article"

    @Before
    fun setUp() {
        fakeRepository = FakeArticleRepository()
        viewModel = DetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(ArticleDetailRoute.ARG_URL to testUrl)),
            getArticleByUrl = GetArticleByUrlUseCase(fakeRepository),
            toggleBookmark = ToggleBookmarkUseCase(fakeRepository)
        )
    }

    @Test
    fun `initial state is loading with no article`() {
        assertThat(viewModel.uiState.value.isLoading).isTrue()
        assertThat(viewModel.uiState.value.article).isNull()
        assertThat(viewModel.uiState.value.hasError).isFalse()
    }

    @Test
    fun `state loads article when present in repository`() = runTest {
        val article = ArticleFactory.makeArticle(url = testUrl)
        fakeRepository.setArticles(listOf(article))

        viewModel.uiState.test {
            val state = awaitNonLoading()
            assertThat(state.isLoading).isFalse()
            assertThat(state.hasError).isFalse()
            assertThat(state.article).isNotNull()
            assertThat(state.article?.url).isEqualTo(testUrl)
            assertThat(state.article?.title).isEqualTo(article.title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state shows error when article not in repository`() = runTest {
        viewModel.uiState.test {
            val state = awaitNonLoading()
            assertThat(state.isLoading).isFalse()
            assertThat(state.hasError).isTrue()
            assertThat(state.article).isNull()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBookmarkClick event toggles bookmark reactively`() = runTest {
        val article = ArticleFactory.makeArticle(url = testUrl, isBookmarked = false)
        fakeRepository.setArticles(listOf(article))

        viewModel.uiState.test {
            val loaded = awaitNonLoading()
            assertThat(loaded.article?.isBookmarked).isFalse()

            viewModel.onEvent(DetailUiEvent.OnBookmarkClick)

            val bookmarked = awaitItem()
            assertThat(bookmarked.article?.isBookmarked).isTrue()

            viewModel.onEvent(DetailUiEvent.OnBookmarkClick)

            val unbookmarked = awaitItem()
            assertThat(unbookmarked.article?.isBookmarked).isFalse()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBackClick event emits NavigateBack effect`() = runTest {
        viewModel.uiEffect.test {
            viewModel.onEvent(DetailUiEvent.OnBackClick)
            assertThat(awaitItem()).isInstanceOf(DetailUiEffect.NavigateBack::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBookmarkClick does nothing when article is not yet loaded`() = runTest {
        viewModel.onEvent(DetailUiEvent.OnBookmarkClick)
        assertThat(viewModel.uiState.value.article).isNull()
    }

    private suspend fun TurbineTestContext<DetailUiState>.awaitNonLoading(): DetailUiState {
        var state: DetailUiState
        do {
            state = awaitItem()
        } while (state.isLoading)
        return state
    }
}