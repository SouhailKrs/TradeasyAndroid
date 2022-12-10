package com.tradeasy.data.remote


import com.tradeasy.domain.model.ForgotPasswordReq
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TradeasyApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>
    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>
    // USER DETAILS API
    @GET("/user/details")
    suspend fun getUserDetailsApi(): Response<WrappedResponse<User>>
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordAPI(@Body req:UpdatePasswordRequest): Response<WrappedResponse<User>>
    //forgot password
    @POST("/user/forgotPassword")
    suspend fun forgotPasswordAPI(@Body req:ForgotPasswordReq): Response<WrappedResponse<User>>
}