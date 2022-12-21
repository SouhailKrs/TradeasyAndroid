package com.tradeasy.domain.product.usecase

import com.tradeasy.data.product.remote.dto.BuyNowReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuyNowUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(req: BuyNowReq) : Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return productRepo.buyNow(req)
    }
}