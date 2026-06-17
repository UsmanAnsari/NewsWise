package com.uansari.newswise.display

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uansari.newswise.core.navigation.ArticleDetailRoute
import com.uansari.newswise.core.navigation.BookmarksRoute
import com.uansari.newswise.core.navigation.HeadlinesRoute
import com.uansari.newswise.core.navigation.SearchRoute
import com.uansari.newswise.core.navigation.TopLevelDestination
import com.uansari.newswise.feature.bookmarks.navigation.bookmarkScreen
import com.uansari.newswise.feature.detail.navigation.detailScreen
import com.uansari.newswise.feature.headlines.navigation.headlinesScreen
import com.uansari.newswise.feature.search.navigation.searchScreen

@Composable
fun NewsWiseApp() {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            val isTopLevel = currentDestination?.hierarchy?.any { navDest ->
                TopLevelDestination.entries.any { dest ->
                    when (dest) {
                        TopLevelDestination.HEADLINES -> navDest.hasRoute(HeadlinesRoute::class)
                        TopLevelDestination.SEARCH -> navDest.hasRoute(SearchRoute::class)
                        TopLevelDestination.BOOKMARKS -> navDest.hasRoute(BookmarksRoute::class)
                    }
                }
            } == true

            if (isTopLevel) {

                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->

                        val isSelected = currentDestination.hierarchy.any { navDest ->
                            when (destination) {
                                TopLevelDestination.HEADLINES -> navDest.hasRoute(HeadlinesRoute::class)
                                TopLevelDestination.SEARCH -> navDest.hasRoute(SearchRoute::class)
                                TopLevelDestination.BOOKMARKS -> navDest.hasRoute(BookmarksRoute::class)
                            }
                        }

                        NavigationBarItem(selected = isSelected, onClick = {
                            val route = when (destination) {
                                TopLevelDestination.HEADLINES -> HeadlinesRoute
                                TopLevelDestination.SEARCH -> SearchRoute
                                TopLevelDestination.BOOKMARKS -> BookmarksRoute
                            }
                            navController.navigate(route) {
                                popUpTo<HeadlinesRoute> { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }, icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon
                                else destination.unselectedIcon,
                                contentDescription = destination.label
                            )
                        }, label = { Text(destination.label) })
                    }
                }

            }
        }) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = HeadlinesRoute,
            modifier = Modifier.padding(paddingValues)
        ) {

            headlinesScreen(onArticleClick = { url ->
                navController.navigate(ArticleDetailRoute(url))
            })

            searchScreen(onArticleClick = { url ->
                navController.navigate(ArticleDetailRoute(url))
            })

            bookmarkScreen(onArticleClick = { url ->
                navController.navigate(ArticleDetailRoute(url))
            })

            detailScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}