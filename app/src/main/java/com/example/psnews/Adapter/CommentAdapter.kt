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
import com.example.psnews.databinding.CommentRowBinding
import com.example.psnews.databinding.NewsArticleRowBinding
import com.example.psnews.databinding.NewsRowBinding
import com.example.psnews.helper.Constants
import com.example.psnews.model.Comment
import com.example.psnews.model.News
import com.example.psnews.view.NewsPost
import com.example.psnews.viewmodel.CommentViewModel
import com.example.psnews.viewmodel.MainNewsViewModel
import java.util.stream.Collectors

class CommentAdapter(val commentList: ArrayList<Comment>, val context: Context) :
    RecyclerView.Adapter<CommentAdapter.CommentVH>() {

    lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentVH {
        layoutInflater = LayoutInflater.from(context)
        val newsRowBinding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.comment_row, parent, false)

        return CommentVH(newsRowBinding)
    }

    override fun onBindViewHolder(holder: CommentVH, position: Int) {
        val viewModel = CommentViewModel(commentList[position], context)
        holder.bindNews(viewModel)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class CommentVH(itemView: ViewDataBinding) : RecyclerView.ViewHolder(itemView.root) {

        var binding: CommentRowBinding = (itemView as CommentRowBinding)

        fun bindNews(mainComment: CommentViewModel) {
            binding.commentViewModel = mainComment
            binding.executePendingBindings()
        }
    }

    public fun insertComment(comment: Comment) {
        commentList.add(0,comment)
        notifyItemInserted(0)
    }
}
