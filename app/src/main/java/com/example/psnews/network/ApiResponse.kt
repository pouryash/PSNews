package com.example.psnews.network

import com.example.psnews.model.Response


class ApiResponse<T> private constructor(status: Status, data: Response<T>?, error: String?) {

    lateinit var status: Status

    var data: Response<T>? = null

    var error: String? = null

    init {
        this.error = error
        this.data = data
        this.status = status
    }
    companion object{
        fun<T> loading(): ApiResponse<T> {
            return ApiResponse(Status.LOADING, null, null)
        }

        fun <T> success(data: Response<T>): ApiResponse<T> {
            return ApiResponse(Status.SUCCESS, data, null)
        }

        fun<T> error(error: String): ApiResponse<T> {
            return ApiResponse(Status.ERROR, null ,error)
        }
    }
}