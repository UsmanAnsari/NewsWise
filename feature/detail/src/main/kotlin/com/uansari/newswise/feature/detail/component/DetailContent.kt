package com.uansari.newswise.feature.detail.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.ui.components.ErrorState
import com.uansari.newswise.core.ui.components.LoadingIndicator
import com.uansari.newswise.core.ui.components.PublishedAtLabel
import com.uansari.newswise.feature.detail.display.DetailUiEvent
import com.uansari.newswise.feature.detail.display.DetailUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailContent(
    uiState: DetailUiState, onEvent: (DetailUiEvent) -> Unit
) {
    Scaffold(contentWindowInsets = WindowInsets(0), topBar = {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { onEvent(DetailUiEvent.OnBackClick) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                uiState.article?.let { article ->
                    IconButton(onClick = { onEvent(DetailUiEvent.OnShareClick) }) {
                        Icon(
                            imageVector = Icons.Outlined.Share, contentDescription = "Share article"
                        )
                    }

                    IconButton(onClick = { onEvent(DetailUiEvent.OnBookmarkClick) }) {
                        Icon(
                            imageVector = if (article.isBookmarked) Icons.Filled.Bookmark
                            else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (article.isBookmarked) "Remove bookmark"
                            else "Save article",
                            tint = if (article.isBookmarked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            },
            windowInsets = WindowInsets(0),
        )
    }, bottomBar = {
        uiState.article?.let {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxWidth()
            ) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                OutlinedButton(
                    onClick = { onEvent(DetailUiEvent.OnViewSourceClick) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    border = BorderStroke(
                        width = 1.dp, color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text("Read Full Article")
                }
            }
        }
    }) { innerPadding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )

            }

            uiState.hasError -> {
                ErrorState(
                    message = "Article not available",
                    onRetry = { onEvent(DetailUiEvent.OnBackClick) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            uiState.article != null -> {
                ArticleDetailBody(
                    article = uiState.article,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)

                )

            }
        }
    }
}

@Composable
private fun ArticleDetailBody(
    article: Article, modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        article.urlToImage?.let { imageUrl ->
            item(key = "hero_image") {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }

        item(key = "body") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.sourceName,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    PublishedAtLabel(publishedAt = article.publishedAt)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                article.author?.let { author ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "By $author",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                article.description?.let { description ->
                    Text(
                        text = description, style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                article.content?.let { content ->
                    val cleanContent = content.substringBefore("[+").trim()
                    if (cleanContent.isNotEmpty()) {
                        Text(
                            text = cleanContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}