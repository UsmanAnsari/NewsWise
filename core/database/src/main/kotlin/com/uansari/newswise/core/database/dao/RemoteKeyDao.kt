package com.uansari.newswise.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.uansari.newswise.core.database.model.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE category = :category")
    suspend fun getByCategory(category: String): RemoteKeyEntity?

    @Upsert
    suspend fun upsert(remoteKey: RemoteKeyEntity)

    @Query("DELETE FROM remote_keys WHERE category = :category")
    suspend fun deleteByCategory(category: String)
}