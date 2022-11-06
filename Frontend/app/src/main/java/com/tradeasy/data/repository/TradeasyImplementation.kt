package com.tradeasy.data.repository

import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister
import com.tradeasy.domain.repository.TradeasyRepository
import javax.inject.Inject

class TradeasyImplementation @Inject constructor( private val api: TradeasyApi) : TradeasyRepository {
    override suspend fun register(user: UserRegister) {
        val response = api.register(user)
        if (response.isSuccessful) {
            response.body()?.let {
                println("Success")
            }
        } else {
            println("Error")
        }
    }

  // login
    override suspend fun login(user: UserLogin) {
        val response = api.login(user)
        if (response.isSuccessful) {
            response.body()?.let {
                println("Success")
            }
        } else {
            println("Error")
        }
    }

}