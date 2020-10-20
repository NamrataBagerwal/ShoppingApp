package com.android_dev_challenge.shoppingapp.remote_repository.webservice.products

import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity.ProductApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ProductApi {

    @GET("womens/shop")
    fun getProducts(@Query("format") format: String = "json"): Call<ProductApiResponse>

}