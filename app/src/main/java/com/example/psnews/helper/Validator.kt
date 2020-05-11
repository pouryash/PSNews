package com.example.psnews.helper

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import com.example.psnews.R
import com.example.psnews.extentions.toast

object Validator {

    fun emailValidator(email: String, context: Context): Boolean {
        var result: Boolean = false
        if (email.toString().trim().isEmpty()) {
            context.toast(context.getString(R.string.enter_all_filed), Toast.LENGTH_SHORT)
            result = false
        } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            result = true
        }else{
            context.toast("invalid email address", Toast.LENGTH_SHORT)
            result = false
        }


        return result
    }

}