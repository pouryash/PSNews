package com.example.psnews.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class News(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("title")
    val title: String,

    @Expose
    @SerializedName("content")
    val content: String,

    @Expose
    @SerializedName("image_url")
    val imageUrl: String,

    @Expose
    @SerializedName("date")
    val date: String,

    @Expose
    @SerializedName("author")
    val author: String,

    @Expose
    @SerializedName("likes")
    val likeCount: String,

    @Expose
    @SerializedName("user_id")
    val userId: String
) {

    inner class NewsList(
        @Expose
        @SerializedName("data")
        val list: ArrayList<News>
    ){

    }
}