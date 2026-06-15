package com.uansari.newswise.display

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.uansari.newswise.core.navigation.HeadlinesRoute
import com.uansari.newswise.feature.headlines.navigation.headlinesScreen

@Composable
fun NewsWiseApp(
    paddingValues: PaddingValues
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HeadlinesRoute,
        modifier = Modifier.padding(paddingValues = paddingValues)
    ) {
        headlinesScreen(
            onArticleClick = { url -> })
    }
}