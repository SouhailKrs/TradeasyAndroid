package com.tradeasy.domain.user.usecase


import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUsernameUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req: UpdateUsernameReq): Flow<BaseResult<User,WrappedResponse<User>>> {
        return userRepo.updateUsername(req)
    }
}
