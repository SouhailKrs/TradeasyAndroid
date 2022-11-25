package com.tradeasy.data.remote


import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TradeasyApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>
    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>
    // USER DETAILS API
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordAPI(@Body req:UpdatePasswordRequest): Response<WrappedResponse<User>>
}