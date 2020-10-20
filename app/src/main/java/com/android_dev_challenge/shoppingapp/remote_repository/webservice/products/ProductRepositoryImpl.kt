package com.android_dev_challenge.shoppingapp.remote_repository.webservice.products
import com.android_dev_challenge.shoppingapp.remote_repository.BaseRemoteRepository
import com.android_dev_challenge.shoppingapp.remote_repository.ErrorHandler
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity.ProductApiResponse
import retrofit2.awaitResponse


class ProductRepositoryImpl(private val productApi: ProductApi, errorHandler: ErrorHandler) : BaseRemoteRepository(errorHandler) {

    suspend fun getProducts(): Result<ProductApiResponse> {
        return makeApiCall(
            call = {
                productApi.getProducts().awaitResponse()
            }
        )
    }
}