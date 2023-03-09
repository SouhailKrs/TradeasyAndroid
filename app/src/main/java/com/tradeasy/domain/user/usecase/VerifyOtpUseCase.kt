package com.tradeasy.domain.user.usecase


import com.tradeasy.data.user.remote.dto.VerifyOtpReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req: VerifyOtpReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return userRepo.verifyOtp(req)
    }
}