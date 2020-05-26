package com.example.psnews.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Comment(

    @Expose
    @SerializedName("id")
    val id: Int,

    @Expose
    @SerializedName("body")
    val body: String,

    @Expose
    @SerializedName("date")
    val date: String,

    @Expose
    @SerializedName("user_id")
    val uid: String,

    @Expose
    @SerializedName("news_id")
    val newsId: String,

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("avatar")
    val avatar: String
    ) {
}