package com.example.psnews.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.psnews.R
import com.example.psnews.databinding.NewsArticleRowBinding
import com.example.psnews.databinding.NewsRowBinding
import com.example.psnews.helper.Constants
import com.example.psnews.model.News
import com.example.psnews.view.NewsPost
import com.example.psnews.viewmodel.MainNewsViewModel

class NewsAdapter(val newsList: ArrayList<News>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsVH>() {

    private val TYPE_ARTICLE = 0
    private val TYPE_MAIN = 1
    lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        layoutInflater = LayoutInflater.from(context)
        var newsRowBinding: ViewDataBinding? = null

        if (viewType == TYPE_MAIN) {
            newsRowBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.news_row, parent, false)
        } else if (viewType == TYPE_ARTICLE) {
            newsRowBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.news_article_row, parent, false)
        }

        return NewsVH(newsRowBinding!!)
    }

    override fun onBindViewHolder(holder: NewsVH, position: Int) {
        val viewModel = MainNewsViewModel(newsList[position], context)
        holder.bindNews(viewModel)

        holder.itemView.setOnClickListener(View.OnClickListener {
            context as Activity
            var intent: Intent = Intent(context, NewsPost::class.java)
            intent.putExtra("news", newsList[position])
            intent.putExtra("pos", position)
            context.startActivityForResult(intent, Constants.NEWS_REQUEST_ID)
        })
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (newsList[position].imageUrl == "")
            TYPE_ARTICLE
        else
            TYPE_MAIN
    }

    inner class NewsVH(itemView: ViewDataBinding) : RecyclerView.ViewHolder(itemView.root) {


        var binding: ViewDataBinding = itemView

        fun bindNews(mainNews: MainNewsViewModel) {
            if (binding is NewsRowBinding) {
                (binding as NewsRowBinding).news = mainNews
                binding.executePendingBindings()
            }else{
                (binding as NewsArticleRowBinding).news = mainNews
                binding.executePendingBindings()
            }
        }
    }

    public fun updateNewsLikeCount(likeCount: String, pos: Int) {
        newsList[pos].likeCount = likeCount
        notifyItemChanged(pos)
    }
}
