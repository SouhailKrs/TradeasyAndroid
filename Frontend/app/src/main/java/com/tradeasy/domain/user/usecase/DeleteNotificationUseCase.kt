package com.tradeasy.domain.user.usecase


import com.tradeasy.data.user.remote.dto.DeleteNotificationReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req:DeleteNotificationReq):Flow<BaseResult<List<Notification>, WrappedListResponse<Notification>>> {
        return userRepo.deleteNotification(req)
    }
}
