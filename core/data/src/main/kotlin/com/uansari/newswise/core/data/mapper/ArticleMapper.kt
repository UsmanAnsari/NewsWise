package com.uansari.newswise.core.data.mapper

import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.network.model.ArticleDto
import com.uansari.newswise.core.database.model.ArticleEntity

fun ArticleDto.toEntity(category: String): ArticleEntity = ArticleEntity(
    url = url,
    sourceId = source.id,
    sourceName = source.name,
    author = author,
    title = title,
    description = description,
    urlToImage = urlToImage,
    publishedAt = publishedAt,
    content = content,
    category = category,
    isBookmarked = false // New articles from network are never bookmarked
)

fun ArticleDto.toDomain(category: String = "search"): Article = Article(
    url = url,
    title = title,
    description = description,
    content = content,
    urlToImage = urlToImage,
    sourceName = source.name,
    author = author,
    publishedAt = publishedAt,
    category = category,
    isBookmarked = false
)

fun ArticleEntity.toDomain(): Article = Article(
    url = url,
    title = title,
    description = description,
    content = content,
    urlToImage = urlToImage,
    sourceName = sourceName,
    author = author,
    publishedAt = publishedAt,
    category = category,
    isBookmarked = isBookmarked
)