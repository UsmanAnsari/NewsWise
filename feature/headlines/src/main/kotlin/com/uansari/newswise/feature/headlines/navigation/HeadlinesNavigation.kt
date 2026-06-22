package com.uansari.newswise.feature.headlines.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uansari.newswise.core.navigation.HeadlinesRoute
import com.uansari.newswise.feature.headlines.display.HeadlinesScreen

fun NavGraphBuilder.headlinesScreen(
    onArticleClick: (String) -> Unit
) {
    composable<HeadlinesRoute> {
        HeadlinesScreen(onArticleClick = onArticleClick)
    }
}