package org.example.project.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteEntity::class, RemoteKey::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}

//expect fun createRoomDatabase(): RoomDatabase.Builder<NotesDatabase>
