package com.example.psnews.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("email")
    val email: String,

    @Expose
    @SerializedName("avatar")
    val userAvatar: String,

    val password: String
) {
    @Expose
    @SerializedName("uid")
    val id: String? = null


}