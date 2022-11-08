package com.tradeasy.domain.repository

import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister

interface TradeasyRepository {



    suspend fun register(user: UserRegister)
    suspend fun login(user: UserLogin)
}