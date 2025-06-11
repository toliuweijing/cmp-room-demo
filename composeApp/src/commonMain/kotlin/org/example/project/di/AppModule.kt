package org.example.project.di

import org.example.project.data.local.DatabaseDriverFactory
import org.example.project.data.local.NotesDatabase
import org.example.project.data.remote.api.NoteApiService
import org.example.project.data.repository.NoteRepositoryImpl
import org.example.project.domain.repository.NoteRepository
import org.example.project.ui.NoteListViewModel
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
