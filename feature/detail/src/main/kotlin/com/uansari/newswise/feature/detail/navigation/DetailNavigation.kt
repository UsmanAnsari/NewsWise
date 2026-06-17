package com.uansari.newswise.feature.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.uansari.newswise.core.navigation.ArticleDetailRoute
import com.uansari.newswise.feature.detail.display.DetailScreen

fun NavGraphBuilder.detailScreen(
    onNavigateBack: () -> Unit
) {
    composable<ArticleDetailRoute> {
        DetailScreen(onNavigateBack = onNavigateBack)
    }
}