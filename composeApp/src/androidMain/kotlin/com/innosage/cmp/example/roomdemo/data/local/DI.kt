package com.innosage.cmp.example.roomdemo.data.local

import org.koin.dsl.module

val databaseModule = module {
    single {
        DatabaseDriverFactory(get())
    }
}
