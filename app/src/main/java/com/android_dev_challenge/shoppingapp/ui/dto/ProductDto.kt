package com.android_dev_challenge.shoppingapp.ui.dto

import com.android_dev_challenge.shoppingapp.common.ErrorHolder

data class ProductDto(
    val productName: String,
    val designerName: String,
    val currencyCode: String,
    val price: Int,
    val imageUrl: String,
    val imageAltText: String
){
    sealed class Result<out T : Any> {
        data class Success<out T : Any>(val data: List<T>) : Result<T>()
        data class Error(val errorHolder: ErrorHolder) : Result<Nothing>()
    }
}

