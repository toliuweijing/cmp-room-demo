package com.innosage.cmp.example.roomdemo.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.innosage.cmp.example.roomdemo.data.local.NoteEntity
import com.innosage.cmp.example.roomdemo.data.local.NotesDatabase
import com.innosage.cmp.example.roomdemo.data.local.RemoteKey
import com.innosage.cmp.example.roomdemo.data.remote.api.NoteApiService
import com.innosage.cmp.example.roomdemo.data.remote.dto.NoteDto

@OptIn(ExperimentalPagingApi::class)
/**
 * [RemoteMediator] implementation for loading paginated note data from a remote API into a local Room database.
 * This class acts as the bridge between the network and the local cache, ensuring that the local database
 * remains the single source of truth for the Paging library.
 *
 * The `load` method is the core of this mediator, handling different [LoadType] scenarios:
 * - [LoadType.REFRESH]: Triggered on initial load or when the data needs to be invalidated and reloaded.
 *   It clears existing local data and remote keys, then fetches the first page of data from the API.
 * - [LoadType.PREPEND]: Not supported in this implementation, as notes are typically appended. It immediately
 *   returns [MediatorResult.Success] with `endOfPaginationReached = true`.
 * - [LoadType.APPEND]: Triggered when more data is needed at the end of the current list. It retrieves the
 *   `nextKey` from the last [RemoteKey] in the local database to determine the next page to fetch from the API.
 *
 * After fetching data from the API, the mediator performs the following steps within a database transaction:
 * 1. If `REFRESH` load type, it clears all existing notes and remote keys from the local database.
 * 2. Calculates `prevKey` and `nextKey` for the newly fetched page.
 * 3. Maps the fetched [NoteDto] objects to [RemoteKey] and [NoteEntity] objects.
 * 4. Inserts the new remote keys and note entities into the local database.
 * 5. Returns [MediatorResult.Success] with `endOfPaginationReached` indicating if there's more data to load.
 *
 * Error handling is included to catch exceptions during API calls or database operations, returning
 * [MediatorResult.Error] in such cases.
 *
 * @param database The local Room database instance.
 * @param apiService The remote API service for fetching notes.
 */
class NoteRemoteMediator(
    private val database: NotesDatabase,
    private val apiService: NoteApiService
) : RemoteMediator<Int, NoteEntity>() {

    private val noteDao = database.noteDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, NoteEntity>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastRemoteKey = noteDao.getLastRemoteKey()
                    lastRemoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = apiService.getNotes(page = page, pageSize = state.config.pageSize)
            val endOfPaginationReached = response.isEmpty()

            database.withTransaction2 {
                if (loadType == LoadType.REFRESH) {
                    noteDao.clearAllNotes()
                    noteDao.clearAllRemoteKeys()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val remoteKeys = response.map { RemoteKey(noteId = it.id, prevKey = prevKey, nextKey = nextKey) }
                noteDao.insertAllRemoteKeys(remoteKeys)
                noteDao.insertAllNotes(response.map { it.toEntity() })
            }

            MediatorResult.Success(endOfPaginationReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
    
    private fun NoteDto.toEntity() = NoteEntity(id = this.id, title = this.title, content = this.content)
}
