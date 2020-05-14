package com.example.psnews.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.psnews.R
import com.example.psnews.databinding.NewsRowBinding
import com.example.psnews.model.News
import com.example.psnews.viewmodel.NewsViewModel

class NewsAdapter(val newsList: ArrayList<News>, val context: Context) :
    RecyclerView.Adapter<NewsAdapter.NewsVH>() {

    lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        layoutInflater = LayoutInflater.from(context)
        var newsRowBinding: NewsRowBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.news_row, parent, false)
        return NewsVH(newsRowBinding)
    }

    override fun onBindViewHolder(holder: NewsVH, position: Int) {
        holder.bindNews(NewsViewModel(newsList[position], context), position)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class NewsVH(itemView: NewsRowBinding) : RecyclerView.ViewHolder(itemView.root) {

        var binding: NewsRowBinding = itemView


        fun bindNews(news: NewsViewModel, position: Int) {
            binding.news = news
            binding.executePendingBindings()

        }
    }

}