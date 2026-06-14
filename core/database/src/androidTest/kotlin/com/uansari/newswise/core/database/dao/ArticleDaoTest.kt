package com.uansari.newswise.core.database.dao

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.database.NewsDatabase
import com.uansari.newswise.core.testing.factory.ArticleFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleDaoTest {

    private lateinit var database: NewsDatabase
    private lateinit var articleDao: ArticleDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // inMemoryDatabaseBuilder: creates a Room database in memory.
        // No files written to disk. Wiped between test runs.
        // allowMainThreadQueries: only for tests — removes the "must run on
        // background thread" restriction for setup/teardown convenience.
        database = Room.inMemoryDatabaseBuilder(
            context, NewsDatabase::class.java
        ).allowMainThreadQueries().build()
        articleDao = database.articleDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun upsert_articles_and_retrieve_by_category() = runTest {
        val entity = ArticleFactory.makeEntity(category = "technology")
        articleDao.upsertArticles(listOf(entity))

        val snapshot = Pager(PagingConfig(pageSize = 10)) {
            articleDao.pagingSource("technology")
        }.flow.asSnapshot()

        assertThat(snapshot).hasSize(1)
        assertThat(snapshot.first().url).isEqualTo(entity.url)
    }

    @Test
    fun toggle_bookmark_updates_isBookmarked_to_true() = runTest {
        val entity = ArticleFactory.makeEntity()
        articleDao.upsertArticles(listOf(entity))

        articleDao.updateBookmarkStatus(entity.url, isBookmarked = true)

        val updated = articleDao.getArticleByUrl(entity.url)
        assertThat(updated?.isBookmarked).isTrue()
    }

    @Test
    fun delete_non_bookmarked_preserves_bookmarked_articles() = runTest {
        val bookmarked = ArticleFactory.makeEntity(
            url = "https://example.com/bookmarked",
            category = "technology",
            isBookmarked = true
        )
        val regular = ArticleFactory.makeEntity(
            url = "https://example.com/regular",
            category = "technology",
            isBookmarked = false
        )
        articleDao.upsertArticles(listOf(bookmarked, regular))

        articleDao.deleteNonBookmarkedArticlesByCategory("technology")

        assertThat(articleDao.getArticleByUrl("https://example.com/bookmarked")).isNotNull()
        assertThat(articleDao.getArticleByUrl("https://example.com/regular")).isNull()
    }

    @Test
    fun observe_bookmark_status_emits_updates() = runTest {
        val entity = ArticleFactory.makeEntity()
        articleDao.upsertArticles(listOf(entity))

        // Phase 7 will use Turbine for flow assertions. For now, use simple collect.
        articleDao.updateBookmarkStatus(entity.url, isBookmarked = true)
        val isBookmarked = articleDao.getArticleByUrl(entity.url)?.isBookmarked
        assertThat(isBookmarked).isTrue()
    }
}