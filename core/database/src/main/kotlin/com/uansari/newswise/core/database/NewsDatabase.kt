package com.uansari.newswise.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uansari.newswise.core.database.dao.ArticleDao
import com.uansari.newswise.core.database.dao.RemoteKeyDao
import com.uansari.newswise.core.database.model.ArticleEntity
import com.uansari.newswise.core.database.model.RemoteKeyEntity

@Database(
    entities = [ArticleEntity::class, RemoteKeyEntity::class], version = 1, exportSchema = false
)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}