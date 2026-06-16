package com.uansari.newswise.feature.bookmarks.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uansari.newswise.core.navigation.BookmarksRoute
import com.uansari.newswise.feature.bookmarks.display.BookmarkScreen

fun NavGraphBuilder.bookmarkScreen(
    onArticleClick: (String) -> Unit
) {
    composable<BookmarksRoute> {
        BookmarkScreen(onArticleClick = onArticleClick)
    }
}