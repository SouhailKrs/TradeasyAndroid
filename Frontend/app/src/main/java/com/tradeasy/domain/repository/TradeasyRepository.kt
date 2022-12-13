package com.tradeasy.domain.repository

import com.tradeasy.domain.model.*
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface TradeasyRepository {


    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User, WrappedResponse<User>>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User, WrappedResponse<User>>>

    // UPDATE USER PASSWORD
    suspend fun updateUserPassword(req: UpdatePasswordRequest): Flow<BaseResult<User, WrappedResponse<User>>>

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
    // UPDATE USER USERNAME
    suspend fun updateUsername(req: UpdateUsernameReq): Flow<BaseResult<User, WrappedResponse<User>>>
    //forgot password
    suspend fun forgotPassword(req:ForgotPasswordReq): Flow<BaseResult<String,WrappedResponse<String>>>
    //verify otp
    suspend fun verifyOtp(req:VerifyOtpReq): Flow<BaseResult<String,WrappedResponse<String>>>
    // Reset password
    suspend fun resetPassword(req: ResetPasswordReq): Flow<BaseResult<User, WrappedResponse<User>>>
    //buy now
    suspend fun buyNow(req: BuyNowReq): Flow<BaseResult<Product,WrappedResponse<Product>>>
}