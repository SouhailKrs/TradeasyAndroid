package com.tradeasy.domain.user.usecase

import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class UploadProfilePicUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun invoke(
                       image: MultipartBody.Part,
                   ) : Flow<BaseResult<User, WrappedResponse<User>>> {
        return userRepo.uploadProfilePicture( image)
    }
}