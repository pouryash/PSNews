package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.psnews.BR
import com.example.psnews.R
import com.example.psnews.di.appModule
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.User
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.UserRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class UserViewModel(val context: Context) : ViewmodelObservable(), KoinComponent{

    private val repository: UserRepository by inject()
    val userLiveData = MutableLiveData<ApiResponse<User>>()

    var id: String? = null
    @get:Bindable
    var name: String? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.name)
    }
    @get:Bindable
    var email: String? = null
    set(value) {
        field = value
        notifyPropertyChanged(BR.email)
    }
    var userAvatar: String? = null

    constructor(user:User, context: Context): this(context){
        this.name = user.name
        this.email = user.email
        this.id = user.id
        this.userAvatar = user.userAvatar
    }


    companion object{
        @BindingAdapter("bind:imgaeProfileUrl", "bind:context")
        @JvmStatic
        fun loadImage(iv: ImageView, uri: String?, context: Context?) {

            Glide.with(iv.context).asBitmap().load(Uri.parse(uri))
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person_black_200dp)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                )
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .into(iv)
        }
    }


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

    fun updateUser(user: User) {

        repository.updateUser(user)
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