package com.tradeasy.domain.product.usecase


import com.tradeasy.data.product.remote.dto.AddToSavedReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddProductToSavedUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(req: AddToSavedReq) : Flow<BaseResult<User, WrappedResponse<User>>> {
        return productRepo.addProductToSaved(req)
    }
}