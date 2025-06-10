package org.example.project.data.local

import androidx.room.RoomDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): RoomDatabase.Builder<NotesDatabase>
}