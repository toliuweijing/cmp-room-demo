package com.innosage.cmp.example.roomdemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import com.innosage.cmp.example.roomdemo.domain.model.Note
import com.innosage.cmp.example.roomdemo.domain.repository.NoteRepository

class NoteListViewModel(repository: NoteRepository) : ViewModel() {
    val notes: Flow<PagingData<Note>> = repository
        .getPagedNotes()
        .cachedIn(viewModelScope)
}
