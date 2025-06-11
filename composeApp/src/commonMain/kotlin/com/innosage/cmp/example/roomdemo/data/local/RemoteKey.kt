package com.innosage.cmp.example.roomdemo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// This entity stores the next page key for a given note item.
// It is the cornerstone of the network + database pagination strategy.
@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val noteId: Long,
    val prevKey: Int?,
    val nextKey: Int?
)
