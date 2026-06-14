package com.uansari.newswise.core.testing.fake

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import com.uansari.newswise.core.domain.model.Article
import com.uansari.newswise.core.domain.model.NewsCategory
import com.uansari.newswise.core.domain.repository.ArticleRepository

class FakeArticleRepository : ArticleRepository {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    private val _bookmarks = MutableStateFlow<List<Article>>(emptyList())

    // Control the repository state from tests:
    // repository.setArticles(ArticleFactory.makeArticleList(5))
    fun setArticles(articles: List<Article>) {
        _articles.value = articles
    }

    fun setBookmarks(articles: List<Article>) {
        _bookmarks.value = articles
    }

    override fun getHeadlinesByCategory(category: NewsCategory): Flow<PagingData<Article>> =
        flowOf(
            PagingData.from(
            _articles.value.filter { it.category == category.value }))

    override fun searchArticles(query: String): Flow<PagingData<Article>> = flowOf(
        PagingData.from(
        _articles.value.filter {
            it.title.contains(query, ignoreCase = true)
        }))

    override fun getBookmarks(): Flow<List<Article>> = _bookmarks

    override fun observeBookmarkStatus(url: String): Flow<Boolean> =
        _bookmarks.map { bookmarks -> bookmarks.any { it.url == url } }

    override suspend fun toggleBookmark(url: String) {
        val current = _bookmarks.value.toMutableList()
        val existing = current.find { it.url == url }
        if (existing != null) {
            _bookmarks.value = current.filter { it.url != url }
        } else {
            val article = _articles.value.find { it.url == url } ?: return
            _bookmarks.value = current + article.copy(isBookmarked = true)
        }
    }
}