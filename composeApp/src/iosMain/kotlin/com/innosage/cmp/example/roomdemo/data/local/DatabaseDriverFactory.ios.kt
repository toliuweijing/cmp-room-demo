package com.innosage.cmp.example.roomdemo.data.local

import androidx.room.RoomDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): RoomDatabase.Builder<NotesDatabase> {
        TODO("Not yet implemented")
    }
}
