package com.tradeasy.data.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.domain.model.*
import com.tradeasy.domain.repository.TradeasyRepository
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
            val response = api.updateUserPasswordAPI(req)
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


    override suspend fun addProduct(product: Product): Flow<BaseResult<Product, WrappedResponse<Product>>> {
        return flow {
            val response = api.addProductApi(product)
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


}