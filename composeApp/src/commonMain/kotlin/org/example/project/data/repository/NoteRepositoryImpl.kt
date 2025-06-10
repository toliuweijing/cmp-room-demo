package org.example.project.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.data.local.NotesDatabase
import org.example.project.data.paging.NoteRemoteMediator
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.toDomainModel
import org.example.project.domain.model.Note
import org.example.project.domain.repository.NoteRepository

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