package com.uansari.newswise.core.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.uansari.newswise.core.database.model.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    fun pagingSource(category: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE isBookmarked = 1 ORDER BY publishedAt DESC")
    fun getBookmarkedArticles(): Flow<List<ArticleEntity>>

    @Upsert
    suspend fun upsertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE category = :category AND isBookmarked = 0")
    suspend fun deleteNonBookmarkedArticlesByCategory(category: String)

    @Query("SELECT * FROM articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): ArticleEntity?

    @Query("UPDATE articles SET isBookmarked = :isBookmarked WHERE url = :url")
    suspend fun updateBookmarkStatus(url: String, isBookmarked: Boolean)

    @Query("SELECT isBookmarked FROM articles WHERE url = :url")
    fun observeBookmarkStatus(url: String): Flow<Boolean?>
}