package com.example.psnews.network

import com.example.psnews.model.News
import com.example.psnews.model.Response
import com.example.psnews.model.User
import retrofit2.http.*
import rx.Observable

interface Api {

    @FormUrlEncoded
    @POST("androidlogin/register")
    fun registerUser(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("name") name: String,
        @Field("avatar") avatar: String
    ): Observable<Response<User>>

    @FormUrlEncoded
    @POST("androidlogin/login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Observable<Response<User>>

    @GET("androidlogin/news")
    fun getNesws(): Observable<Response<ArrayList<News>>>

}