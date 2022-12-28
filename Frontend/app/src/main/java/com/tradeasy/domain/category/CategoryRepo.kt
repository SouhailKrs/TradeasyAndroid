package com.tradeasy.domain.category

import com.tradeasy.domain.category.entity.Category
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow

interface CategoryRepo {


    // GET SAVED PRODUCTS
    suspend fun getCategories(): Flow<BaseResult<List<Category>, WrappedListResponse<Category>>>
}

