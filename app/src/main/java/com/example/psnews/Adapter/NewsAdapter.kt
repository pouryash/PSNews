package com.example.psnews.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.psnews.R
import com.example.psnews.databinding.NewsRowBinding
import com.example.psnews.helper.Constants
import com.example.psnews.model.News
import com.example.psnews.view.NewsPost
import com.example.psnews.viewmodel.MainNewsViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.news_row.*
import kotlinx.android.synthetic.main.news_row.view.*

class NewsAdapter(val newsList: ArrayList<News>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsVH>() {

    lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        layoutInflater = LayoutInflater.from(context)
        val newsRowBinding: NewsRowBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.news_row, parent, false)
        return NewsVH(newsRowBinding)
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

    inner class NewsVH(itemView: NewsRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        var binding: NewsRowBinding = itemView

        fun bindNews(mainNews: MainNewsViewModel) {
            binding.news = mainNews
            binding.executePendingBindings()
        }
    }

   public fun updateNewsLikeCount(likeCount: String, pos: Int) {
        newsList[pos].likeCount = likeCount
        notifyItemChanged(pos)
    }

}