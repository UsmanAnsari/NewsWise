package com.uansari.newswise.feature.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.feature.search.component.SearchContent
import com.uansari.newswise.feature.search.display.SearchUiEvent
import com.uansari.newswise.feature.search.display.SearchUiState
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class SearchContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Idle state

    @Test
    fun `idle state shows search prompt`() {
        composeTestRule.setContent {
            val articles = flowOf(PagingData.empty<Article>()).collectAsLazyPagingItems()

            SearchContent(
                uiState = SearchUiState(query = ""),
                searchResults = articles,
                bookmarkedUrls = emptySet(),
                onEvent = {})
        }

        composeTestRule.onNodeWithText("Search topic for the latest news").assertIsDisplayed()
    }

    @Test
    fun `search field accepts text input and fires OnQueryChanged`() {
        val events = mutableListOf<SearchUiEvent>()

        composeTestRule.setContent {
            val articles =
                flowOf(PagingData.empty<Article>()).collectAsLazyPagingItems()

            SearchContent(
                uiState = SearchUiState(query = ""),
                searchResults = articles,
                bookmarkedUrls = emptySet(),
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithText("Search articles...").performTextInput("android")

        assertThat(events.filterIsInstance<SearchUiEvent.OnQueryChanged>()).hasSize(1)

        assertThat(events.filterIsInstance<SearchUiEvent.OnQueryChanged>().first().query).isEqualTo(
            "android"
        )
    }

    // bookmarkedUrls overlay

    @Test
    fun `article not in bookmarkedUrls shows save article icon`() {
        val articles = ArticleFactory.makeArticleList()
        val pagingItems = flowOf(PagingData.from(articles))
        val bookmarkArticles = articles.mapNotNull { if (it.isBookmarked) it.url else null }.toSet()

        composeTestRule.setContent {
            SearchContent(
                uiState = SearchUiState(query = "test"),
                searchResults = pagingItems.collectAsLazyPagingItems(),
                bookmarkedUrls = bookmarkArticles,
                onEvent = {})
        }
        composeTestRule.onAllNodesWithContentDescription("Save article").fetchSemanticsNodes()
            .isNotEmpty()
    }

    @Test
    fun `article in bookmarkedUrls shows remove bookmark icon`() {
        val articles = ArticleFactory.makeArticleList()
        val bookmarkArticles = articles.mapNotNull { if (it.isBookmarked) it.url else null }.toSet()
        val pagingItems = flowOf(PagingData.from(articles))

        composeTestRule.setContent {

            SearchContent(
                uiState = SearchUiState(query = "test"),
                searchResults = pagingItems.collectAsLazyPagingItems(),
                bookmarkedUrls = bookmarkArticles,
                onEvent = {})
        }
        composeTestRule.onAllNodesWithContentDescription("Remove bookmark").fetchSemanticsNodes()
            .isNotEmpty()
    }

}