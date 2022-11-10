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
    override suspend fun register(user: User) {
        val response = api.registerApi(user)
        if (response.isSuccessful) {
            response.body()?.let {
                println("Success")
            }
        } else {
            println("Error")
        }
    }

    // login
    override suspend fun login(user: User): Flow<BaseResult<User>> {
        return flow {
            try {
                val response = api.loginApi(user)
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

    // User details
    override suspend fun getUserDetails(): Flow<BaseResult<User>> {
        return flow {
            val response = api.getUserDetailsApi()
            if (response.isSuccessful) {
                println("repo succ")
                val body = response.body()!!
                val user = User(
                    body.username,
                    body.phoneNumber,
                    body.email,
                    body.password,
                    body.profilePicture
                )
                emit(BaseResult.Success(user))
            } else {
                println("repo err")
                emit(BaseResult.Error<User>("An unexpected error occurred"))
            }
        }
    }
}