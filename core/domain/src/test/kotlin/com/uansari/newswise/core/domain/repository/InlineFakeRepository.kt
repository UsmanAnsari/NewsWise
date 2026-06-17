package com.uansari.newswise.core.domain.repository

import androidx.paging.PagingData
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class InlineFakeRepository : ArticleRepository {

    private val articles = MutableStateFlow<List<Article>>(emptyList())
    private val bookmarks = MutableStateFlow<List<Article>>(emptyList())

    fun addBookmark(article: Article) {
        bookmarks.value += article
    }

    override fun getHeadlinesByCategory(category: NewsCategory) =
        flowOf(PagingData.empty<Article>())

    override suspend fun upsertArticle(article: Article) {
        val index = articles.value.indexOfFirst { it.url == article.url }
        articles.value = if (index >= 0) {
            val existingIsBookmarked = articles.value[index].isBookmarked
            articles.value.toMutableList().also {
                it[index] = article.copy(isBookmarked = existingIsBookmarked)
            }
        } else {
            articles.value + article
        }
    }

    override fun observeArticle(url: String): Flow<Article?> =
        articles.map { list -> list.find { it.url == url } }

    override fun searchArticles(query: String) = flowOf(PagingData.empty<Article>())

    override fun getBookmarks(): Flow<List<Article>> = bookmarks

    override fun observeBookmarkStatus(url: String): Flow<Boolean> =
        bookmarks.map { it.any { a -> a.url == url } }

    override suspend fun toggleBookmark(url: String) {
        val current = bookmarks.value.toMutableList()
        val existing = current.find { it.url == url }
        if (existing != null) {
            bookmarks.value = current.filter { it.url != url }
        } else {
            val article = articles.value.find { it.url == url } ?: return
            bookmarks.value = current + article.copy(isBookmarked = true)
        }
    }
}

internal fun makeTestArticle(url: String = "https://example.com/test") = Article(
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