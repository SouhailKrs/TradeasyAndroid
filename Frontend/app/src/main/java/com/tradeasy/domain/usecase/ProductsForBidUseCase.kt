package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.Product
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsForBid @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke() : Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>{
        return tradeasyRepository.getProductsForBid()
    }
}