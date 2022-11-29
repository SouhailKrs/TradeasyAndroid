package com.tradeasy.data.remote


import com.tradeasy.domain.model.Bid
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface TradeasyApi {

    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>
    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>
    // USER DETAILS API
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordAPI(@Body req:UpdatePasswordRequest): Response<WrappedResponse<User>>
    // GET PRODUCTS FOR API
    @GET("product/productsforbid")
    suspend fun getProductsForBidApi():  Response<WrappedListResponse<Product>>

    @POST("product/user/add")
    suspend fun addProductApi(@Body product: Product) : Response<WrappedResponse<Product>>
    @POST("/bid/place")
    suspend fun placeBidApi(@Body bid: Bid) : Response<WrappedResponse<Bid>>

}