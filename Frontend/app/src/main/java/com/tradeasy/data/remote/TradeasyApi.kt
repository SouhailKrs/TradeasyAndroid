package com.tradeasy.data.remote


import com.tradeasy.domain.model.User
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TradeasyApi {

    //Sign up
    @POST("/user/register")
    suspend fun registerApi(@Body user: User): Response<User>
    //Sign in
    @POST("/user/login")
    suspend fun loginApi(@Body user: User): Response<User>
    // User details
    @GET("/user/details")
    suspend fun getUserDetailsApi(): Response<User>
}