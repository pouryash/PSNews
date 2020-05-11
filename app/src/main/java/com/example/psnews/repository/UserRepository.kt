package com.example.psnews.repository

import com.example.psnews.model.Response
import com.example.psnews.model.User
import com.example.psnews.network.RetrofitBuilder
import rx.Observable

class UserRepository {

    fun registerUser(user: User): Observable<Response<User>>{
        return RetrofitBuilder.apiService.registerUser(user.email, user.password, user.name, user.userAvatar)
    }

    fun loginUser(user: User): Observable<User>{
        return RetrofitBuilder.apiService.loginUser(user.email, user.password)
    }

}