package com.innosage.cmp.example.roomdemo.data.local

import androidx.room.RoomDatabase

expect class DatabaseDriverFactory {
    fun createDriver(): RoomDatabase.Builder<NotesDatabase>
}
