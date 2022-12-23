package com.tradeasy.data.category.remote.api

import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.WrappedListResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {

    @GET("/category/getcategories")
    suspend fun getCategoriesApi(): Response<WrappedListResponse<Product>>
}
