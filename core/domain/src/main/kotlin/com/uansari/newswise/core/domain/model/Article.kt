package com.uansari.newswise.core.domain.model

data class Article(
    val url: String,
    val title: String,
    val description: String?,
    val content: String?,
    val urlToImage: String?,
    val sourceName: String,
    val author: String?,
    val publishedAt: String,    // ISO 8601 e.g. "2024-01-01T00:00:00Z"
    val category: String,       // NewsCategory.value - which tab this belongs to
    val isBookmarked: Boolean
)