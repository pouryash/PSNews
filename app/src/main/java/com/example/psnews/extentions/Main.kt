package com.example.psnews.extentions

import android.content.Context
import android.widget.Toast

var toast: Toast? = null


fun Context.toast(msg: String, length: Int = Toast.LENGTH_LONG) {

    toast = if (toast == null) {
        Toast.makeText(this, msg, length)
    } else {
        toast!!.cancel()
        Toast.makeText(this, msg, length)
    }

    toast!!.show()

}