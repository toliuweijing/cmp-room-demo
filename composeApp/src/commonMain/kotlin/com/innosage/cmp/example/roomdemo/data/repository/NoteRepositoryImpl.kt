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

class NoteRepositoryImpl(
    private val database: NotesDatabase,
    private val apiService: NoteApiService
) : NoteRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getPagedNotes(): Flow<PagingData<Note>> {
        val pagingSourceFactory = { database.noteDao().pagingSource() }

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = NoteRemoteMediator(database, apiService),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomainModel() }
        }
    }
}
