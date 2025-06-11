package com.innosage.cmp.example.roomdemo.di

import com.innosage.cmp.example.roomdemo.data.local.DatabaseDriverFactory
import com.innosage.cmp.example.roomdemo.data.local.NotesDatabase
import com.innosage.cmp.example.roomdemo.data.remote.api.NoteApiService
import com.innosage.cmp.example.roomdemo.data.repository.NoteRepositoryImpl
import com.innosage.cmp.example.roomdemo.domain.repository.NoteRepository
import com.innosage.cmp.example.roomdemo.ui.NoteListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<NotesDatabase> {
        get<DatabaseDriverFactory>().createDriver().build()
    }

    factory { get<NotesDatabase>().noteDao() }

    single { NoteApiService() }
    single<NoteRepository> { NoteRepositoryImpl(get(), get()) }

    viewModel { NoteListViewModel(get()) }
}
