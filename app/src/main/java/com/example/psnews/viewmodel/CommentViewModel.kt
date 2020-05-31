package com.example.psnews.viewmodel

import android.content.Context
import android.graphics.Bitmap
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
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.psnews.Adapter.CommentAdapter
import com.example.psnews.Adapter.NewsAdapter
import com.example.psnews.BR
import com.example.psnews.R
import com.example.psnews.helper.ErrorHandler
import com.example.psnews.helper.ViewmodelObservable
import com.example.psnews.model.Comment
import com.example.psnews.model.News
import com.example.psnews.network.ApiResponse
import com.example.psnews.repository.NewsRepository
import org.koin.core.KoinComponent
import org.koin.core.inject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class CommentViewModel(val context: Context) : ViewmodelObservable(), KoinComponent {

    val newsRepository: NewsRepository by inject()
    val mutableCommentResponseList: MutableLiveData<ApiResponse<ArrayList<Comment>>> =
        MutableLiveData<ApiResponse<ArrayList<Comment>>>()
    val mutableCommentList: MutableLiveData<ArrayList<Comment>> = MutableLiveData<ArrayList<Comment>>()
    val mutableInsertedComment: MutableLiveData<ApiResponse<Comment>> = MutableLiveData<ApiResponse<Comment>>()

    @get:Bindable
    var name: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.name)
        }

    @get:Bindable
    var comment: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.comment)
        }

    @get:Bindable
    var avatar: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.avatar)
        }


    constructor(
        comment: Comment,
        context: Context
    ) : this(context) {
        this.name = comment.name
        this.comment = comment.body
        this.avatar = comment.avatar
    }

    companion object {

        @JvmStatic
        @BindingAdapter("bind:context", "bind:commentList")
        fun getRecyclerBinder(
            recyclerView: RecyclerView,
            context: Context,
            mutableCommentViewModelList: MutableLiveData<ArrayList<Comment>>
        ) {
            var comment = ArrayList<Comment>()
            val commentAdapter = CommentAdapter(comment, context)
            val layoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
            recyclerView.layoutManager = layoutManager
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = commentAdapter
            recyclerView.setHasFixedSize(true)

            mutableCommentViewModelList.observe(recyclerView.context as LifecycleOwner, Observer {
                comment.clear()
                comment.addAll(it)
                commentAdapter.notifyDataSetChanged()
            })


        }


        @BindingAdapter("bind:imageCommentAvatar")
        @JvmStatic
        fun loadAvatarImage(iv: ImageView, url: String?) {

            Glide.with(iv.context).asBitmap().load(url)
                .apply(
                    RequestOptions().placeholder(R.drawable.ic_person_black_24dp)
                )
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


    fun getNews(newsId: String) {
        newsRepository.getComments(newsId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableCommentResponseList.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    mutableCommentList.postValue(response.data)
                    mutableCommentResponseList.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableCommentResponseList.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

    fun insertComment(body: String, userId: String, newsId: String) {
        newsRepository.insertComment(body, userId, newsId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { mutableInsertedComment.postValue(ApiResponse.loading()) }
            .subscribe(
                { response ->
                    mutableInsertedComment.postValue(
                        ApiResponse.success(response)
                    )
                },
                { error ->

                    mutableInsertedComment.postValue(
                        ApiResponse.error(
                            ErrorHandler.handleThrowable(error)
                        )
                    )
                })
    }

}