package com.innosage.cmp.example.roomdemo

import android.app.Application
import com.innosage.cmp.example.roomdemo.data.local.androidModule
import com.innosage.cmp.example.roomdemo.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger()
            modules(appModule, androidModule)
        }
    }
}
