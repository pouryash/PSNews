package com.example.psnews.network

import com.example.psnews.model.News
import com.example.psnews.model.Response
import com.example.psnews.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @FormUrlEncoded
    @POST("androidlogin/UpdateUser")
    fun updateUser(
        @Field("email") email: String,
        @Field("name") name: String,
        @Field("uid") userid: String
    ): Observable<Response<User>>

    @GET("androidlogin/isLike")
    fun isLike(
        @Query("newsId") name: String,
        @Query("uid") userid: String
    ): Observable<Response<Boolean>>

    @FormUrlEncoded
    @POST("androidlogin/addOrRemoveLike")
    fun likeAndDisLike(
        @Field("newsId") name: String,
        @Field("uid") userid: String
    ): Observable<Response<String>>

    @Multipart
    @POST("androidlogin/UploadAvatar")
    fun uploadAvatar(
        @Part("uid") uid: RequestBody,
        @Part image: MultipartBody.Part,
        @Query("opr") opr: String
    ): Observable<Response<User>>

    @GET("androidlogin/news")
    fun getNesws(): Observable<Response<ArrayList<News>>>

}