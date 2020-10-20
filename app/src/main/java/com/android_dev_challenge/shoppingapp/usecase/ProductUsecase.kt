package com.android_dev_challenge.shoppingapp.usecase

import com.android_dev_challenge.shoppingapp.remote_repository.BaseRemoteRepository
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.ProductRepositoryImpl
import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity.ProductApiResponse
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto

class ProductUsecase(private val productRepositoryImpl: BaseRemoteRepository) {

    suspend fun getProducts(): ProductDto.Result<ProductDto> {
        return convertToDto(
            (productRepositoryImpl as ProductRepositoryImpl)
                .getProducts()
        )
    }

    private fun convertToDto(result: BaseRemoteRepository.Result<ProductApiResponse>): ProductDto.Result<ProductDto> {
        return when (result) {
            is BaseRemoteRepository.Result.Success -> {
                val results: List<ProductApiResponse.Result> = result.data.results
                val productDtoList: List<ProductDto> =
                    results.flatMap { productApiResponseResult: ProductApiResponse.Result ->
                        listOf(
                            ProductDto(
                                productApiResponseResult.name,
                                productApiResponseResult.designer.name,
                                productApiResponseResult.price.currencyIso,
                                productApiResponseResult.price.value,
                                productApiResponseResult.primaryImageMap.medium
                                    .url,
                                productApiResponseResult.primaryImageMap.medium.altText
                            )
                        )
                    }
                ProductDto.Result.Success(productDtoList)
            }
            is BaseRemoteRepository.Result.Error -> ProductDto.Result.Error(result.errorHolder)
        }
    }
}