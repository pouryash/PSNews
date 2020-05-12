package com.example.psnews.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.psnews.di.appModule
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.model.User
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.UserRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class UserViewModel : ViewModel(), KoinComponent{

    private val repository: UserRepository by inject()
    val userLiveData = MutableLiveData<ApiResponse<User>>()
    var id: String? = null
    var name: String? = null
    var email: String? = null
    var userAvatar: String? = null


    fun registerUser(user: User) {

        repository.registerUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { userLiveData.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    userLiveData.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    userLiveData.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

    fun loginUser(user: User) {

        repository.loginUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { userLiveData.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    userLiveData.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    userLiveData.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }


}