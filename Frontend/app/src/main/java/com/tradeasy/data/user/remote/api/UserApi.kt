package com.tradeasy.data.user.remote.api

import com.tradeasy.data.user.remote.dto.*
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>

    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>

    // USER DETAILS API
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordApi(@Body req: UpdatePasswordRequest): Response<WrappedResponse<User>>

    // UPDATE USERNAME API
    @POST("/user/updateUsername")
    suspend fun updateUsernameApi(@Body req: UpdateUsernameReq): Response<WrappedResponse<User>>

    //forgot password
    @POST("/user/forgotPassword")
    suspend fun forgotPasswordAPI(@Body req: ForgotPasswordReq): Response<WrappedResponse<String>>

    //verify otp
    @POST("/user/verifyOtp")
    suspend fun verifyOtpApi(@Body req: VerifyOtpReq): Response<WrappedResponse<String>>

    @POST("/user/resetpassword")
    suspend fun resetPasswordApi(@Body req: ResetPasswordReq): Response<WrappedResponse<User>>

 // Verify Username API
 @POST("/user/verifyusername")
 suspend fun verifyUsernameApi(@Body req: UpdateUsernameReq): Response<WrappedResponse<String>>
}
