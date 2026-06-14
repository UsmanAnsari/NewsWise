package com.uansari.newswise.core.data.repository

import com.uansari.newswise.core.database.NewsDatabase
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.uansari.newswise.core.network.api.NewsApiService
import com.uansari.newswise.core.database.dao.ArticleDao
import com.uansari.newswise.core.database.dao.RemoteKeyDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.uansari.newswise.core.data.mapper.toDomain
import com.uansari.newswise.core.data.paging.ArticleRemoteMediator
import com.uansari.newswise.core.data.paging.SearchPagingSource
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.repository.ArticleRepository
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ArticleRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val newsDatabase: NewsDatabase
) : ArticleRepository {

    override fun getHeadlinesByCategory(category: NewsCategory): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                // enablePlaceholders: show empty items while loading.
                // false: only show real items (no loading placeholders in the list).
                enablePlaceholders = false
            ),
            remoteMediator = ArticleRemoteMediator(
                category = category,
                newsApiService = newsApiService,
                articleDao = articleDao,
                remoteKeyDao = remoteKeyDao,
                newsDatabase = newsDatabase
            ),
            // pagingSourceFactory: Room's DAO provides the PagingSource.
            // RemoteMediator feeds Room with data. The UI observes Room.
            pagingSourceFactory = { articleDao.pagingSource(category.value) }
        ).flow
            // Map ArticleEntity (database type) → Article (domain type)
            // before the PagingData reaches the ViewModel/UI.
            .map { pagingData -> pagingData.map { it.toDomain() } }

    override fun searchArticles(query: String): Flow<PagingData<Article>> =
        Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(newsApiService, query) }
        ).flow

    override fun getBookmarks(): Flow<List<Article>> =
        articleDao.getBookmarkedArticles()
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeBookmarkStatus(url: String): Flow<Boolean> =
        articleDao.observeBookmarkStatus(url).map { it ?: false }

    override suspend fun toggleBookmark(url: String) {
        val article = articleDao.getArticleByUrl(url) ?: return
        articleDao.updateBookmarkStatus(url, !article.isBookmarked)
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}