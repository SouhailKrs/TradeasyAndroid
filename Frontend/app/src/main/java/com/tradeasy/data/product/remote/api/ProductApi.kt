package com.tradeasy.data.product.remote.api


import com.tradeasy.data.product.remote.dto.AddToSavedReq
import com.tradeasy.data.product.remote.dto.BuyNowReq
import com.tradeasy.data.product.remote.dto.GetByCatReq
import com.tradeasy.data.product.remote.dto.SearchReq
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {

    // GET PRODUCTS FOR BID API
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
        @Part image: List<MultipartBody.Part>,
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

    //buy now
    @POST("/product/buynow")
    suspend fun buyNowApi(@Body req: BuyNowReq): Response<WrappedResponse<Product>>

    // GET PRODUCTS BY CATEGORY
    @POST("/product/findbycat")
    suspend fun getProductByCategoryApi(@Body category: GetByCatReq): Response<WrappedListResponse<Product>>
    // GET USER PRODUCTS API
    @GET("/product/userproducts")
    suspend fun getUserProducts(): Response<WrappedListResponse<Product>>

}
