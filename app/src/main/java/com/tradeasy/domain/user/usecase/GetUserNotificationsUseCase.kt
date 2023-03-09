package com.tradeasy.domain.user.usecase


import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserNotificationsUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun invoke() : Flow<BaseResult<List<Notification>, WrappedListResponse<Notification>>>{
        return userRepo.getUserNotifications()
    }
}