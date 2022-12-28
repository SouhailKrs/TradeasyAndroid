package com.tradeasy.domain.product.usecase


import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnlistProdUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun execute(req:ProdIdReq): Flow<BaseResult<String,WrappedResponse<String>>> {
        return productRepo.unlistProduct(req)
    }
}
