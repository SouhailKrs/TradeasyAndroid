package com.tradeasy.domain.repository

import com.tradeasy.domain.model.User
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow

interface TradeasyRepository {
    suspend fun register(user: User)

    suspend fun login(user: User):Flow<BaseResult<User>>

    suspend fun getUserDetails(): Flow<BaseResult<User>>
}