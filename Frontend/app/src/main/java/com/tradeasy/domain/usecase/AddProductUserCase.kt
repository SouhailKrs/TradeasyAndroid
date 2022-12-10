package com.tradeasy.domain.usecase


import com.tradeasy.domain.model.Product
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val tradeasyRepository: TradeasyRepository) {
    suspend fun invoke(category: MultipartBody.Part,
                       name: MultipartBody.Part,
                       description: MultipartBody.Part,
                       price: MultipartBody.Part,
                       image: MultipartBody.Part,
                       quantity: MultipartBody.Part,
                       for_bid: MultipartBody.Part,
                       bid_end_date: MultipartBody.Part,) : Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return tradeasyRepository.addProduct(category,name,description,price, image, quantity, for_bid, bid_end_date)
    }
}