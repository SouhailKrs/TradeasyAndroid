package com.tradeasy.domain.repository

<<<<<<< HEAD
import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister
=======
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
>>>>>>> Souhail

interface TradeasyRepository {


<<<<<<< HEAD

    suspend fun register(user: UserRegister)
    suspend fun login(user: UserLogin)
=======
    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // USER DETAILS
    suspend fun getUserDetails(): Flow<BaseResult<User,WrappedResponse<User>>>
    // UPDATE USER PASSWORD
    suspend fun updateUserPassword(req:UpdatePasswordRequest): Flow<BaseResult<User,WrappedResponse<User>>>

>>>>>>> Souhail
}