package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun execute(user: User): Flow<BaseResult<User>> = flow {
        try {
            emit(BaseResult.Loading<User>())
            val user = tradeasyRepository.register(user)
            emit(BaseResult.Success<User>(user))
        } catch (e: HttpException) {
            emit(BaseResult.Error<User>(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {

            emit(BaseResult.Error<User>("Couldn't reach server. Check your internet connection."))
        }
    }
}
