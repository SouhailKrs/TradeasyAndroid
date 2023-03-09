package com.tradeasy.domain.product.usecase


import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class CreateProductUseCase @Inject constructor(private val productRepo: ProductRepo) {
    suspend fun invoke(
        category: MultipartBody.Part,
        name: MultipartBody.Part,
        description: MultipartBody.Part,
        price: MultipartBody.Part,
        image: List<MultipartBody.Part>,
        quantity: MultipartBody.Part,
        for_bid: MultipartBody.Part,
        bid_end_date: MultipartBody.Part,) : Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return productRepo.addProduct(category,name,description,price, image, quantity, for_bid, bid_end_date)
    }
}