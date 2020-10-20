package com.android_dev_challenge.shoppingapp.remote_repository.webservice.products.entity

data class ProductApiResponse(
    val results: List<Result>
) {
    data class Result(
        val code: String,
        val designer: Designer,
        val name: String,
        val price: Price,
        val primaryImageMap: PrimaryImageMap,
        val url: String
    ) {
        data class Designer(
            val name: String
        )

        data class Price(
            val currencyIso: String,
            val formattedPriceWithoutDecimals: String,
            val value: Int
        )

        data class PrimaryImageMap(
            val medium: Medium
        ) {
            data class Medium(
                val altText: String,
                val url: String
            )
        }
    }
}