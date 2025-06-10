package org.example.project.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import org.example.project.domain.model.Note

@Composable
fun NoteListScreen(viewModel: NoteListViewModel) {
    val lazyPagingItems = viewModel.notes.collectAsLazyPagingItems()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                val note = lazyPagingItems[index]
                if (note != null) {
                    NoteItem(note)
                }
            }

            when (val refreshState = lazyPagingItems.loadState.refresh) {
                is LoadState.Loading -> item { FullScreenLoading() }
                is LoadState.Error -> item { ErrorItem("Error refreshing: ${refreshState.error.message}") }
                else -> {}
            }

            when (val appendState = lazyPagingItems.loadState.append) {
                is LoadState.Loading -> item { CenteredLoadingIndicator() }
                is LoadState.Error -> item { ErrorItem("Error appending: ${appendState.error.message}") }
                else -> {}
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(note.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(note.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FullScreenLoading() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun CenteredLoadingIndicator() {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorItem(message: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.Center) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}