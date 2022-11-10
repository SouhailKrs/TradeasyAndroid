package com.tradeasy.domain.repository

import com.tradeasy.domain.model.User
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow

interface TradeasyRepository {


    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User>>

    // USER DETAILS
    suspend fun getUserDetails(): Flow<BaseResult<User>>
}