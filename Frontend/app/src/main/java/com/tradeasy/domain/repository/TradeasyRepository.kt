package com.tradeasy.domain.repository

import com.tradeasy.domain.model.Bid
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import com.tradeasy.utils.WrappedResponse
import kotlinx.coroutines.flow.Flow

interface TradeasyRepository {


    // USER REGISTER
    suspend fun userRegister(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // USER LOGIN
    suspend fun userLogin(user: User): Flow<BaseResult<User,WrappedResponse<User>>>

    // UPDATE USER PASSWORD
    suspend fun updateUserPassword(req:UpdatePasswordRequest): Flow<BaseResult<User,WrappedResponse<User>>>

    // GET PRODUCTS FOR BID
    suspend fun getProductsForBid():    Flow<BaseResult<List<Product>, WrappedListResponse<Product>>>

    //ADD PRODUCT
    suspend fun addProduct(product: Product): Flow<BaseResult<Product,WrappedResponse<Product>>>
  // PLACE BID
    suspend fun placeBid(bid: Bid): Flow<BaseResult<Bid,WrappedResponse<Bid>>>



}