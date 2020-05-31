package com.example.psnews.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.psnews.model.User
import org.json.JSONObject

class SharedPrefrenceManager(context: Context) {

    private val sharedPrefrences: SharedPreferences =
        context.getSharedPreferences(Constants.USER_SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun saveUser(user: User) {

        val editor: SharedPreferences.Editor = sharedPrefrences.edit()
        val jsonObject = JSONObject()

        jsonObject.put("id", user.id)
        jsonObject.put("name", user.name)
        jsonObject.put("email", user.email)
        jsonObject.put("avatar", user.userAvatar)

        editor.putString(Constants.KEY_USER_MODEL, jsonObject.toString())
        editor.apply()

    }

    fun getUser(): User {
        val user: User = User()
        val jsonModel = sharedPrefrences.getString(Constants.KEY_USER_MODEL, null)

        if (!jsonModel.isNullOrEmpty()) {
            val jsonObject = JSONObject(jsonModel)
            user.id = jsonObject.getString("id")
            user.name = jsonObject.getString("name")
            user.email = jsonObject.getString("email")
            user.userAvatar = jsonObject.getString("avatar")
        }

        return user
    }

    fun clearUserSession() {
        val editor: SharedPreferences.Editor = sharedPrefrences.edit()
        editor.putString(Constants.KEY_USER_MODEL, "")
        editor.apply()
    }
}