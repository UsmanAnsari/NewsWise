package com.uansari.newswise.feature.search.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.ui.components.ArticleCard
import com.uansari.newswise.core.ui.components.EmptyState
import com.uansari.newswise.core.ui.components.ErrorState
import com.uansari.newswise.core.ui.components.LoadingIndicator
import com.uansari.newswise.core.ui.components.ShimmerArticleCard
import com.uansari.newswise.feature.search.display.SearchUiEvent
import com.uansari.newswise.feature.search.display.SearchUiState

@Composable
internal fun SearchContent(
    uiState: SearchUiState,
    searchResults: LazyPagingItems<Article>,
    bookmarkedUrls: Set<String>,
    onEvent: (SearchUiEvent) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(modifier = Modifier.fillMaxSize()) {

        OutlinedTextField(
            value = uiState.query,
            onValueChange = { onEvent(SearchUiEvent.OnQueryChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            placeholder = { Text("Search articles...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search, contentDescription = null
                )
            },
            trailingIcon = {
                if (uiState.query.isNotEmpty()) {
                    IconButton(onClick = { onEvent(SearchUiEvent.OnClearQuery) }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear, contentDescription = "Clear search"
                        )
                    }
                }
            },
            shape = RoundedCornerShape(33),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { keyboardController?.hide() }),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {

                uiState.query.isBlank() -> {
                    EmptyState(
                        message = "Search topic for the latest news", icon = Icons.Outlined.Search
                    )
                }

                searchResults.loadState.refresh is LoadState.Loading && searchResults.itemCount == 0 -> {
                    LoadingIndicator()
                }

                searchResults.loadState.refresh is LoadState.Error && searchResults.itemCount == 0 -> {
                    val error = (searchResults.loadState.refresh as LoadState.Error).error
                    ErrorState(
                        message = error.localizedMessage ?: "Search failed",
                        onRetry = { searchResults.retry() })
                }

                searchResults.loadState.refresh is LoadState.NotLoading && searchResults.itemCount == 0 -> {
                    EmptyState(
                        message = "No results for \"${uiState.query}\"",
                        icon = Icons.Outlined.SearchOff
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            count = searchResults.itemCount,
                            key = searchResults.itemKey { it.url }) { index ->
                            val article = searchResults[index]
                            if (article != null) {
                                ArticleCard(
                                    article = article.copy(isBookmarked = article.url in bookmarkedUrls),
                                    onClick = { onEvent(SearchUiEvent.OnArticleClick(article)) },
                                    onBookmarkClick = {
                                        onEvent(
                                            SearchUiEvent.OnBookmarkClick(
                                                article
                                            )
                                        )
                                    },
                                    modifier = Modifier.animateItem()
                                )
                            } else {
                                ShimmerArticleCard()
                            }
                        }

                        when (searchResults.loadState.append) {
                            is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            is LoadState.Error -> {
                                item {
                                    TextButton(
                                        onClick = { searchResults.retry() },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Failed to load more — tap to retry")
                                    }
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