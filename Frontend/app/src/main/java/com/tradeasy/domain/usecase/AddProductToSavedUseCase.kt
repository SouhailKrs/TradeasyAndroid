package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.AddToSavedReq
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddProductToSavedUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke(req: AddToSavedReq) : Flow<BaseResult<User, WrappedResponse<User>>> {
        return tradeasyRepository.addProductToSaved(req)
    }
}