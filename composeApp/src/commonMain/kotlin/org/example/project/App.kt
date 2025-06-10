package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import org.example.project.data.local.DatabaseDriverFactory
import org.example.project.data.local.NotesDatabase
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.repository.NoteRepositoryImpl
import org.example.project.domain.repository.NoteRepository
import org.example.project.ui.NoteListScreen
import org.example.project.ui.NoteListViewModel

@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    // Simple manual DI
    val database = remember { NotesDatabase.getDatabase(driverFactory) }
    val apiService = remember { NoteApiService() }
    val repository: NoteRepository = remember { NoteRepositoryImpl(database, apiService) }
    val viewModel = remember { NoteListViewModel(repository) }

    MaterialTheme {
        NoteListScreen(viewModel)
    }
}

// Helper to build the database once
fun NotesDatabase.Companion.getDatabase(driverFactory: DatabaseDriverFactory): NotesDatabase {
    return driverFactory.createDriver().build()
}