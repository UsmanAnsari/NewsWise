package com.uansari.newswise.core.testing.fake

import androidx.paging.PagingData
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class FakeArticleRepository : ArticleRepository {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    private val _bookmarks = MutableStateFlow<List<Article>>(emptyList())

    fun setArticles(articles: List<Article>) {
        _articles.value = articles
    }

    fun setBookmarks(articles: List<Article>) {
        _bookmarks.value = articles
    }

    override fun getHeadlinesByCategory(category: NewsCategory): Flow<PagingData<Article>> = flowOf(
        PagingData.from(
            _articles.value.filter { it.category == category.value })
    )

    override suspend fun upsertArticle(article: Article) {
        val existing = _articles.value.indexOfFirst { it.url == article.url }
        _articles.value = if (existing >= 0) {
            val existingIsBookmarked = _articles.value[existing].isBookmarked
            _articles.value.toMutableList().also {
                it[existing] = article.copy(isBookmarked = existingIsBookmarked)
            }
        } else {
            _articles.value + article
        }
    }

    override fun observeArticle(url: String): Flow<Article?> =
        _articles.map { articles -> articles.find { it.url == url } }

    override fun searchArticles(query: String): Flow<PagingData<Article>> = flowOf(
        PagingData.from(
            _articles.value.filter {
                it.title.contains(query, ignoreCase = true)
            })
    )

    override fun getBookmarks(): Flow<List<Article>> = _bookmarks

    override fun observeBookmarkStatus(url: String): Flow<Boolean> =
        _bookmarks.map { bookmarks -> bookmarks.any { it.url == url } }

    override suspend fun toggleBookmark(url: String) {
        val current = _bookmarks.value.toMutableList()
        val existing = current.find { it.url == url }
        if (existing != null) {
            _bookmarks.value = current.filter { it.url != url }
            _articles.value = _articles.value.map {
                if (it.url == url) it.copy(isBookmarked = false) else it
            }
        } else {
            val article = _articles.value.find { it.url == url } ?: return
            _bookmarks.value = current + article.copy(isBookmarked = true)
            _articles.value = _articles.value.map {
                if (it.url == url) it.copy(isBookmarked = true) else it
            }
        }
    }
}