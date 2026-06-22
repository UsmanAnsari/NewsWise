package com.uansari.newswise.display

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.uansari.newswise.display.component.DetailPanePlaceholder
import com.uansari.newswise.feature.bookmarks.navigation.bookmarkScreen
import com.uansari.newswise.feature.detail.navigation.detailScreen
import com.uansari.newswise.feature.headlines.navigation.headlinesScreen
import com.uansari.newswise.feature.search.navigation.searchScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NewsWiseApp() {
    val coroutineScope = rememberCoroutineScope()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val paneNavigator = rememberListDetailPaneScaffoldNavigator<String>()

    BackHandler(paneNavigator.canNavigateBack()) {
        coroutineScope.launch { paneNavigator.navigateBack() }
    }

    val isTopLevel = currentDestination?.hierarchy?.any { navDest ->
        TopLevelDestination.entries.any { dest ->
            when (dest) {
                TopLevelDestination.HEADLINES -> navDest.hasRoute(HeadlinesRoute::class)
                TopLevelDestination.SEARCH -> navDest.hasRoute(SearchRoute::class)
                TopLevelDestination.BOOKMARKS -> navDest.hasRoute(BookmarksRoute::class)
            }
        }
    } == true

    val isListHidden =
        paneNavigator.scaffoldValue[ListDetailPaneScaffoldRole.List] != PaneAdaptedValue.Expanded

    val showNavigationItems = isTopLevel && !isListHidden

    val paneScaffold: @Composable () -> Unit = {
        ListDetailPaneScaffold(
            directive = paneNavigator.scaffoldDirective,
            value = paneNavigator.scaffoldValue,
            listPane = {
                AnimatedPane {
                    NavHost(
                        navController = navController,
                        startDestination = HeadlinesRoute,
                        modifier = Modifier.statusBarsPadding()
                    ) {
                        headlinesScreen(onArticleClick = { url ->
                            coroutineScope.launch {
                                paneNavigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail, contentKey = url
                                )
                            }
                        })
                        searchScreen(onArticleClick = { url ->
                            coroutineScope.launch {
                                paneNavigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail, contentKey = url
                                )
                            }
                        })
                        bookmarkScreen(onArticleClick = { url ->
                            coroutineScope.launch {
                                paneNavigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail, contentKey = url
                                )
                            }
                        })
                    }
                }
            },


            detailPane = {
                AnimatedPane {
                    val articleUrl = paneNavigator.currentDestination?.contentKey

                    if (articleUrl != null) {
                        key(articleUrl) {
                            val detailNavController = rememberNavController()
                            NavHost(
                                navController = detailNavController,
                                startDestination = ArticleDetailRoute(articleUrl),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .statusBarsPadding()
                            ) {
                                detailScreen(
                                    onNavigateBack = {
                                        coroutineScope.launch {
                                            paneNavigator.navigateBack()
                                        }
                                    })
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding(),
                            contentAlignment = Alignment.Center
                        ) {
                            DetailPanePlaceholder()
                        }
                    }
                }
            })
    }

    if (showNavigationItems) {
        NavigationSuiteScaffold(
            layoutType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(
                currentWindowAdaptiveInfo()
            ), navigationSuiteItems = {
                TopLevelDestination.entries.forEach { destination ->
                    val isSelected = currentDestination.hierarchy.any { navDest ->
                        when (destination) {
                            TopLevelDestination.HEADLINES -> navDest.hasRoute(HeadlinesRoute::class)

                            TopLevelDestination.SEARCH -> navDest.hasRoute(SearchRoute::class)

                            TopLevelDestination.BOOKMARKS -> navDest.hasRoute(BookmarksRoute::class)
                        }
                    }
                    item(selected = isSelected, onClick = {
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
                            else destination.unselectedIcon, contentDescription = destination.label
                        )
                    }, label = { Text(destination.label) })
                }
            }) {
            paneScaffold()
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
        ) {
            paneScaffold()
        }
    }
}