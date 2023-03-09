package com.tradeasy.data.category

import com.tradeasy.data.category.remote.api.CategoryApi
import com.tradeasy.data.category.repository.CategoryRepoImpl
import com.tradeasy.data.module.NetworkModule
import com.tradeasy.domain.category.CategoryRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class CategoryModule {

    @Singleton
    @Provides
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserRepo(categoryApi: CategoryApi): CategoryRepo {
        return CategoryRepoImpl(categoryApi)
    }


}