package com.tradeasy.domain.user

import com.tradeasy.data.user.remote.dto.*
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface UserRepo {


    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User, WrappedResponse<User>>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User, WrappedResponse<User>>>

    // UPDATE USER PASSWORD
    suspend fun updateUserPassword(req: UpdatePasswordRequest): Flow<BaseResult<User, WrappedResponse<User>>>

    // UPDATE USER USERNAME
    suspend fun updateUsername(req: UpdateUsernameReq): Flow<BaseResult<User, WrappedResponse<User>>>

    //forgot password
    suspend fun forgotPassword(req: ForgotPasswordReq): Flow<BaseResult<String, WrappedResponse<String>>>

    //verify otp
    suspend fun verifyOtp(req: VerifyOtpReq): Flow<BaseResult<String, WrappedResponse<String>>>

    // Reset password
    suspend fun resetPassword(req: ResetPasswordReq): Flow<BaseResult<User, WrappedResponse<User>>>

    // Verify username
    suspend fun verifyUsername(req: UpdateUsernameReq): Flow<BaseResult<String, WrappedResponse<String>>>

    // upload profile picture
    suspend fun uploadProfilePicture(
        image: MultipartBody.Part,
        ): Flow<BaseResult<User, WrappedResponse<User>>>
suspend fun deleteAccount():Flow<BaseResult<String, WrappedResponse<String>>>
suspend fun getUserNotifications(): Flow<BaseResult<List<Notification>, WrappedListResponse<Notification>>>
}
// get user notifications
