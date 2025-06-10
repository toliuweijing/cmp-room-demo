package org.example.project.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Note

interface NoteRepository {
    fun getPagedNotes(): Flow<PagingData<Note>>
}