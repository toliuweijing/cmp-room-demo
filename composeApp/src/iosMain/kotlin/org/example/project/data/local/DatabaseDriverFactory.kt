package org.example.project.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import org.example.project.data.local.instantiateImpl
import platform.Foundation.NSHomeDirectory

actual class DatabaseDriverFactory {
    actual fun createDriver(): RoomDatabase.Builder<NotesDatabase> {
        val dbFile = NSHomeDirectory() + "/notes.db"
        return Room.databaseBuilder<NotesDatabase>(
            name = dbFile,
            factory = { NotesDatabase::class.instantiateImpl() }
        )
    }
}