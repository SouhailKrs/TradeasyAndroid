package com.tradeasy.domain.repository

import com.tradeasy.domain.model.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow

interface TradeasyRepository {


    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // USER DETAILS
    suspend fun getUserDetails(): Flow<BaseResult<User,WrappedResponse<User>>>
}