package com.tradeasy.domain.user.usecase

import com.tradeasy.data.user.remote.dto.ForgotPasswordReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ForgotPasswordUseCase  @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req: ForgotPasswordReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return userRepo.forgotPassword(req)
    }
}