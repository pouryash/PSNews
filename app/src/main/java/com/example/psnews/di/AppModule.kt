package com.example.psnews.di

import com.example.psnews.helper.SharedPrefrenceManager
import com.example.psnews.repository.NewsRepository
import com.example.psnews.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single {
        UserRepository()
    }

    single {
        NewsRepository()
    }

    single { SharedPrefrenceManager(androidContext()) }

}