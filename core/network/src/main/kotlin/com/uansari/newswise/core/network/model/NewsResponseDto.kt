package com.uansari.newswise.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponseDto(
    val status: String = "",
    val totalResults: Int = 0,
    val articles: List<ArticleDto> = emptyList(),
    val message: String? = null
)