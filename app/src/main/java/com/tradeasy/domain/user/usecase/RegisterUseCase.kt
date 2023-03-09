package com.tradeasy.domain.user.usecase


import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase@Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(user: User): Flow<BaseResult<User,WrappedResponse<User>>> {
        return userRepo.userRegister(user)
    }
}
