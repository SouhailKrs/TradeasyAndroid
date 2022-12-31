package com.tradeasy.data.user.remote.api

import com.tradeasy.data.user.remote.dto.*
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import okhttp3.MultipartBody
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

    // upload profile pic
    @Multipart
    @POST("/user/uploadprofilepicture")
    suspend fun uploadProfilePicApi(
        @Part image: MultipartBody.Part
    ): Response<WrappedResponse<User>>

    // delete account
    @GET("/user/deleteaccount")
    suspend fun deleteAccountApi(): Response<WrappedResponse<String>>

    // get user notifications
    @GET("/user/getnotifications")
    suspend fun getUserNotificationsApi(): Response<WrappedListResponse<Notification>>

    // delete notification api
    @POST("/user/deletenotification")
    suspend fun deleteNotificationApi(@Body req: DeleteNotificationReq): Response<WrappedListResponse<Notification>>
    @GET("/user/logout")
    suspend fun logoutApi(): Response<WrappedResponse<String>>
    @POST("/user/verifyaccount")
    suspend fun verifyAccountApi(@Body req: VerifyAccountReq):  Response<WrappedResponse<User>>

    // send sms to verify account
    @POST("/user/smstoverify")
    suspend fun smsToVerifyApi(): Response<WrappedResponse<String>>


}
