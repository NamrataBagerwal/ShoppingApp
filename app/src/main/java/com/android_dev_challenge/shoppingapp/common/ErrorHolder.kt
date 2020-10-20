package com.android_dev_challenge.shoppingapp.common

sealed class ErrorHolder(override val message: String) : Throwable(message) {
    data class NetworkConnection(override val message: String) : ErrorHolder(message)
    data class BadRequest(override val message: String) : ErrorHolder(message)
    data class UnAuthorized(override val message: String) : ErrorHolder(message)
    data class InternalServerError(override val message: String) : ErrorHolder(message)
    data class ResourceNotFound(override val message: String) : ErrorHolder(message)
    data class UnexpectedException(override val message: String) : ErrorHolder(message)
}