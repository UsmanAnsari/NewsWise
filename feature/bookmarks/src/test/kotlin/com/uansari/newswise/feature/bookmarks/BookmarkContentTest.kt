package com.uansari.newswise.feature.bookmarks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.ui.tags.TestTags
import com.uansari.newswise.feature.bookmarks.component.BookmarkContent
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiEvent
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class BookmarkContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Empty state

    @Test
    fun `empty state shows empty state component`() {
        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = emptyList()), onEvent = {})
        }

        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertIsDisplayed()
    }

    @Test
    fun `empty state shows no bookmarks message`() {
        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = emptyList()), onEvent = {})
        }

        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertIsDisplayed()

        composeTestRule.onNodeWithText("No bookmarks yet", substring = true).assertIsDisplayed()
    }

    // Populated state

    @Test
    fun `populated state shows article titles`() {
        val articles = listOf(
            ArticleFactory.makeArticle(title = "Breaking News Article"),
            ArticleFactory.makeArticle(title = "Tech Story Today")
        )

        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = articles), onEvent = {})
        }

        composeTestRule.onNodeWithText("Breaking News Article").assertIsDisplayed()

        composeTestRule.onNodeWithText("Tech Story Today").assertIsDisplayed()
    }

    @Test
    fun `populated state does not show empty state`() {
        val articles = listOf(ArticleFactory.makeArticle())

        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = articles), onEvent = {})
        }

        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertDoesNotExist()
    }

    // Interactions

    @Test
    fun `tapping article card fires OnArticleClick with correct url`() {
        val article = ArticleFactory.makeArticle(
            title = "Tappable Article", url = "https://example.com/tappable"
        )
        val events = mutableListOf<BookmarkUiEvent>()

        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = listOf(article)), onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithText("Tappable Article").performClick()

        assertThat(events).hasSize(1)
        assertThat(events.first()).isEqualTo(BookmarkUiEvent.OnArticleClick("https://example.com/tappable"))
    }

    @Test
    fun `tapping bookmark icon fires OnBookmarkClick with correct url`() {
        val article = ArticleFactory.makeArticle(
            url = "https://example.com/bookmarked", isBookmarked = true
        )
        val events = mutableListOf<BookmarkUiEvent>()

        composeTestRule.setContent {
            BookmarkContent(
                uiState = BookmarkUiState(articles = listOf(article)), onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithContentDescription("Remove bookmark").performClick()

        assertThat(events.filterIsInstance<BookmarkUiEvent.OnBookmarkClick>()).hasSize(1)
        assertThat(
            events.filterIsInstance<BookmarkUiEvent.OnBookmarkClick>().first().url
        ).isEqualTo("https://example.com/bookmarked")
    }

}