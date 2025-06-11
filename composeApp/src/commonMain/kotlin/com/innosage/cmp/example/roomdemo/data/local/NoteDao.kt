package com.innosage.cmp.example.roomdemo.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object (DAO) for managing [NoteEntity] and [RemoteKey] data in the local Room database.
 * This interface defines the methods for interacting with the `notes` and `remote_keys` tables.
 */
@Dao
interface NoteDao {
    /**
     * Inserts a list of [NoteEntity] objects into the database. If a note with the same primary key
     * already exists, it will be replaced.
     * @param notes The list of [NoteEntity] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotes(notes: List<NoteEntity>)

    /**
     * Provides a [PagingSource] for paginated access to [NoteEntity] objects, ordered by ID in ascending order.
     * This is used by the Paging 3 library to load data incrementally into the UI.
     * @return A [PagingSource] for [NoteEntity].
     */
    @Query("SELECT * FROM notes ORDER BY id ASC")
    fun pagingSource(): PagingSource<Int, NoteEntity>

    /**
     * Deletes all [NoteEntity] objects from the `notes` table. This is typically used during a refresh
     * operation by the [NoteRemoteMediator] to clear old data before inserting new data.
     */
    @Query("DELETE FROM notes")
    suspend fun clearAllNotes()

    /**
     * Inserts a list of [RemoteKey] objects into the database. If a remote key with the same primary key
     * already exists, it will be replaced. These keys are used by [NoteRemoteMediator] to track pagination.
     * @param remoteKeys The list of [RemoteKey] objects to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKey>)

    /**
     * Retrieves a [RemoteKey] associated with a specific note ID.
     * @param noteId The ID of the note for which to retrieve the remote key.
     * @return The [RemoteKey] for the given note ID, or `null` if not found.
     */
    @Query("SELECT * FROM remote_keys WHERE noteId = :noteId")
    suspend fun getRemoteKeyByNoteId(noteId: Long): RemoteKey?
    
    /**
     * Retrieves the last [RemoteKey] inserted into the database, based on the highest note ID.
     * This is used by [NoteRemoteMediator] to determine the `nextKey` for appending more data.
     * @return The last [RemoteKey], or `null` if the table is empty.
     */
    @Query("SELECT * FROM remote_keys ORDER BY noteId DESC LIMIT 1")
    suspend fun getLastRemoteKey(): RemoteKey?

    /**
     * Deletes all [RemoteKey] objects from the `remote_keys` table. This is typically used during a refresh
     * operation by the [NoteRemoteMediator] to clear old remote key data.
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()
}
