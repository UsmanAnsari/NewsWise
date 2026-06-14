package com.uansari.newswise.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.uansari.newswise.core.network.api.NewsApiService
import com.uansari.newswise.core.data.mapper.toDomain
import com.uansari.newswise.core.domain.model.Article
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val newsApiService: NewsApiService,
    private val query: String
) : PagingSource<Int, Article>() {

    // getRefreshKey: called when the Pager needs to re-anchor after config change.
    // Returns the page nearest to the current scroll position.
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = newsApiService.searchArticles(
                query = query,
                page = page,
                pageSize = params.loadSize
            )
            val articles = response.articles
            LoadResult.Page(
                data = articles.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}