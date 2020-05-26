package com.example.psnews.repository

import com.example.psnews.di.appModule
import com.example.psnews.model.Comment
import com.example.psnews.model.News
import com.example.psnews.model.Response
import com.example.psnews.model.User
import com.example.psnews.network.Api
import com.example.psnews.network.RetrofitBuilder
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    fun isLike(userId:String, newsId:String): Observable<Response<Boolean>>{
        return retrofit.isLike(newsId, userId)
    }

    fun likeAndDisLike(userId:String, newsId:String): Observable<Response<String>>{
        return retrofit.likeAndDisLike(newsId, userId)
    }

    fun insertNews(news: News, image: MultipartBody.Part, type:String): Observable<Response<News>>{
        val uid = RequestBody.create(MediaType.parse("text/plain"), news.userId)
        val title = RequestBody.create(MediaType.parse("text/plain"), news.title)
        val content = RequestBody.create(MediaType.parse("text/plain"), news.content)
        val author = RequestBody.create(MediaType.parse("text/plain"), news.author)
        return retrofit.insertNews(uid, title, content, author, image, type)
    }

    fun getComments(newsId:String): Observable<Response<ArrayList<Comment>>>{
        return retrofit.getComments(newsId)
    }

    fun insertComment(body:String, userId:String, newsId:String): Observable<Response<Comment>>{
        return retrofit.insertComment(body, userId, newsId);
    }

}