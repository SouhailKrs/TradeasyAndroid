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
    suspend fun updateUserPasswordApi(@Body req: UpdatePasswordRequest): Response<WrappedResponse<User>>

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

    // GET PRODUCTS THAT THE CONNECTED USER IS SELLING
    @GET("/product/userSelling")
    suspend fun userSellingApi(): Response<WrappedListResponse<Product>>

    // UPDATE USERNAME API
    @POST("/user/updateUsername")
    suspend fun updateUsernameApi(@Body req: UpdateUsernameReq): Response<WrappedResponse<User>>
    //forgot password
    @POST("/user/forgotPassword")
    suspend fun forgotPasswordAPI(@Body req:ForgotPasswordReq): Response<WrappedResponse<String>>
    //verify otp
    @POST("/user/verifyOtp")
    suspend fun verifyOtpApi(@Body req: VerifyOtpReq): Response<WrappedResponse<String>>
    @POST("/user/resetpassword")
    suspend fun resetPasswordApi(@Body req: ResetPasswordReq): Response<WrappedResponse<User>>
    //buy now
    @POST("/product/buynow")
    suspend fun buyNowApi(@Body req: BuyNowReq): Response<WrappedResponse<Product>>


}
