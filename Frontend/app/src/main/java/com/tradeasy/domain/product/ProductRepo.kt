package com.tradeasy.domain.product

import com.tradeasy.data.product.remote.dto.AddToSavedReq
import com.tradeasy.data.product.remote.dto.BuyNowReq
import com.tradeasy.data.product.remote.dto.SearchReq
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ProductRepo {


    // GET PRODUCTS FOR BID
    suspend fun getProductsForBid(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>

    //ADD PRODUCT
    suspend fun addProduct(
        category: MultipartBody.Part,
        name: MultipartBody.Part,
        description: MultipartBody.Part,
        price: MultipartBody.Part,
        image: MultipartBody.Part,
        quantity: MultipartBody.Part,
        for_bid: MultipartBody.Part,
        bid_end_date: MultipartBody.Part,

        ): Flow<BaseResult<Product, WrappedResponse<Product>>>

    // PLACE BID
    suspend fun placeBid(bid: Bid): Flow<BaseResult<Bid, WrappedResponse<Bid>>>

    // SEARCH PRODUCTS BY NAME
    suspend fun searchProductByName(name: SearchReq): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>

    // ADD PROD TO SAVED
    suspend fun addProductToSaved(req: AddToSavedReq): Flow<BaseResult<User, WrappedResponse<User>>>

    // GET SAVED PRODUCTS
    suspend fun getSavedProducts(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>

    // GET PRODUCTS THAT THE CONNECTED USER IS SELLING
    suspend fun userSelling(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>

    //buy now
    suspend fun buyNow(req: BuyNowReq): Flow<BaseResult<Product, WrappedResponse<Product>>>
}