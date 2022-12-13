package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.VerifyOtpReq
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun execute(req: VerifyOtpReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return tradeasyRepository.verifyOtp(req)
    }
}