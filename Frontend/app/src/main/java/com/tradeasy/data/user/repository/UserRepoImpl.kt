package com.tradeasy.data.user.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.user.remote.api.UserApi
import com.tradeasy.data.user.remote.dto.*
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject


class UserRepoImpl @Inject constructor(private val api: UserApi) :
    UserRepo {

    // USER REGISTER IMPLEMENTATION
    override suspend fun userRegister(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userRegisterApi(user)
            if (response.isSuccessful) {
                val body = response.body()!!
                val registerEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,


                    body.token

                )
                emit(BaseResult.Success(registerEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err: WrappedResponse<User> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // USER LOGIN IMPLEMENTATION
    override suspend fun userLogin(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userLoginApi(user)
            if (response.isSuccessful) {
                val body = response.body()!!
                val loginEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,

                    body.token
                )
                emit(BaseResult.Success(loginEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err: WrappedResponse<User> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // GETTING USER DETAILS IMPLEMENTATION
    override suspend fun updateUserPassword(req: UpdatePasswordRequest): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.updateUserPasswordApi(req)
            if (response.isSuccessful) {

                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,


                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }


    // UPDATE USERNAME
    override suspend fun updateUsername(req: UpdateUsernameReq): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.updateUsernameApi(req)
            if (response.isSuccessful) {

                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,
                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }


    override suspend fun forgotPassword(req: ForgotPasswordReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.forgotPasswordAPI(req)
            if (response.isSuccessful) {



                emit(BaseResult.Success("email sent"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }

    // Verify OTP Implementation
    override suspend fun verifyOtp(req: VerifyOtpReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.verifyOtpApi(req)
            if (response.isSuccessful) {

                emit(BaseResult.Success("otp verified"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))


            }
        }
    }

    // reset password
    override suspend fun resetPassword(req: ResetPasswordReq): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.resetPasswordApi(req)
            if (response.isSuccessful) {

                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,


                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }
    override suspend fun verifyUsername(req: UpdateUsernameReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.verifyUsernameApi(req)
            if (response.isSuccessful) {

                emit(BaseResult.Success("otp verified"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))


            }
        }
    }

    override suspend fun uploadProfilePicture(

        image: MultipartBody.Part,

    ): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.uploadProfilePicApi(image)

            if (response.isSuccessful) {
                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,
                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun deleteAccount(): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.deleteAccountApi()
            if (response.isSuccessful) {

                emit(BaseResult.Success("account deleted"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))


            }
        }
    }
    override suspend fun getUserNotifications():  Flow<BaseResult<List<Notification>, WrappedListResponse<Notification>>> {
        return flow {
            val response = api.getUserNotificationsApi()
            if (response.isSuccessful) {
                val body = response.body()!!
                val notifications = mutableListOf<Notification>()

                body.data?.forEach { notificationResponse ->
                    notifications.add(
                        Notification(
                            notificationResponse.title,
                            notificationResponse.description!!,
                            notificationResponse.date,
                        )
                    )

                }
                emit(BaseResult.Success(notifications))
            } else {
                val type = object : TypeToken<WrappedListResponse<Notification>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Notification>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }

    }

    override suspend fun deleteNotification(req: DeleteNotificationReq):  Flow<BaseResult<List<Notification>, WrappedListResponse<Notification>>> {
        return flow {
            val response = api.deleteNotificationApi(req)
            if (response.isSuccessful) {
                val body = response.body()!!
                val notifications = mutableListOf<Notification>()

                body.data?.forEach { notificationResponse ->
                    notifications.add(
                        Notification(
                            notificationResponse.title,
                            notificationResponse.description!!,
                            notificationResponse.date,
                        )
                    )

                }
                emit(BaseResult.Success(notifications))
            } else {
                val type = object : TypeToken<WrappedListResponse<Notification>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Notification>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }

    }
    override suspend fun logout(): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.logoutApi()
            if (response.isSuccessful) {

                emit(BaseResult.Success("Logout successful"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))


            }
        }
    }
}