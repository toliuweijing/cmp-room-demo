package com.innosage.cmp.example.roomdemo

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.innosage.cmp.example.roomdemo.ui.NoteListScreen
import com.innosage.cmp.example.roomdemo.ui.NoteListViewModel
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
//            val viewModel: NoteListViewModel = koinViewModel()
            NoteListScreen()
        }
    }
}
