package com.example.psnews.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Response<T>(

    @Expose
    @SerializedName("data")
    val data: T,

    @Expose
    @SerializedName("error_msg")
    val message: String

) {
}