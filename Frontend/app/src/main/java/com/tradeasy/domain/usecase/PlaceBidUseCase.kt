package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.Bid
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceBidUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke(bid: Bid) : Flow<BaseResult<Bid, WrappedResponse<Bid>>> {
        return tradeasyRepository.placeBid(bid)
    }
}