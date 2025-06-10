package org.example.project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.project.ui.NoteListScreen
import org.example.project.ui.NoteListViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            NoteListScreen()
        }
    }
}
