package com.innosage.cmp.example.roomdemo.domain.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.innosage.cmp.example.roomdemo.domain.model.Note

interface NoteRepository {
    fun getPagedNotes(): Flow<PagingData<Note>>
}
