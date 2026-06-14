package com.uansari.newswise.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val category: String,
    val prevPage: Int?,
    val nextPage: Int?
)