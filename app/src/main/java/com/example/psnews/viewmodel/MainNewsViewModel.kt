package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.library.baseAdapters.BR
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
import com.example.psnews.R
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.News
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.NewsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainNewsViewModel(val context: Context) : ViewmodelObservable(), KoinComponent {

    val newsRepository: NewsRepository by inject()
    val mutableNewsResponseList: MutableLiveData<ApiResponse<ArrayList<News>>> =
        MutableLiveData<ApiResponse<ArrayList<News>>>()
    val mutableNewsList: MutableLiveData<ArrayList<News>> = MutableLiveData<ArrayList<News>>()

    var title: String = ""
    var content: String = ""
    var imageUrl: String = ""
    var date: String = ""
    var author: String = ""
    var likeCount: String = ""
    var userId: String = ""
    @get:Bindable
    var profileAvatar:String = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.profileAvatar)
    }

    constructor(
        news: News,
        context: Context
    ) : this(context) {
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

            Glide.with(iv.context).asBitmap().load(Uri.parse(uri))
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_camera)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                )
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                        iv.scaleType = ImageView.ScaleType.CENTER
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        iv.scaleType = ImageView.ScaleType.CENTER_CROP
                        return false
                    }
                })
                .into(iv)
        }

        @BindingAdapter("bind:imgaeUrl")
        @JvmStatic
        fun loadAvatarImage(iv: ImageView, uri: String?) {

            Glide.with(iv.context).asBitmap().load(Uri.parse(uri))
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person_white_24dp)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                )
                .listener(object : RequestListener<Bitmap?> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Bitmap?>, isFirstResource: Boolean): Boolean {
                        iv.setPadding(4,4,4,4)
                        return false
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any, target: Target<Bitmap?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        iv.setPadding(0,0,0,0)
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

}