package com.android_dev_challenge.shoppingapp.common.util

import com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity.ProductApiResponse
import com.android_dev_challenge.shoppingapp.ui.dto.ProductDto

object TestData {
    private const val PRODUCT_CODE = "PRODUCT_CODE"
    private const val PRODUCT_NAME = "PRODUCT_NAME"
    private const val PRODUCT_DESIGNER_NAME = "PRODUCT_DESIGNER_NAME"
    private const val PRODUCT_IMAGE_URL = "PRODUCT_IMAGE_URL"
    private const val PRODUCT_IMAGE_URL_ALT_TEXT = "PRODUCT_IMAGE_URL_ALT_TEXT"
    private const val PRODUCT_PRICE_CURRENCY_CODE = "PRODUCT_PRICE_CURRENCY_CODE"
    private const val PRODUCT_FORMATTED_PRICE_WITHOUT_DECIMALS =
        "PRODUCT_FORMATTED_PRICE_WITHOUT_DECIMALS"
    private const val PRODUCT_PRICE_VALUE = 1000
    private const val PRODUCT_URL = "PRODUCT_URL"

    fun getTestProductDtoData(): List<ProductDto> {
        return listOf(
            ProductDto(
                PRODUCT_NAME,
                PRODUCT_DESIGNER_NAME,
                PRODUCT_PRICE_CURRENCY_CODE,
                PRODUCT_PRICE_VALUE,
                PRODUCT_IMAGE_URL,
                PRODUCT_IMAGE_URL_ALT_TEXT
            )
        )
    }

    fun getTestProductApiResponse(): ProductApiResponse {
        return ProductApiResponse(
            listOf<ProductApiResponse.Result>(
                ProductApiResponse.Result(
                    PRODUCT_CODE,
                    ProductApiResponse.Result.Designer(PRODUCT_DESIGNER_NAME),
                    PRODUCT_NAME,
                    ProductApiResponse.Result.Price(
                        PRODUCT_PRICE_CURRENCY_CODE,
                        PRODUCT_FORMATTED_PRICE_WITHOUT_DECIMALS,
                        PRODUCT_PRICE_VALUE
                    ),
                    ProductApiResponse.Result.PrimaryImageMap(
                        ProductApiResponse.Result.PrimaryImageMap.Medium(
                            PRODUCT_IMAGE_URL_ALT_TEXT,
                            PRODUCT_IMAGE_URL
                        )
                    ),
                    PRODUCT_URL
                )
            )
        )
    }
}