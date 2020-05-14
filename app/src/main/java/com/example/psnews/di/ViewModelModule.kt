package com.example.psnews.di

import com.example.psnews.viewmodel.NewsViewModel
import com.example.psnews.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {


    viewModel { UserViewModel() }

    viewModel { NewsViewModel(androidContext()) }

}