package com.example.psnews.di

import com.example.psnews.helper.Constants
import com.example.psnews.network.Api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val retrofitModlue = module {

    single {
        okHttpClint()
    }

    single {

        retrofit(Constants.BASE_URL, get())
    }

    single {
        get<Retrofit>().create(Api::class.java)
    }

}

private fun okHttpClint():OkHttpClient {

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    return OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

}

private fun retrofit(baseUrl: String, okHttpClient: OkHttpClient) = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())
    .build()
