package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.User
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDetailsUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun execute(): Flow<BaseResult<User>> {
        return tradeasyRepository.getUserDetails()
    }
}
