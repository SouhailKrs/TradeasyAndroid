package com.tradeasy.data.remote


<<<<<<< HEAD
import com.tradeasy.domain.model.UserLogin
import com.tradeasy.domain.model.UserRegister
import retrofit2.Response
import retrofit2.http.Body
=======
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
>>>>>>> Souhail
import retrofit2.http.POST

interface TradeasyApi {

<<<<<<< HEAD
    //Sign up
    @POST("/user/register")
    suspend fun register(@Body user: UserRegister): Response<UserRegister>
    //Sign in
    @POST("/user/login")
    suspend fun login(@Body user: UserLogin): Response<UserRegister>
=======
    // USER REGISTER API
    @POST("/user/register")
    suspend fun userRegisterApi(@Body user: User): Response<WrappedResponse<User>>
    // USER LOGIN API
    @POST("/user/login")
    suspend fun userLoginApi(@Body user: User): Response<WrappedResponse<User>>
    // USER DETAILS API
    @GET("/user/details")
    suspend fun getUserDetailsApi(): Response<WrappedResponse<User>>
    @POST("/user/updatePassword")
    suspend fun updateUserPasswordAPI(@Body req:UpdatePasswordRequest): Response<WrappedResponse<User>>
>>>>>>> Souhail
}