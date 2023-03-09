package com.tradeasy.domain.product.usecase


import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceBidUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(bid: Bid) : Flow<BaseResult<Bid, WrappedResponse<Bid>>> {
        return productRepo.placeBid(bid)
    }
}