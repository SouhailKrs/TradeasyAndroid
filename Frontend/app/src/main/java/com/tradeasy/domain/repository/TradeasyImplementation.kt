package com.tradeasy.domain.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.*
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject


class TradeasyImplementation @Inject constructor(private val api: TradeasyApi) :
    TradeasyRepository {

    // USER REGISTER IMPLEMENTATION
    override suspend fun userRegister(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userRegisterApi(user)
            if (response.isSuccessful) {
                val body = response.body()!!
                val registerEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notification!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,


                    body.token

                )
                emit(BaseResult.Success(registerEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err: WrappedResponse<User> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // USER LOGIN IMPLEMENTATION
    override suspend fun userLogin(user: User): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.userLoginApi(user)
            if (response.isSuccessful) {
                val body = response.body()!!
                val loginEntity = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notification!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,

                    body.token
                )
                emit(BaseResult.Success(loginEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err: WrappedResponse<User> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // GETTING USER DETAILS IMPLEMENTATION
    override suspend fun updateUserPassword(req: UpdatePasswordRequest): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.updateUserPasswordApi(req)
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notification!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,


                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }


    override suspend fun addProduct(
        category: MultipartBody.Part,
        name: MultipartBody.Part,
        description: MultipartBody.Part,
        price: MultipartBody.Part,
        image: MultipartBody.Part,
        quantity: MultipartBody.Part,
        for_bid: MultipartBody.Part,
        bid_end_date: MultipartBody.Part,
    ): Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return flow {
            val response = api.addProductApi(
                category,
                name,
                description,
                price,
                image,
                quantity,
                for_bid,
                bid_end_date
            )
            if (response.isSuccessful) {
                val body = response.body()!!
                val product = Product(
                    body.data?.userId!!,
                    body.data?.category!!,
                    body.data?.name!!,
                    body.data?.description!!,
                    body.data?.price!!,
                    body.data?.image!!,
                    body.data?.quantity!!,
                    body.data?.addedDate!!,
                    body.data?.forBid!!,
                    body.data?.bidEndDate!!,
                    body.data?.bade!!,
                    body.data?.sold!!,
                    body.data?.productId!!
                )
                emit(BaseResult.Success(product))
            } else {
                val type = object : TypeToken<WrappedResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedResponse<Product>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    override suspend fun placeBid(bid: Bid): Flow<BaseResult<Bid, WrappedResponse<Bid>>> {
        return flow {
            val response = api.placeBidApi(bid)
            if (response.isSuccessful) {
                val body = response.body()!!
                val bidEntity = Bid(
                    body.data?.userId!!,
                    body.data?.productId!!,
                    body.data?.bidAmount!!,
                )
                emit(BaseResult.Success(bidEntity))
            } else {
                val type = object : TypeToken<WrappedResponse<Bid>>() {}.type
                val err: WrappedResponse<Bid> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // SEARCH PRODUCT BY NAME IMPLEMENTATION
    override suspend fun searchProductByName(name: SearchReq): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>> {
        return flow {
            val response = api.searchProductByNameApi(name)
            if (response.isSuccessful) {
                val body = response.body()!!
                val products = mutableListOf<Product>()

                body.data?.forEach { productResponse ->
                    products.add(
                        Product(
                            productResponse.userId,
                            productResponse.category,
                            productResponse.name,
                            productResponse.description,
                            productResponse.price,
                            productResponse.image,
                            productResponse.quantity,
                            productResponse.addedDate,
                            productResponse.forBid,
                            productResponse.bidEndDate,
                            productResponse.bade,
                            productResponse.sold,
                            productResponse.productId
                        )
                    )
                    println("products  $products")
                }
                emit(BaseResult.Success(products))
            } else {
                val type = object : TypeToken<WrappedListResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Product>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // GET PRODUCTS FOR BID IMPLEMENTATION
    override suspend fun getProductsForBid(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>> {
        return flow {
            val response = api.getProductsForBidApi()
            if (response.isSuccessful) {
                val body = response.body()!!
                val data = body.data
                println("data $data")
                val products = mutableListOf<Product>()


                    body.data?.forEach { productResponse ->
                        products.add(
                            Product(
                                productResponse.userId,
                                productResponse.category,
                                productResponse.name,
                                productResponse.description,
                                productResponse.price,
                                productResponse.image,
                                productResponse.quantity,
                                productResponse.addedDate,
                                productResponse.forBid,
                                productResponse.bidEndDate,
                                productResponse.bade,
                                productResponse.sold,
                                productResponse.productId
                            )
                        )
                    }
                    emit(BaseResult.Success(products))


            } else {
                val type = object : TypeToken<WrappedListResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Product>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // ADD PRODUCT TO SAVED  IMPLEMENTATION
    override suspend fun addProductToSaved(req: AddToSavedReq): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.addProductToSavedApi(req)
            if (response.isSuccessful) {
                val body = response.body()!!
                val newUser = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notification!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.token

                )
                emit(BaseResult.Success(newUser))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err: WrappedResponse<User> =
                    Gson().fromJson(response.errorBody()!!.charStream(), type)
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }

    // GET SAVED PRODUCTS IMPLEMENTATION
    override suspend fun getSavedProducts(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>> {
        return flow {
            val response = api.getSavedProductsApi()
            if (response.isSuccessful) {
                val body = response.body()!!
                val products = mutableListOf<Product>()

                body.data?.forEach { productResponse ->
                    products.add(
                        Product(
                            productResponse.userId,
                            productResponse.category,
                            productResponse.name,
                            productResponse.description,
                            productResponse.price,
                            productResponse.image,
                            productResponse.quantity,
                            productResponse.addedDate,
                            productResponse.forBid,
                            productResponse.bidEndDate,
                            productResponse.bade,
                            productResponse.sold,
                            productResponse.productId
                        )
                    )
                    println("products  $products")
                }
                emit(BaseResult.Success(products))
            } else {
                val type = object : TypeToken<WrappedListResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Product>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }

    }

    // GET PRODUCTS THAT THE CONNECTED USER IS SELLING IMPLEMENTATION
    override suspend fun userSelling(): Flow<BaseResult<List<Product>, WrappedListResponse<Product>>> {
        return flow {
            val response = api.userSellingApi()
            if (response.isSuccessful) {
                val body = response.body()!!
                val products = mutableListOf<Product>()

                body.data?.forEach { productResponse ->
                    products.add(
                        Product(
                            productResponse.userId,
                            productResponse.category,
                            productResponse.name,
                            productResponse.description,
                            productResponse.price,
                            productResponse.image,
                            productResponse.quantity,
                            productResponse.addedDate,
                            productResponse.forBid,
                            productResponse.bidEndDate,
                            productResponse.bade,
                            productResponse.sold,
                            productResponse.productId
                        )
                    )
                    println("products  $products")
                }
                emit(BaseResult.Success(products))
            } else {
                val type = object : TypeToken<WrappedListResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Product>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }

    }
// UPDATE USERNAME
    override suspend fun updateUsername(req: UpdateUsernameReq): Flow<BaseResult<User, WrappedResponse<User>>> {
        return flow {
            val response = api.updateUsernameApi(req)
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val user = User(
                    body.data?.username!!,
                    body.data?.phoneNumber!!,
                    body.data?.email!!,
                    body.data?.password!!,
                    body.data?.profilePicture!!,
                    body.data?.isVerified!!,
                    body.data?.notificationToken!!,
                    body.data?.notification!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.token
                )
                emit(BaseResult.Success(user))
            } else {
                val type = object : TypeToken<WrappedResponse<User>>() {}.type
                val err = Gson().fromJson<WrappedResponse<User>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }


    override suspend fun forgotPassword(req:ForgotPasswordReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.forgotPasswordAPI(req)
            if (response.isSuccessful) {
                println("response successfully")


                emit(BaseResult.Success("email sent"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>(){}.type
                val err = Gson().fromJson<WrappedResponse<String>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }

    // Verify OTP Implementation
    override suspend fun verifyOtp(req: VerifyOtpReq): Flow<BaseResult<String, WrappedResponse<String>>> {
        return flow {
            val response = api.verifyOtpApi(req)
            if (response.isSuccessful) {
                println("response successfully")
                emit(BaseResult.Success("otp verified"))
            } else {
                val type = object : TypeToken<WrappedResponse<String>>() {}.type
                val err = Gson().fromJson<WrappedResponse<String>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))


            }
        }
    }
// reset password
override suspend fun resetPassword(req: ResetPasswordReq): Flow<BaseResult<User, WrappedResponse<User>>> {
    return flow {
        val response = api.resetPasswordApi(req)
        if (response.isSuccessful) {
            println("response successfully")
            val body = response.body()!!
            val user = User(
                body.data?.username!!,
                body.data?.phoneNumber!!,
                body.data?.email!!,
                body.data?.password!!,
                body.data?.profilePicture!!,
                body.data?.isVerified!!,
                body.data?.notificationToken!!,
                body.data?.notification!!,
                body.data?.savedProducts!!,
                body.data?.otp!!,


                body.token
            )
            emit(BaseResult.Success(user))
        } else {
            val type = object : TypeToken<WrappedResponse<User>>() {}.type
            val err = Gson().fromJson<WrappedResponse<User>>(
                response.errorBody()!!.charStream(), type
            )!!
            err.code = response.code()
            emit(BaseResult.Error(err))

        }
    }
}
    override suspend fun buyNow(req: BuyNowReq): Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return flow {
            val response = api.buyNowApi(req)
            if (response.isSuccessful) {
                println("response successfully")
                val body = response.body()!!
                val product = Product(
                    body.data?.userId!!,
                    body.data?.category!!,
                    body.data?.name!!,
                    body.data?.description!!,
                    body.data?.price!!,
                    body.data?.image!!,
                    body.data?.quantity!!,
                    body.data?.addedDate!!,
                    body.data?.forBid!!,
                    body.data?.bidEndDate!!,
                    body.data?.bade!!,
                    body.data?.sold!!,
                    body.data?.productId!!
                )
                emit(BaseResult.Success(product))
            } else {
                val type = object : TypeToken<WrappedResponse<Product>>(){}.type
                val err = Gson().fromJson<WrappedResponse<Product>>(response.errorBody()!!.charStream(), type)!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }
}