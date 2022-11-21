package com.tradeasy.domain.usecase


<<<<<<< HEAD
import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class LoginUseCase@Inject constructor(private val tradeasyRepository: TradeasyRepository) {
        suspend fun execute(userLogin: UserLogin): Flow<BaseResult <UserLogin>   > = flow {
            try {
                emit(BaseResult.Loading<UserLogin>())
                val user = tradeasyRepository.login(userLogin)
                emit(BaseResult.Success<UserLogin>(user))
            } catch(e: HttpException) {
                emit(BaseResult.Error<UserLogin>(e.localizedMessage ?: "An unexpected error occured"))
            } catch(e: IOException) {

                emit(BaseResult.Error<UserLogin>("Couldn't reach server. Check your internet connection."))
            }
        }
=======
import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase@Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun execute(user: User): Flow<BaseResult<User,WrappedResponse<User>>> {
        return tradeasyRepository.userLogin(user)
    }
>>>>>>> Souhail
}
