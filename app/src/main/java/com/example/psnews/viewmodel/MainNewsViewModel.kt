package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.psnews.Adapter.NewsAdapter
import com.example.psnews.BR
import com.example.psnews.R
import com.example.psnews.helper.Constants
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.News
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.NewsRepository
import kotlinx.android.parcel.Parcelize
import org.koin.core.KoinComponent
import org.koin.core.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainNewsViewModel(val context: Context) : ViewmodelObservable(), KoinComponent {

    val newsRepository: NewsRepository by inject()
    val mutableNewsResponseList: MutableLiveData<ApiResponse<ArrayList<News>>> =
        MutableLiveData<ApiResponse<ArrayList<News>>>()
    val mutableNewsList: MutableLiveData<ArrayList<News>> = MutableLiveData<ArrayList<News>>()
    val mutableIsNewsLiked: MutableLiveData<ApiResponse<Boolean>> =
        MutableLiveData<ApiResponse<Boolean>>()
    val mutableLikeAndDissLike: MutableLiveData<ApiResponse<String>> =
        MutableLiveData<ApiResponse<String>>()


    var newsId: String? = ""

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
    var imageUrl: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageUrl)
        }

    @get:Bindable
    var date: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.date)
        }

    @get:Bindable
    var author: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.author)
        }

    @get:Bindable
    var likeCount: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.likeCount)
        }
    var userId: String = ""

    @get:Bindable
    var profileAvatar: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.profileAvatar)
        }

    @get:Bindable
    var liked: Boolean = false
        set(value) {
            field = value
            notifyPropertyChanged(BR.liked)
        }


    constructor(
        news: News,
        context: Context
    ) : this(context) {
        this.newsId = news.id
        this.title = news.title
        this.content = news.content
        this.imageUrl = news.imageUrl
        this.date = news.date
        this.author = news.author
        this.likeCount = news.likeCount
        this.userId = news.userId
    }

    companion object {

        @JvmStatic
        @BindingAdapter("bind:context", "bind:newsList")
        fun getRecyclerBinder(
            recyclerView: RecyclerView,
            context: Context,
            mutableSongViewModelList: MutableLiveData<ArrayList<News>>
        ) {
            var news = ArrayList<News>()
            val newsAdapter = NewsAdapter(news, context)
            recyclerView.layoutManager =
                LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = newsAdapter
            recyclerView.setHasFixedSize(true)

            mutableSongViewModelList.observe(recyclerView.context as LifecycleOwner, Observer {
                news.clear()
                news.addAll(it)
                newsAdapter.notifyDataSetChanged()
            })


        }

        @BindingAdapter("bind:imgaeUrl", "bind:context")
        @JvmStatic
        fun loadImage(iv: ImageView, uri: String?, context: Context?) {

            uri?.let {
                Glide.with(iv.context).asBitmap().load(Uri.parse(uri))
                    .apply(
                        RequestOptions().placeholder(R.drawable.ic_camera)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    )
                    .listener(object : RequestListener<Bitmap?> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any,
                            target: Target<Bitmap?>,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv.scaleType = ImageView.ScaleType.CENTER
                            iv.contentDescription = Constants.IMAGE_FAILD
                            return false
                        }

                        override fun onResourceReady(
                            resource: Bitmap?,
                            model: Any,
                            target: Target<Bitmap?>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            iv.scaleType = ImageView.ScaleType.CENTER_CROP
                            iv.contentDescription = Constants.IMAGE_SUCCESS
                            return false
                        }
                    })
                    .into(iv)
            }

        }

        @BindingAdapter("bind:imgaeUrl")
        @JvmStatic
        fun loadAvatarImage(iv: ImageView, uri: String?) {

            Glide.with(iv.context).asBitmap().load(Uri.parse(uri))
                .apply(RequestOptions().placeholder(R.drawable.ic_person_white_24dp)
                    .diskCacheStrategy(DiskCacheStrategy.NONE))
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any,
                        target: Target<Bitmap?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        iv.setPadding(4, 4, 4, 4)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any,
                        target: Target<Bitmap?>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        iv.setPadding(0, 0, 0, 0)
                        return false
                    }
                })
                .into(iv)
        }
    }


    fun getNews() {
        newsRepository.getNews()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableNewsResponseList.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    mutableNewsList.postValue(response.data)
                    mutableNewsResponseList.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableNewsResponseList.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

    fun isLike(userId: String, newsId: String) {
        newsRepository.isLike(userId, newsId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableIsNewsLiked.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    val a = response.data
                    mutableIsNewsLiked.postValue(ApiResponse.success(response))
                    mutableIsNewsLiked.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableIsNewsLiked.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

    fun likeAndDisLike(userId: String, newsId: String) {
        newsRepository.likeAndDisLike(userId, newsId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableLikeAndDissLike.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    val a = response.data
                    mutableLikeAndDissLike.postValue(ApiResponse.success(response))
                    mutableLikeAndDissLike.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableLikeAndDissLike.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

}