package com.example.psnews.repository

import com.example.psnews.di.appModule
import com.example.psnews.model.News
import com.example.psnews.model.Response
import com.example.psnews.model.User
import com.example.psnews.network.Api
import com.example.psnews.network.RetrofitBuilder
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import retrofit2.Retrofit
import rx.Observable

class NewsRepository : KoinComponent{

    private val retrofit: Api by inject()

    fun getNews(): Observable<Response<ArrayList<News>>>{
        return retrofit.getNesws()
    }


}