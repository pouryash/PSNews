package com.example.psnews.extentions

import android.content.Context
import android.widget.Toast

fun Context.toast(msg: String, lengh: Int = Toast.LENGTH_LONG){
    Toast.makeText(this, msg, lengh).show()
}