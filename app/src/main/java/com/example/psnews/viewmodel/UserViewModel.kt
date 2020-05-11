package com.example.psnews.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.model.User
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.UserRepository
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class UserViewModel : ViewModel() {

    private val repository: UserRepository = UserRepository()
    val registerLiveData = MutableLiveData<ApiResponse<User>>()

    //    val loginLiveData = MutableLiveData<ApiResponse<RequestResult>>()
    var id: String? = null
    var name: String? = null
    var email: String? = null
    var userAvatar: String? = null


    fun registerUser(user: User) {

        repository.registerUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { registerLiveData.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    registerLiveData.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    registerLiveData.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

//    fun loginUser(user: User) {
//
//        repository.loginUser(user)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnSubscribe { loginLiveData.postValue(ApiResponse.loading()) }
//            .subscribe(
//                { response ->
//                    loginLiveData.postValue(ApiResponse.success(
//                        RequestResult(false,200,"login successfuly done")
//                    ))
//                },
//                { error ->
//                    val jsonObject: JSONObject = ErrorHandler.getHttpExBody(error)
//
//                    loginLiveData.postValue(
//                        ApiResponse.error(
//                            error,
//                            RequestResult(
//                                true,
//                                jsonObject.getInt("status"),
//                                jsonObject.getString("error_msg")
//                            )
//                        )
//                    )
//                })
//    }


}