package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.UserRegister
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RegisterUseCase@Inject constructor(private val tradeasyRepository: TradeasyRepository) {
        suspend fun execute(userRegister: UserRegister): Flow<BaseResult <UserRegister>   > = flow {
            try {
                emit(BaseResult.Loading<UserRegister>())
                val user = tradeasyRepository.register(userRegister)
                emit(BaseResult.Success<UserRegister>(user))
            } catch(e: HttpException) {
                emit(BaseResult.Error<UserRegister>(e.localizedMessage ?: "An unexpected error occured"))
            } catch(e: IOException) {

                emit(BaseResult.Error<UserRegister>("Couldn't reach server. Check your internet connection."))
            }
        }
}
