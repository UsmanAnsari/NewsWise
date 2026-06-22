package com.uansari.newswise.core.database.dao

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.database.NewsDatabase
import com.uansari.newswise.core.testing.factory.ArticleFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ArticleDaoTest {

    private lateinit var database: NewsDatabase
    private lateinit var articleDao: ArticleDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NewsDatabase::class.java
        ).allowMainThreadQueries().build()
        articleDao = database.articleDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun `upsert articles and retrieve by category`() = runTest {
        val entity = ArticleFactory.makeEntity(category = "technology")
        articleDao.upsertArticles(listOf(entity))

        val snapshot = Pager(PagingConfig(pageSize = 10)) {
            articleDao.pagingSource("technology")
        }.flow.asSnapshot()

        assertThat(snapshot).hasSize(1)
        assertThat(snapshot.first().url).isEqualTo(entity.url)
    }

    @Test
    fun `toggle bookmark updates isBookmarked to true`() = runTest {
        val entity = ArticleFactory.makeEntity()
        articleDao.upsertArticles(listOf(entity))

        articleDao.updateBookmarkStatus(entity.url, isBookmarked = true)

        val updated = articleDao.getArticleByUrl(entity.url)
        assertThat(updated?.isBookmarked).isTrue()
    }

    @Test
    fun `delete non bookmarked articles preserves bookmarked ones`() = runTest {
        val bookmarked = ArticleFactory.makeEntity(
            url = "https://example.com/bookmarked", category = "technology", isBookmarked = true
        )
        val regular = ArticleFactory.makeEntity(
            url = "https://example.com/regular", category = "technology", isBookmarked = false
        )
        articleDao.upsertArticles(listOf(bookmarked, regular))

        articleDao.deleteNonBookmarkedArticlesByCategory("technology")

        assertThat(articleDao.getArticleByUrl("https://example.com/bookmarked")).isNotNull()
        assertThat(articleDao.getArticleByUrl("https://example.com/regular")).isNull()
    }

    @Test
    fun `observe bookmark status emits true after update`() = runTest {
        val entity = ArticleFactory.makeEntity(isBookmarked = false)
        articleDao.upsertArticles(listOf(entity))

        articleDao.observeBookmarkStatus(entity.url).test {
            assertThat(awaitItem()).isFalse()
            articleDao.updateBookmarkStatus(entity.url, true)
            assertThat(awaitItem()).isTrue()
            cancelAndIgnoreRemainingEvents()
        }
    }
}