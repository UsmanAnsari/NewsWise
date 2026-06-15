package com.uansari.newswise.feature.headlines.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.ui.components.ArticleCard
import com.uansari.newswise.core.ui.components.EmptyState
import com.uansari.newswise.core.ui.components.ErrorState
import com.uansari.newswise.core.ui.components.ShimmerArticleCard
import com.uansari.newswise.feature.headlines.display.HeadlinesUiEvent
import com.uansari.newswise.feature.headlines.display.HeadlinesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HeadlinesContent(
    uiState: HeadlinesUiState,
    articles: LazyPagingItems<Article>,
    onEvent: (HeadlinesUiEvent) -> Unit
) {
    val isRefreshing = articles.loadState.refresh is LoadState.Loading

    Column(modifier = Modifier.fillMaxSize()) {

        ScrollableTabRow(
            selectedTabIndex = uiState.categories.indexOf(uiState.selectedCategory),
            edgePadding = 0.dp
        ) {
            uiState.categories.forEach { category ->
                Tab(
                    selected = category == uiState.selectedCategory,
                    onClick = { onEvent(HeadlinesUiEvent.OnCategorySelected(category)) },
                    text = { Text(category.displayName) })
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                isRefreshing && articles.itemCount == 0 -> HeadlinesLoadingContent()

                articles.loadState.refresh is LoadState.Error && articles.itemCount == 0 -> {
                    val error = (articles.loadState.refresh as LoadState.Error).error
                    ErrorState(
                        message = error.localizedMessage ?: "Failed to load articles",
                        onRetry = { articles.retry() })
                }

                articles.loadState.refresh is LoadState.NotLoading && articles.itemCount == 0 -> {
                    EmptyState(
                        message = "No articles in ${uiState.selectedCategory.displayName}",
                        icon = Icons.Outlined.Newspaper
                    )
                }

                else -> {
                    PullToRefreshBox(
                        isRefreshing = isRefreshing, onRefresh = { articles.refresh() }) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                count = articles.itemCount,
                                key = articles.itemKey { it.url }) { index ->
                                val article = articles[index]
                                if (article != null) {
                                    ArticleCard(
                                        article = article,
                                        onClick = { onEvent(HeadlinesUiEvent.OnArticleClick(article.url)) },
                                        onBookmarkClick = {
                                            onEvent(
                                                HeadlinesUiEvent.OnBookmarkClick(
                                                    article.url
                                                )
                                            )
                                        },
                                        modifier = Modifier.animateItem()
                                    )
                                } else {
                                    ShimmerArticleCard()
                                }
                            }

                            when (articles.loadState.append) {
                                is LoadState.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) { CircularProgressIndicator() }
                                    }
                                }

                                is LoadState.Error -> {
                                    item {
                                        TextButton(
                                            onClick = { articles.retry() },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Failed to load more — tap to retry") }
                                    }
                                }

                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

