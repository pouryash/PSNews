package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.psnews.BR
import com.example.psnews.R
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.News
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.NewsRepository
import okhttp3.MultipartBody
import org.koin.core.KoinComponent
import org.koin.core.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class AddNewsViewModel(val context: Context) : ViewmodelObservable(), KoinComponent {

    val newsRepository: NewsRepository by inject()
    val mutableInsertedNewsResponse: MutableLiveData<ApiResponse<News>> =
        MutableLiveData<ApiResponse<News>>()

    @get:Bindable
    var title: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var content: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.content)
        }

    @get:Bindable
    var imageUrl: Bitmap? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUrl)
        }

    companion object{
        @BindingAdapter("bind:imgaeBitmap")
        @JvmStatic
        fun loadInsertedImage(iv: ImageView, bitmap: Bitmap?) {

            Glide.with(iv.context).asBitmap().load(bitmap)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_camera)
                        .transforms(CenterCrop(), RoundedCorners(222)))
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Bitmap?>,
                        isFirstResource: Boolean
                    ): Boolean {
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
                })
                .into(iv)
        }
    }

    fun insertNews(news: News, image: MultipartBody.Part, type:String) {
        newsRepository.insertNews(news, image, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableInsertedNewsResponse.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    mutableInsertedNewsResponse.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableInsertedNewsResponse.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

}