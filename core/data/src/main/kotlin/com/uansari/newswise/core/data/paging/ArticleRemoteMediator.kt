package com.uansari.newswise.core.data.paging

import com.uansari.newswise.core.database.NewsDatabase
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.uansari.newswise.core.network.api.NewsApiService
import com.uansari.newswise.core.database.dao.ArticleDao
import com.uansari.newswise.core.database.dao.RemoteKeyDao
import com.uansari.newswise.core.data.mapper.toEntity
import com.uansari.newswise.core.database.model.ArticleEntity
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.database.model.RemoteKeyEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val category: NewsCategory,
    private val newsApiService: NewsApiService,
    private val articleDao: ArticleDao,
    private val remoteKeyDao: RemoteKeyDao,
    private val newsDatabase: NewsDatabase
) : RemoteMediator<Int, ArticleEntity>() {

    // LAUNCH_INITIAL_REFRESH: always refresh when the Pager is first created.
    // This ensures the UI shows fresh data on first load rather than stale cache.
    override suspend fun initialize() = InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, ArticleEntity>
    ): MediatorResult {

        val page = when (loadType) {
            // REFRESH: triggered on first load and pull-to-refresh.
            // Always start from page 1.
            LoadType.REFRESH -> 1

            // PREPEND: would mean loading data before the current first item.
            // News articles don't support backward pagination — newest is always first.
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)

            // APPEND: user scrolled to the bottom. Read the next page from remote_keys.
            // If nextPage is null, we've reached the end of the API's results.
            LoadType.APPEND -> {
                val remoteKey = remoteKeyDao.getByCategory(category.value)
                remoteKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val response = newsApiService.getTopHeadlines(
                category = category.value, page = page, pageSize = state.config.pageSize
            )
            val articles = response.articles
            val endOfPaginationReached = articles.isEmpty()

            // withTransaction: all database operations below are atomic.
            // If any write fails, ALL are rolled back — no partial state.
            newsDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Clear stale page tracking and non-bookmarked articles.
                    // Bookmarked articles are preserved — see ArticleDao comment.
                    remoteKeyDao.deleteByCategory(category.value)
                    articleDao.deleteNonBookmarkedArticlesByCategory(category.value)
                }

                remoteKeyDao.upsert(
                    RemoteKeyEntity(
                        category = category.value,
                        prevPage = if (page == 1) null else page - 1,
                        nextPage = if (endOfPaginationReached) null else page + 1
                    )
                )
                articleDao.upsertArticles(
                    articles.map { dto ->
                        // Preserve isBookmarked if this article is already in Room.
                        val existingIsBookmarked =
                            articleDao.getArticleByUrl(dto.url)?.isBookmarked ?: false
                        dto.toEntity(category.value).copy(isBookmarked = existingIsBookmarked)
                    })
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            // Network error (no connection, timeout, etc.)
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            // HTTP error (4xx, 5xx status codes)
            MediatorResult.Error(e)
        }
    }
}