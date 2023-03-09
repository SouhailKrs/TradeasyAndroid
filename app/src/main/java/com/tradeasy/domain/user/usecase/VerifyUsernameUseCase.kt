package com.tradeasy.domain.user.usecase


import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerifyUsernameUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req: UpdateUsernameReq): Flow<BaseResult<String,WrappedResponse<String>>> {
        return userRepo.verifyUsername(req)
    }
}
