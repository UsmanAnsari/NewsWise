package com.uansari.newswise.core.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.uansari.newswise.core.data.mapper.toDomain
import com.uansari.newswise.core.data.mapper.toEntity
import com.uansari.newswise.core.data.paging.ArticleRemoteMediator
import com.uansari.newswise.core.data.paging.SearchPagingSource
import com.uansari.newswise.core.database.NewsDatabase
import com.uansari.newswise.core.database.dao.ArticleDao
import com.uansari.newswise.core.database.dao.RemoteKeyDao
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.repository.ArticleRepository
import com.uansari.newswise.core.network.api.NewsApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ArticleRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val newsDatabase: NewsDatabase
) : ArticleRepository {

    override fun getHeadlinesByCategory(category: NewsCategory): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE, enablePlaceholders = false
        ),
        remoteMediator = ArticleRemoteMediator(
            category = category,
            newsApiService = newsApiService,
            articleDao = articleDao,
            remoteKeyDao = remoteKeyDao,
            newsDatabase = newsDatabase
        ),
        pagingSourceFactory = { articleDao.pagingSource(category.value) }).flow.map { pagingData -> pagingData.map { it.toDomain() } }

    override suspend fun upsertArticle(article: Article) {
        val existingIsBookmarked = articleDao.getArticleByUrl(article.url)?.isBookmarked ?: false
        articleDao.upsertArticles(
            listOf(article.toEntity().copy(isBookmarked = existingIsBookmarked))
        )
    }

    override fun observeArticle(url: String): Flow<Article?> =
        articleDao.observeArticleByUrl(url).map { it?.toDomain() }

    override fun searchArticles(query: String): Flow<PagingData<Article>> = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { SearchPagingSource(newsApiService, query) }).flow

    override fun getBookmarks(): Flow<List<Article>> =
        articleDao.getBookmarkedArticles().map { entities -> entities.map { it.toDomain() } }

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