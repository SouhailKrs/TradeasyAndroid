package com.tradeasy.data.remote


import com.tradeasy.domain.model.*
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface TradeasyApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>

    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>

    // USER DETAILS API
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordAPI(@Body req: UpdatePasswordRequest): Response<WrappedResponse<User>>

    // GET PRODUCTS FOR API
    @GET("/product/productsforbid")
    suspend fun getProductsForBidApi(): Response<WrappedListResponse<Product>>

    // ADD PRODUCT API
    @Multipart
    @POST("/product/user/add")
    suspend fun addProductApi(
        @Part category: MultipartBody.Part,
        @Part name: MultipartBody.Part,
        @Part description: MultipartBody.Part,
        @Part price: MultipartBody.Part,
        @Part image: MultipartBody.Part,
        @Part quantity: MultipartBody.Part,
        @Part for_bid: MultipartBody.Part,
        @Part bid_end_date: MultipartBody.Part,
    ): Response<WrappedResponse<Product>>

    // PLACE BID API
    @POST("/bid/place")
    suspend fun placeBidApi(@Body bid: Bid): Response<WrappedResponse<Bid>>

    // SEARCH PRODUCT BY NAME API
    @POST("/product/searchbyname")
    suspend fun searchProductByNameApi(@Body name: SearchReq): Response<WrappedListResponse<Product>>

    // ADD PRODUCT TO SAVED API
    @POST("/product/addprodtosaved")
    suspend fun addProductToSavedApi(@Body addToSavedReq: AddToSavedReq): Response<WrappedResponse<User>>

    // GET SAVED PRODUCTS API
    @GET("/product/getsavedprods")
    suspend fun getSavedProductsApi(): Response<WrappedListResponse<Product>>
}
