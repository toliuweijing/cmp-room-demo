package org.example.project.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.core.context.GlobalContext.get
import org.koin.dsl.module

val androidModule = module {
    single {
        get<Context>()
    }
}

actual fun createRoomDatabase(): RoomDatabase.Builder<NotesDatabase> {
    val context: Context = get().get()
    val dbFile = context.getDatabasePath("notes.db")
    return Room.databaseBuilder<NotesDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
}