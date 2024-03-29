package com.tradeasy.domain.product.usecase


import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentlyAddedProductsUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke() : Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>{
        return productRepo.getRecentlyAddedProducts()
    }
}