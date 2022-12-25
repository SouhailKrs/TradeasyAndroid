package com.tradeasy.data.product.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.product.remote.api.ProductApi
import com.tradeasy.data.product.remote.dto.AddToSavedReq
import com.tradeasy.data.product.remote.dto.BuyNowReq
import com.tradeasy.data.product.remote.dto.SearchReq
import com.tradeasy.domain.product.ProductRepo
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import javax.inject.Inject


class ProductRepoImpl @Inject constructor(private val api: ProductApi) :
    ProductRepo {


    override suspend fun addProduct(
        category: MultipartBody.Part,
        name: MultipartBody.Part,
        description: MultipartBody.Part,
        price: MultipartBody.Part,
        image: List<MultipartBody.Part>,
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
                    body.data?.username!!,
                    body.data?.userPhoneNumber!!,
                    body.data?.userProfilePicture!!,
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
                            productResponse.username,
                            productResponse.userPhoneNumber,
                            productResponse.userProfilePicture,
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
                            productResponse.username,
                            productResponse.userPhoneNumber,
                            productResponse.userProfilePicture,
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
                    body.data?.notifications!!,
                    body.data?.savedProducts!!,
                    body.data?.otp!!,
                    body.data?.countryCode!!,
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
                            productResponse.username,
                            productResponse.userPhoneNumber,
                            productResponse.userProfilePicture,
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
                            productResponse.username,
                            productResponse.userPhoneNumber,
                            productResponse.userProfilePicture,
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

    // BUY NOW IMPLEMENTATION
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
                    body.data?.username!!,
                    body.data?.userPhoneNumber!!,
                    body.data?.userProfilePicture!!,
                    body.data?.productId!!
                )
                emit(BaseResult.Success(product))
            } else {
                val type = object : TypeToken<WrappedResponse<Product>>() {}.type
                val err = Gson().fromJson<WrappedResponse<Product>>(
                    response.errorBody()!!.charStream(),
                    type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))

            }
        }
    }
}