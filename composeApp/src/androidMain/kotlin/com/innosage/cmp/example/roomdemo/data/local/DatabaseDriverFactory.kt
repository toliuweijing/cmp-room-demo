package com.innosage.cmp.example.roomdemo.data.local

import org.koin.dsl.module

val androidModule = module {
    single {
        DatabaseDriverFactory(get())
    }
}

//actual fun createRoomDatabase(): RoomDatabase.Builder<NotesDatabase> {
//    val context: Context = get().get()
//    val dbFile = context.getDatabasePath("notes.db")
//    return Room.databaseBuilder<NotesDatabase>(
//        context = context.applicationContext,
//        name = dbFile.absolutePath
//    )
//}
