package com.tradeasy.data.remote


import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TradeasyApi {

    //Sign up
    @POST("/user/register")
    suspend fun register(@Body user: UserRegister): Response<UserRegister>
    //Sign in
    @POST("/user/login")
    suspend fun login(@Body user: UserLogin): Response<UserRegister>
}