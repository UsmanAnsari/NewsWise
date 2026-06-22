package com.uansari.newswise.feature.search.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uansari.newswise.core.navigation.SearchRoute
import com.uansari.newswise.feature.search.display.SearchScreen

fun NavGraphBuilder.searchScreen(
    onArticleClick: (String) -> Unit
) {
    composable<SearchRoute> {
        SearchScreen(onArticleClick = onArticleClick)
    }
}