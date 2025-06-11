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

            database.withTransaction {
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
