package com.tradeasy.data.category.repository


import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tradeasy.data.category.remote.api.CategoryApi
import com.tradeasy.domain.category.CategoryRepo
import com.tradeasy.domain.category.entity.Category
import com.tradeasy.utils.BaseResult
import com.tradeasy.utils.WrappedListResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CategoryRepoImpl @Inject constructor(private val api: CategoryApi) :
    CategoryRepo {

    // GET CATEGORIES
    override suspend fun getCategories(): Flow<BaseResult<List<Category>, WrappedListResponse<Category>>> {
        return flow {
            val response = api.getCategoriesApi()
            if (response.isSuccessful) {
                val body = response.body()!!
                val data = body.data
                println("data $data")
                val category = mutableListOf<Category>()
                body.data?.forEach { categoryResponse ->
                    category.add(
                        Category(
                            categoryResponse.name,
                        )
                    )

                }

                emit(BaseResult.Success(category))


            } else {
                val type = object : TypeToken<WrappedListResponse<Category>>() {}.type
                val err = Gson().fromJson<WrappedListResponse<Category>>(
                    response.errorBody()!!.charStream(), type
                )!!
                err.code = response.code()
                emit(BaseResult.Error(err))
            }
        }
    }
}