package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Handler
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
import com.bumptech.glide.request.target.ViewTarget
import com.example.psnews.BR
import com.example.psnews.R
import com.example.psnews.di.appModule
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.Response
import com.example.psnews.model.User
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.UserRepository
import kotlinx.android.synthetic.main.activity_profile.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.koin.java.KoinJavaComponent.get
import org.koin.java.KoinJavaComponent.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class UserViewModel(val context: Context) : ViewmodelObservable(), KoinComponent {

    private val repository: UserRepository by inject()
    val userLiveData = MutableLiveData<ApiResponse<User>>()
    val mutableUserUploadImage = MutableLiveData<ApiResponse<User>>()
    val mutableDeleteAvatar = MutableLiveData<ApiResponse<Boolean>>()

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
    @get:Bindable
    var userAvatar: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.userAvatar)
        }

    constructor(user: User, context: Context) : this(context) {
        this.name = user.name
        this.email = user.email
        this.id = user.id
        this.userAvatar = user.userAvatar
    }


    companion object {
        @BindingAdapter("bind:imgaeProfileUrl", "bind:context")
        @JvmStatic
        fun loadImage(iv: ImageView, url: String?, context: Context?) {

            val callBcak = object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any,
                    target: Target<Bitmap?>,
                    isFirstResource: Boolean
                ): Boolean {
                    iv.setColorFilter(
                        context!!.resources.getColor(R.color.colorAccent),
                        PorterDuff.Mode.SRC_IN
                    )
                    Handler().postDelayed(Runnable {
                        Glide.with(context).clear(target)
                    }, 1000)
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any,
                    target: Target<Bitmap?>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            }

            val target = Glide.with(iv.context).asBitmap().load(url)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person_black_200dp)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
                .listener(callBcak)
                .into(iv)


        }
    }

    @Synchronized
    private fun clearGlideTarget(target: ViewTarget<ImageView, Bitmap>) {
        Glide.with(context).clear(target)
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

    fun updateAvatar(uid: RequestBody, image: MultipartBody.Part, opr: String) {

        repository.uploadAvatar(uid, image, opr)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableUserUploadImage.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    mutableUserUploadImage.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->
                    mutableUserUploadImage.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

    fun deleteAvatar(uid: RequestBody, opr: String) {

        repository.uploadAvatar(uid, MultipartBody.Part.createFormData("", ""), opr)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableDeleteAvatar.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                   response?.let {
                       mutableDeleteAvatar.postValue(
                           ApiResponse.success(Response(true,response.message))
                       )
                   }
                },
                { error ->
                    mutableDeleteAvatar.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

}