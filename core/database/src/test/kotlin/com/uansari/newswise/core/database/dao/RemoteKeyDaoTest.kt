package com.uansari.newswise.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.uansari.newswise.core.database.NewsDatabase
import com.uansari.newswise.core.database.model.RemoteKeyEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RemoteKeyDaoTest {

    private lateinit var database: NewsDatabase
    private lateinit var remoteKeyDao: RemoteKeyDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, NewsDatabase::class.java
        ).allowMainThreadQueries().build()
        remoteKeyDao = database.remoteKeyDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun upsert_and_retrieve_by_category() = runTest {
        remoteKeyDao.upsert(
            RemoteKeyEntity(category = "technology", prevPage = null, nextPage = 2)
        )
        val result = remoteKeyDao.getByCategory("technology")
        assertThat(result?.nextPage).isEqualTo(2)
    }

    @Test
    fun delete_by_category_removes_only_that_category() = runTest {
        remoteKeyDao.upsert(RemoteKeyEntity("technology", null, 2))
        remoteKeyDao.upsert(RemoteKeyEntity("health", null, 2))

        remoteKeyDao.deleteByCategory("technology")

        assertThat(remoteKeyDao.getByCategory("technology")).isNull()
        assertThat(remoteKeyDao.getByCategory("health")).isNotNull()
    }
}