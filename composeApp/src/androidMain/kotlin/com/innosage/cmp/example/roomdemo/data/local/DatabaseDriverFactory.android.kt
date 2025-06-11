package com.innosage.cmp.example.roomdemo.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): RoomDatabase.Builder<NotesDatabase> {
        val dbFile = context.getDatabasePath("notes.db")
        return Room.databaseBuilder<NotesDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath,
        )
    }
}
