package com.tradeasy.domain.product.usecase

import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class DeleteProdUseCase  @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(req: ProdIdReq) : Flow<BaseResult<String, WrappedResponse<String>>>{
        return productRepo.deleteProduct(req)
    }
}