package com.uansari.newswise.feature.bookmarks.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.uansari.newswise.core.ui.components.ArticleCard
import com.uansari.newswise.core.ui.components.EmptyState
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiEvent
import com.uansari.newswise.feature.bookmarks.display.BookmarkUiState

@Composable
internal fun BookmarkContent(
    uiState: BookmarkUiState, onEvent: (BookmarkUiEvent) -> Unit
) {
    if (uiState.articles.isEmpty()) {
        EmptyState(
            message = "No bookmarks yet\nSave articles to read them later",
            icon = Icons.Outlined.BookmarkBorder
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = uiState.articles, key = { article -> article.url }) { article ->
                ArticleCard(
                    article = article,
                    onClick = { onEvent(BookmarkUiEvent.OnArticleClick(article.url)) },
                    onBookmarkClick = { onEvent(BookmarkUiEvent.OnBookmarkClick(article.url)) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}