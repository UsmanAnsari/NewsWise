package com.uansari.newswise.core.domain.repository

import androidx.paging.PagingData
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface ArticleRepository {

    fun getHeadlinesByCategory(category: NewsCategory): Flow<PagingData<Article>>

    suspend fun upsertArticle(article: Article)

    fun observeArticle(url: String): Flow<Article?>

    fun searchArticles(query: String): Flow<PagingData<Article>>

    fun getBookmarks(): Flow<List<Article>>

    fun observeBookmarkStatus(url: String): Flow<Boolean>

    suspend fun toggleBookmark(url: String)
}