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
    @SerializedName("userAvatar")
    var userAvatar: String,

    @Expose
    @SerializedName("uid")
    var id: String? = null,

    var password: String
) {

    constructor() : this("", "", "", "","")

}