package com.innosage.cmp.example.roomdemo.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<NoteEntity>)

    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, NoteEntity>

    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKey>)

    @Query("SELECT * FROM remote_keys WHERE noteId = :noteId")
    suspend fun getRemoteKeyByNoteId(noteId: Long): RemoteKey?
    
    @Query("SELECT * FROM remote_keys ORDER BY noteId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): RemoteKey?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()
}
