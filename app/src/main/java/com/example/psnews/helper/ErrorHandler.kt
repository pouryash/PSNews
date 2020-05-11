package com.example.psnews.helper

import com.example.psnews.App
import com.example.psnews.R
import com.example.psnews.model.User
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.adapter.rxjava.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ErrorHandler {
    fun handleThrowable(e: Throwable?): String {
        return if (e is IOException) {
            App.context.getString(R.string.user_not_connected_to_internet)
        } else if (e is HttpException) {
            e as HttpException
                return JSONObject(e.response()!!.errorBody()!!.string()).getString("error_msg")
        }else if (e is SocketTimeoutException){
            App.context.getString(R.string.user_not_connected_to_internet)
        } else {
            App.context.getString(R.string.connection_error)
        }
    }


    fun getHttpBody(response: Response<ResponseBody>): JSONObject{

        return JSONObject(response.body()!!.string())
    }
}