package com.innosage.cmp.example.roomdemo.data.remote.api

import kotlinx.coroutines.delay
import com.innosage.cmp.example.roomdemo.data.remote.dto.NoteDto

// This class fakes a remote API service.
class NoteApiService {
    suspend fun getNotes(page: Int, pageSize: Int): List<NoteDto> {
        println("Fetching page: $page")
        delay(1500) // Simulate network latency

        // Simulate having only 5 pages of data
        if (page > 5) {
            println("End of data reached.")
            return emptyList()
        }

        // Generate fake data for the current page
        return (1..pageSize).map { i ->
            val noteId = ((page - 1) * pageSize) + i.toLong()
            NoteDto(id = noteId, title = "Note #$noteId", content = "This is the content for note $noteId fetched from the remote API.")
        }
    }
}
