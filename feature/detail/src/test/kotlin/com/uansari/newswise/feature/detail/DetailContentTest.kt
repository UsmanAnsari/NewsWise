package com.uansari.newswise.feature.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.testing.factory.ArticleFactory
import com.uansari.newswise.core.ui.tags.TestTags
import com.uansari.newswise.feature.detail.component.DetailContent
import com.uansari.newswise.feature.detail.display.DetailUiEvent
import com.uansari.newswise.feature.detail.display.DetailUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [36])
class DetailContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // Loading state

    @Test
    fun `loading state shows loading indicator`() {
        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(isLoading = true), onEvent = {})
        }

        composeTestRule.onNodeWithTag(TestTags.LOADING_INDICATOR).assertIsDisplayed()
    }

    @Test
    fun `loading state does not show TopAppBar actions`() {
        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(isLoading = true), onEvent = {})
        }

        composeTestRule.onNodeWithContentDescription("Share article").assertDoesNotExist()

        composeTestRule.onNodeWithContentDescription("Save article").assertDoesNotExist()
    }

    // Error state

    @Test
    fun `error state shows article not available message`() {
        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(isLoading = false, hasError = true), onEvent = {})
        }

        composeTestRule.onNodeWithText("Article not available").assertIsDisplayed()
    }

    @Test
    fun `error state retry button fires OnBackClick`() {
        val events = mutableListOf<DetailUiEvent>()

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(isLoading = false, hasError = true),
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithText("Try Again").performClick()

        assertThat(events).contains(DetailUiEvent.OnBackClick)
    }

    // Loaded state

    @Test
    fun `loaded state shows article title`() {
        val article = ArticleFactory.makeArticle(title = "Compose UI Test Article")

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false), onEvent = {})
        }

        composeTestRule.onNodeWithText("Compose UI Test Article").assertIsDisplayed()
    }

    @Test
    fun `loaded state shows source name`() {
        val article = ArticleFactory.makeArticle(sourceName = "Test Source")

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false), onEvent = {})
        }

        composeTestRule.onNodeWithText("Test Source").assertIsDisplayed()
    }

    @Test
    fun `loaded state shows Read Full Article button`() {
        val article = ArticleFactory.makeArticle()

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false), onEvent = {})
        }

        composeTestRule.onNodeWithText("Read Full Article").assertIsDisplayed()
    }

    @Test
    fun `Read Full Article button fires OnViewSourceClick`() {
        val events = mutableListOf<DetailUiEvent>()
        val article = ArticleFactory.makeArticle()

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false),
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithText("Read Full Article").performClick()

        assertThat(events.filterIsInstance<DetailUiEvent.OnViewSourceClick>()).hasSize(1)
    }

    @Test
    fun `unbookmarked article shows Save article icon`() {
        val article = ArticleFactory.makeArticle(isBookmarked = false)

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false), onEvent = {})
        }

        composeTestRule.onNodeWithContentDescription("Save article").assertIsDisplayed()
    }

    @Test
    fun `bookmarked article shows Remove bookmark icon`() {
        val article = ArticleFactory.makeArticle(isBookmarked = true)

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false), onEvent = {})
        }

        composeTestRule.onNodeWithContentDescription("Remove bookmark").assertIsDisplayed()
    }

    @Test
    fun `back button fires OnBackClick`() {
        val events = mutableListOf<DetailUiEvent>()

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(isLoading = true), onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithContentDescription("Back").performClick()

        assertThat(events).contains(DetailUiEvent.OnBackClick)
    }

    @Test
    fun `share button fires OnShareClick`() {
        val events = mutableListOf<DetailUiEvent>()
        val article = ArticleFactory.makeArticle()

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false),
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithContentDescription("Share article").performClick()

        assertThat(events.filterIsInstance<DetailUiEvent.OnShareClick>()).hasSize(1)
    }

    @Test
    fun `bookmark button fires OnBookmarkClick`() {
        val events = mutableListOf<DetailUiEvent>()
        val article = ArticleFactory.makeArticle(isBookmarked = false)

        composeTestRule.setContent {
            DetailContent(
                uiState = DetailUiState(article = article, isLoading = false),
                onEvent = { events.add(it) })
        }

        composeTestRule.onNodeWithContentDescription("Save article").performClick()

        assertThat(events.filterIsInstance<DetailUiEvent.OnBookmarkClick>()).hasSize(1)
    }
}