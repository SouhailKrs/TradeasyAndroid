package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.SearchReq
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchProductByNameUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke(name: SearchReq) : Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>{
        return tradeasyRepository.searchProductByName(name)
    }
}