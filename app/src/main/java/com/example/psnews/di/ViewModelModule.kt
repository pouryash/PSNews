package com.example.psnews.di

import android.content.Context
import com.example.psnews.model.User
import com.example.psnews.viewmodel.AddNewsViewModel
import com.example.psnews.viewmodel.CommentViewModel
import com.example.psnews.viewmodel.MainNewsViewModel
import com.example.psnews.viewmodel.UserViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val viewModelModule = module {


    viewModel(named("b")) {
        UserViewModel(androidContext())
    }
    viewModel(named("a")) { (user: User) ->
        UserViewModel(user, androidContext())
    }

    viewModel { (context: Context) -> MainNewsViewModel(context) }

    viewModel { AddNewsViewModel(androidContext()) }

    viewModel { (context: Context) -> CommentViewModel(context) }

}