package com.tradeasy.data.repository


import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class TradeasyImplementation @Inject constructor(private val api: TradeasyApi) :
    TradeasyRepository {

    // USER REGISTER IMPLEMENTATION
    override suspend fun userRegister(user: User): Flow<BaseResult<User>> {
        return flow {
            try {
                val response = api.userRegisterApi(user)
                if (response.isSuccessful) {
                    val body = response.body()
                    val registeredUser = User(
                        body?.username,
                        body?.phoneNumber,
                        body?.email,
                        body?.password,
                        body?.profilePicture
                    )
                    emit(BaseResult.Success<User>(registeredUser))
                } else {
                    emit(BaseResult.Error<User>("Error"))
                }
            } catch (e: Exception) {
                emit(BaseResult.Error<User>("Error"))
            }
        }
    }

    // USER LOGIN IMPLEMENTATION
    override suspend fun userLogin(user: User): Flow<BaseResult<User>> {
        return flow {
            try {
                val response = api.userLoginApi(user)
                if (response.isSuccessful) {
                    val body = response.body()
                    val loggedUser = User(
                        body?.username,
                        body?.phoneNumber,
                        body?.email,
                        body?.password,
                        body?.profilePicture
                    )
                    emit(BaseResult.Success<User>(loggedUser))
                } else {
                    emit(BaseResult.Error<User>("Error"))
                }
            } catch (e: Exception) {
                emit(BaseResult.Error<User>("Error"))
            }
        }
    }

    // GETTING USER DETAILS IMPLEMENTATION
    override suspend fun getUserDetails(): Flow<BaseResult<User>> {
        return flow {
            val response = api.getUserDetailsApi()
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val user = User(
                    body.username, body.phoneNumber, body.email, body.password, body.profilePicture
                )
                emit(BaseResult.Success(user))
            } else {
                println("repo err")
                emit(BaseResult.Error<User>("An unexpected error occurred"))
            }
        }
    }


}