package com.uansari.newswise.core.testing.factory

import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.database.model.ArticleEntity
import com.uansari.newswise.core.domain.model.NewsCategory
import kotlin.random.Random

object ArticleFactory {

    fun makeArticle(
        url: String = "https://example.com/article-${Random.nextInt(10000)}",
        title: String = "Test Article Title",
        category: String = NewsCategory.GENERAL.value,
        isBookmarked: Boolean = false,
        sourceName: String = "BBC News"
    ) = Article(
        url = url,
        title = title,
        description = "A detailed description of the test article for testing purposes.",
        content = "Article content truncated to 200 chars on NewsAPI free tier.",
        urlToImage = null,
        sourceName = sourceName,
        author = "Test Author",
        publishedAt = "2024-06-01T09:00:00Z",
        category = category,
        isBookmarked = isBookmarked
    )

    fun makeArticleList(
        count: Int = 5,
        category: String = NewsCategory.GENERAL.value
    ) = List(count) { index ->
        makeArticle(
            url = "https://example.com/article-$index",
            title = "Test Article $index",
            category = category
        )
    }

    // Produces an ArticleEntity for DAO instrumented tests.
    fun makeEntity(
        url: String = "https://example.com/entity-${Random.nextInt(10000)}",
        category: String = NewsCategory.GENERAL.value,
        isBookmarked: Boolean = false
    ) = ArticleEntity(
        url = url,
        sourceId = "bbc-news",
        sourceName = "BBC News",
        author = null,
        title = "Test Entity Title",
        description = "Test entity description",
        urlToImage = null,
        publishedAt = "2024-06-01T09:00:00Z",
        content = "Test content",
        category = category,
        isBookmarked = isBookmarked
    )
}