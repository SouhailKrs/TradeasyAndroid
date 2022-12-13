package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.UpdateUsernameReq
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun execute(req:UpdateUsernameReq): Flow<BaseResult<User,WrappedResponse<User>>> {
        return tradeasyRepository.updateUsername(req)
    }
}
