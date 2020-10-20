package com.android_dev_challenge.shoppingapp.remote_repository

import com.android_dev_challenge.shoppingapp.common.AppConstants.ERROR_MSG_FETCHING_DATA
import com.android_dev_challenge.shoppingapp.common.AppConstants.ERROR_MSG_INTERNET_CONNECTION_UNAVAILABLE
import com.android_dev_challenge.shoppingapp.common.AppConstants.ERROR_MSG_RESOURCE_NOT_FOUND
import com.android_dev_challenge.shoppingapp.common.ErrorHolder
import retrofit2.HttpException
import java.io.IOException

class ErrorHandler {

    enum class ErrorCodes(val code: Int) {
        BAD_REQUEST(400),
        INTERNAL_SERVER(500),
        UNAUTHORIZED(403),
        RESOURCE_NOT_FOUND(404)
    }

    fun handleException(ex: Throwable): ErrorHolder {
        return when (ex) {
            is IOException -> {
                ErrorHolder.NetworkConnection(
                    ERROR_MSG_INTERNET_CONNECTION_UNAVAILABLE
                )
            }
            is HttpException -> extractHttpExceptions(ex.code())
            else -> ErrorHolder.UnexpectedException(ERROR_MSG_FETCHING_DATA)
        }
    }

    fun handleException(code: Int): ErrorHolder = extractHttpExceptions(code)

    private fun extractHttpExceptions(code: Int): ErrorHolder {
        return when (code) {
            ErrorCodes.BAD_REQUEST.code ->
                ErrorHolder.BadRequest(ERROR_MSG_FETCHING_DATA)

            ErrorCodes.INTERNAL_SERVER.code ->
                ErrorHolder.InternalServerError(ERROR_MSG_FETCHING_DATA)

            ErrorCodes.UNAUTHORIZED.code ->
                ErrorHolder.UnAuthorized(ERROR_MSG_FETCHING_DATA)

            ErrorCodes.RESOURCE_NOT_FOUND.code ->
                ErrorHolder.ResourceNotFound(ERROR_MSG_RESOURCE_NOT_FOUND)

            else ->
                ErrorHolder.UnexpectedException(ERROR_MSG_FETCHING_DATA)

        }
    }
}