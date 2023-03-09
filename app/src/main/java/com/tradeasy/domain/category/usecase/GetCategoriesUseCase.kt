package com.tradeasy.domain.category.usecase


import com.tradeasy.domain.category.CategoryRepo
import com.tradeasy.domain.category.entity.Category
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(private val categoryRepo: CategoryRepo) {
    suspend fun invoke() : Flow<BaseResult<List<Category>, WrappedListResponse<Category>>>{
        return categoryRepo.getCategories()
    }
}