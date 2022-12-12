package com.tradeasy.data.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.ForgotPasswordReq
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class TradeasyImplementation @Inject constructor(private val api: TradeasyApi) :
    TradeasyRepository {

    // USER REGISTER IMPLEMENTATION
    override suspend fun userRegister(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userRegisterApi(user)
            if(response.isSuccessful){
                val body = response.body()!!
                val registerEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!

                )
                emit(BaseResult.Success(registerEntity))
            }else{
                val type = object : TypeToken<WrappedResponse<User>>(){}.type
                val err : WrappedResponse<User> = Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // USER LOGIN IMPLEMENTATION
    override suspend fun userLogin(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userLoginApi(user)
            if(response.isSuccessful){
                val body = response.body()!!
                val loginEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!
                )
                emit(BaseResult.Success(loginEntity))
            }else{
                val type = object : TypeToken<WrappedResponse<User>>(){}.type
                val err : WrappedResponse<User> = Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // GETTING USER DETAILS IMPLEMENTATION
    override suspend fun getUserDetails(): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.getUserDetailsApi()
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>(){}.type
                val err = Gson().fromJson<WrappedResponse<User>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }
    override suspend fun updateUserPassword(req:UpdatePasswordRequest): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.updateUserPasswordAPI(req)
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>(){}.type
                val err = Gson().fromJson<WrappedResponse<User>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }

    override suspend fun forgotPassword(req:ForgotPasswordReq): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.forgotPasswordAPI(req)
            if (response.isSuccessful) {
                println("response successfully")

                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>(){}.type
                val err = Gson().fromJson<WrappedResponse<User>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }


}