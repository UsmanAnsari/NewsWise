package com.uansari.newswise.core.database.di

import com.uansari.newswise.core.database.NewsDatabase
import android.content.Context
import androidx.room.Room
import com.uansari.newswise.core.database.dao.ArticleDao
import com.uansari.newswise.core.database.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(
        @ApplicationContext context: Context
    ): NewsDatabase = Room.databaseBuilder(
        context,
        NewsDatabase::class.java,
        "news_db"
    ).build()

    @Provides
    @Singleton
    fun provideArticleDao(database: NewsDatabase): ArticleDao =
        database.articleDao()

    @Provides
    @Singleton
    fun provideRemoteKeyDao(database: NewsDatabase): RemoteKeyDao =
        database.remoteKeyDao()
}