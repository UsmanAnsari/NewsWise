package com.uansari.newswise.feature.bookmarks

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.usecase.GetBookmarksUseCase
import com.uansari.newswise.core.domain.usecase.ToggleBookmarkUseCase
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.testing.fake.FakeArticleRepository
import com.uansari.newswise.core.testing.rule.TestDispatcherRule
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiEffect
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiEvent
import com.uansari.newswise.feature.bookmarks.display.BookmarkViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.jvm.java

class BookmarkViewModelTest {

    @get:Rule
    val testDispatcherRule = TestDispatcherRule()
    private lateinit var fakeRepository: FakeArticleRepository
    private lateinit var viewModel: BookmarkViewModel

    @Before
    fun setUp() {
        fakeRepository = FakeArticleRepository()
        viewModel = BookmarkViewModel(
            getBookmarks = GetBookmarksUseCase(fakeRepository),
            toggleBookmark = ToggleBookmarkUseCase(fakeRepository)
        )
    }

    @Test
    fun `initial state has empty articles list`() {
        assertThat(viewModel.uiState.value.articles).isEmpty()
    }

    @Test
    fun `state updates reactively when bookmarks are added`() = runTest {
        val article = ArticleFactory.makeArticle(isBookmarked = true)

        viewModel.uiState.test {
            assertThat(awaitItem().articles).isEmpty()

            fakeRepository.setBookmarks(listOf(article))
            val state = awaitItem()
            assertThat(state.articles).hasSize(1)
            assertThat(state.articles.first().url).isEqualTo(article.url)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `state becomes empty when all bookmarks are removed`() = runTest {
        val article = ArticleFactory.makeArticle(isBookmarked = true)

        viewModel.uiState.test {
            assertThat(awaitItem().articles).isEmpty()

            fakeRepository.setBookmarks(listOf(article))
            assertThat(awaitItem().articles).hasSize(1)

            fakeRepository.setBookmarks(emptyList())
            assertThat(awaitItem().articles).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnBookmarkClick event removes article via reactive Room update`() = runTest {
        val article = ArticleFactory.makeArticle(isBookmarked = true)
        fakeRepository.setArticles(listOf(article))

        viewModel.uiState.test {
            assertThat(awaitItem().articles).isEmpty()

            fakeRepository.setBookmarks(listOf(article))
            assertThat(awaitItem().articles).hasSize(1)

            viewModel.onEvent(BookmarkUiEvent.OnBookmarkClick(article.url))

            assertThat(awaitItem().articles).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `OnArticleClick event emits NavigateToDetail effect`() = runTest {
        val url = "https://example.com/article"

        viewModel.uiEffect.test {
            viewModel.onEvent(BookmarkUiEvent.OnArticleClick(url))
            val effect = awaitItem()
            assertThat(effect).isInstanceOf(BookmarkUiEffect.NavigateToDetail::class.java)
            assertThat((effect as BookmarkUiEffect.NavigateToDetail).url).isEqualTo(url)
            cancelAndIgnoreRemainingEvents()
        }
    }
}