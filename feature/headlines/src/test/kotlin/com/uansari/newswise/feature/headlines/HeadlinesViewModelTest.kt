package com.uansari.newswise.feature.headlines

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.usecase.GetHeadlinesByCategoryUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.testing.fake.FakeArticleRepository
import com.uansari.newswise.core.testing.rule.TestDispatcherRule
import com.uansari.newswise.feature.headlines.display.HeadlinesUiEffect
import com.uansari.newswise.feature.headlines.display.HeadlinesUiEvent
import com.uansari.newswise.feature.headlines.display.HeadlinesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HeadlinesViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()

    private lateinit var fakeRepository: FakeArticleRepository
    private lateinit var viewModel: HeadlinesViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeArticleRepository()
        viewModel = HeadlinesViewModel(
            getHeadlinesByCategory = GetHeadlinesByCategoryUseCase(fakeRepository),
            toggleBookmark = ToggleBookmarkUseCase(fakeRepository)
        )
    }

    @Test
    fun `initial state has GENERAL category selected`() {
        assertThat(viewModel.uiState.value.selectedCategory).isEqualTo(NewsCategory.GENERAL)
    }

    @Test
    fun `initial state contains all categories`() {
        assertThat(viewModel.uiState.value.categories).containsExactlyElementsIn(NewsCategory.entries)
            .inOrder()
    }

    @Test
    fun `OnCategorySelected event updates selectedCategory`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().selectedCategory).isEqualTo(NewsCategory.GENERAL)

            viewModel.onEvent(HeadlinesUiEvent.OnCategorySelected(NewsCategory.TECHNOLOGY))
            assertThat(awaitItem().selectedCategory).isEqualTo(NewsCategory.TECHNOLOGY)

            viewModel.onEvent(HeadlinesUiEvent.OnCategorySelected(NewsCategory.BUSINESS))
            assertThat(awaitItem().selectedCategory).isEqualTo(NewsCategory.BUSINESS)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBookmarkClick event adds article to bookmarks`() = runTest {
        val article = ArticleFactory.makeArticle(category = NewsCategory.GENERAL.value)
        fakeRepository.setArticles(listOf(article))

        viewModel.onEvent(HeadlinesUiEvent.OnBookmarkClick(article.url))

        val bookmarks = fakeRepository.getBookmarks().first()
        assertThat(bookmarks).hasSize(1)
        assertThat(bookmarks.first().url).isEqualTo(article.url)
    }

    @Test
    fun `OnBookmarkClick event removes article when already bookmarked`() = runTest {
        val article = ArticleFactory.makeArticle(
            category = NewsCategory.GENERAL.value, isBookmarked = true
        )
        fakeRepository.setArticles(listOf(article))
        fakeRepository.setBookmarks(listOf(article))

        viewModel.onEvent(HeadlinesUiEvent.OnBookmarkClick(article.url))

        assertThat(fakeRepository.getBookmarks().first()).isEmpty()
    }

    @Test
    fun `OnArticleClick event emits NavigateToDetail effect`() = runTest {
        val url = "https://example.com/article"

        viewModel.uiEffect.test {
            viewModel.onEvent(HeadlinesUiEvent.OnArticleClick(url))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(HeadlinesUiEffect.NavigateToDetail::class.java)
            assertThat((effect as HeadlinesUiEffect.NavigateToDetail).url).isEqualTo(url)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `category changes emit in order`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().selectedCategory).isEqualTo(NewsCategory.GENERAL)

            NewsCategory.entries.drop(1).forEach { category ->
                viewModel.onEvent(HeadlinesUiEvent.OnCategorySelected(category))
                assertThat(awaitItem().selectedCategory).isEqualTo(category)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
