package org.example.project.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Note
import org.example.project.domain.repository.NoteRepository

class NoteListViewModel(repository: NoteRepository) : ViewModel() {
    val notes: Flow<PagingData<Note>> = repository
        .getPagedNotes()
        .cachedIn(viewModelScope)
}