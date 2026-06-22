package com.uansari.newswise.feature.headlines

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.ui.tags.TestTags
import com.uansari.newswise.feature.headlines.component.HeadlinesContent
import com.uansari.newswise.feature.headlines.display.HeadlinesUiEvent
import com.uansari.newswise.feature.headlines.display.HeadlinesUiState
import kotlinx.coroutines.flow.flowOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class HeadlinesContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Categories

    @Test
    fun `first 3 news category tabs are displayed`() {
        composeTestRule.setContent {
            val articles = flowOf(PagingData.empty<Article>()).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(), articles = articles, onEvent = {})
        }
        composeTestRule.onNodeWithText(NewsCategory.GENERAL.displayName, substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(NewsCategory.BUSINESS.displayName, substring = true)
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(NewsCategory.SCIENCE.displayName, substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun `selected category tab appears selected`() {
        composeTestRule.setContent {
            val articles = flowOf(PagingData.empty<Article>()).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(selectedCategory = NewsCategory.TECHNOLOGY),
                articles = articles,
                onEvent = {})
        }

        composeTestRule.onNodeWithText(NewsCategory.TECHNOLOGY.displayName).assertIsSelected()
    }

    // Populated state

    @Test
    fun `populated articles show article titles`() {
        val articles = listOf(
            ArticleFactory.makeArticle(title = "Top Story Today"),
            ArticleFactory.makeArticle(title = "Second Headline")
        )

        composeTestRule.setContent {
            val pagingItems = flowOf(PagingData.from(articles)).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(), articles = pagingItems, onEvent = {})
        }

        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule.onAllNodesWithText("Top Story Today").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Top Story Today").assertIsDisplayed()
    }

    // Interaction

    @Test
    fun `tapping category tab fires OnCategorySelected`() {
        val events = mutableListOf<HeadlinesUiEvent>()

        composeTestRule.setContent {
            val articles = flowOf(PagingData.empty<Article>()).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(selectedCategory = NewsCategory.GENERAL),
                articles = articles,
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithText(NewsCategory.TECHNOLOGY.displayName).performClick()

        assertThat(
            events.filterIsInstance<HeadlinesUiEvent.OnCategorySelected>()
        ).hasSize(1)

        assertThat(
            events.filterIsInstance<HeadlinesUiEvent.OnCategorySelected>().first().category
        ).isEqualTo(NewsCategory.TECHNOLOGY)
    }

    @Test
    fun `tapping article card fires OnArticleClick`() {
        val article = ArticleFactory.makeArticle()
        val events = mutableListOf<HeadlinesUiEvent>()

        composeTestRule.setContent {
            val articles = flowOf(PagingData.from(listOf(article))).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(selectedCategory = NewsCategory.GENERAL),
                articles = articles,
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithTag(TestTags.ARTICLE_CARD).performClick()

        assertThat(
            events.filterIsInstance<HeadlinesUiEvent.OnArticleClick>()
        ).hasSize(1)
    }

    @Test
    fun `tapping bookmark save fires OnBookmarkClick`() {
        val article = ArticleFactory.makeArticle()
        val events = mutableListOf<HeadlinesUiEvent>()

        composeTestRule.setContent {
            val articles = flowOf(PagingData.from(listOf(article))).collectAsLazyPagingItems()

            HeadlinesContent(
                uiState = HeadlinesUiState(selectedCategory = NewsCategory.GENERAL),
                articles = articles,
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithTag(TestTags.TOGGLE_BOOKMARK_BTN).performClick()

        assertThat(
            events.filterIsInstance<HeadlinesUiEvent.OnBookmarkClick>()
        ).hasSize(1)
    }
}