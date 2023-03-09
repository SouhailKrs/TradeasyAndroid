package com.tradeasy.data.product

import com.tradeasy.data.module.NetworkModule
import com.tradeasy.data.product.remote.api.ProductApi
import com.tradeasy.data.product.repository.ProductRepoImpl
import com.tradeasy.domain.product.ProductRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ProductModule {

    @Singleton
    @Provides
    fun provideProductApi(retrofit: Retrofit): ProductApi {
        return retrofit.create(ProductApi::class.java)
    }

    @Singleton
    @Provides
    fun provideProductRepo(productApi: ProductApi): ProductRepo {
        return ProductRepoImpl(productApi)
    }
}