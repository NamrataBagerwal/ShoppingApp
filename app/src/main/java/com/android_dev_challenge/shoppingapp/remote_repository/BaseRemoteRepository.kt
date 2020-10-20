package com.android_dev_challenge.shoppingapp.remote_repository

import com.android_dev_challenge.shoppingapp.common.ErrorHolder
import retrofit2.Response

open class BaseRemoteRepository(private val errorHandler: ErrorHandler) {

    suspend fun <T : Any> makeApiCall(
        call: suspend () -> Response<T>
    ): Result<T> {
        try {
            val response = call.invoke()
            if (response.isSuccessful) return Result.Success(response.body()!!)
            return Result.Error(errorHandler.handleException(response.code()))
        } catch (throwable: Throwable) {
            return Result.Error(errorHandler.handleException(throwable))
        }
    }

    sealed class Result<out T : Any> {
        data class Success<out T : Any>(val data: T) : Result<T>()
        data class Error(val errorHolder: ErrorHolder) : Result<Nothing>()
    }
}

