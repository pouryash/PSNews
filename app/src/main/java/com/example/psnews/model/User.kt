package com.example.psnews.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class User(

    @Expose
    @SerializedName("name")
    var name: String,

    @Expose
    @SerializedName("email")
    var email: String,

    @Expose
    @SerializedName("avatar")
    var userAvatar: String,

    var password: String
) {
    @Expose
    @SerializedName("uid")
    val id: String? = null

    constructor() : this("", "", "", "")

}