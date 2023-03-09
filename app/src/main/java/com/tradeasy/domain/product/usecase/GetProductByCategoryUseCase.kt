package com.tradeasy.domain.product.usecase


import com.tradeasy.data.product.remote.dto.GetByCatReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductByCategoryUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(category:GetByCatReq) : Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>{
        return productRepo.getProductByCategory(category)
    }
}
