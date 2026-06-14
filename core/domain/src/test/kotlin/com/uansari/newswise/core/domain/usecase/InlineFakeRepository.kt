package com.uansari.newswise.core.domain.usecase

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.repository.ArticleRepository

class InlineFakeRepository : ArticleRepository {
    private val bookmarks = MutableStateFlow<List<Article>>(emptyList())

    fun addBookmark(article: Article) {
        bookmarks.value += article
    }

    override fun getHeadlinesByCategory(category: NewsCategory) =
        flowOf(PagingData.empty<Article>())

    override fun searchArticles(query: String) = flowOf(PagingData.empty<Article>())

    override fun getBookmarks(): Flow<List<Article>> = bookmarks
    override fun observeBookmarkStatus(url: String) = bookmarks.map { it.any { a -> a.url == url } }

    override suspend fun toggleBookmark(url: String) {
        val current = bookmarks.value.toMutableList()
        val existing = current.find { it.url == url }
        if (existing != null) {
            bookmarks.value = current.filter { it.url != url }
        }
    }
}

fun makeTestArticle(url: String = "https://example.com/test") = Article(
    url = url,
    title = "Test",
    description = null,
    content = null,
    urlToImage = null,
    sourceName = "Source",
    author = null,
    publishedAt = "2024-01-01T00:00:00Z",
    category = "general",
    isBookmarked = false
)