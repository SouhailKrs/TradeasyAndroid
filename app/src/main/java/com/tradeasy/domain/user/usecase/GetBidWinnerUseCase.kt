package com.tradeasy.domain.user.usecase


import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.user.UserRepo
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBidWinnerUseCase @Inject constructor(private val userRepo: UserRepo) {
    suspend fun execute(req: ProdIdReq): Flow<BaseResult<User,WrappedResponse<User>>> {
        return userRepo.getBidWinner(req)
    }
}
