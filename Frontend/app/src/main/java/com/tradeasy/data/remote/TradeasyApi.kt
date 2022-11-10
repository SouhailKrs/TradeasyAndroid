package com.tradeasy.data.remote


import com.tradeasy.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TradeasyApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<User>
    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<User>
    // USER DETAILS API
    @GET("/user/details")
    suspend fun getUserDetailsApi(): Response<User>
}