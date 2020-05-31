package com.example.psnews

import android.app.Application
import android.content.Context
import com.example.psnews.di.appModule
import com.example.psnews.di.retrofitModlue
import com.example.psnews.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.koinApplication
import org.koin.dsl.module

class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        startKoin {
            // Koin Android logger
            androidLogger(Level.DEBUG)

            environmentProperties()

            //inject Android context
            androidContext(this@App)

            modules(listOf(retrofitModlue, viewModelModule, appModule))
        }

    }
}