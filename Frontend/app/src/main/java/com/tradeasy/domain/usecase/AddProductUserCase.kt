package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.Product
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke(product: Product) : Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return tradeasyRepository.addProduct(product)
    }
}