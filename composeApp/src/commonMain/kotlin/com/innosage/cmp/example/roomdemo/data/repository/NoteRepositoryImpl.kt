package com.innosage.cmp.example.roomdemo.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.innosage.cmp.example.roomdemo.data.local.NotesDatabase
import com.innosage.cmp.example.roomdemo.data.paging.NoteRemoteMediator
import com.innosage.cmp.example.roomdemo.data.remote.api.NoteApiService
import com.innosage.cmp.example.roomdemo.data.toDomainModel
import com.innosage.cmp.example.roomdemo.domain.model.Note
import com.innosage.cmp.example.roomdemo.domain.repository.NoteRepository

/**
 * Implementation of [NoteRepository] that handles data loading from both local and remote sources.
 * This class orchestrates the data flow using AndroidX Paging 3 library, combining data from
 * a Room database and a network API.
 *
 * The data loading flow works as follows:
 * 1. When [getPagedNotes] is called, a [Pager] is configured with a [PagingConfig], a [NoteRemoteMediator],
 *    and a [PagingSource] from the local database.
 * 2. The [PagingSource] (obtained from [NotesDatabase.noteDao]) provides the initial data from the local cache.
 * 3. The [NoteRemoteMediator] is responsible for fetching data from the [NoteApiService] (remote source)
 *    when the local data runs out or needs to be refreshed.
 * 4. When the [NoteRemoteMediator] fetches new data from the API, it stores this data in the local database.
 *    This ensures that the local database always acts as the single source of truth.
 * 5. The [PagingSource] then observes changes in the local database and emits new [PagingData] to the UI.
 *    This means the UI always displays data from the local database, which is kept up-to-date by the
 *    [NoteRemoteMediator].
 * 6. The `map` operation on the [Flow<PagingData<NoteEntity>>] transforms [NoteEntity] objects
 *    from the database into [Note] domain models before they are exposed to the UI layer.
 *
 * @param database The local Room database instance.
 * @param apiService The remote API service for fetching notes.
 */
class NoteRepositoryImpl(
    private val database: NotesDatabase,
    private val apiService: NoteApiService
) : NoteRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedNotes(): Flow<PagingData<Note>> {
        val pagingSourceFactory = {
            database.noteDao().pagingSource()
        }

        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false,
                prefetchDistance = 1,
                initialLoadSize = 5,
            ),
            remoteMediator = NoteRemoteMediator(database, apiService),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }
}
