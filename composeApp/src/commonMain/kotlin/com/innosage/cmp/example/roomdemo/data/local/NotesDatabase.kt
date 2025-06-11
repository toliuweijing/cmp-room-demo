package com.innosage.cmp.example.roomdemo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction

@Database(entities = [NoteEntity::class, RemoteKey::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    // TODO: find a right fix for androidx.room.withTransaction not resolved issue.
    suspend fun <R> withTransaction2(block: suspend () -> R): R {
        return this.withTransaction(block)
    }
}
