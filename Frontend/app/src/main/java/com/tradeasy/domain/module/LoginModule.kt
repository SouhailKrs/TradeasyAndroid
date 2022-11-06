package com.tradeasy.domain.module

import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.data.repository.TradeasyImplementation
import com.tradeasy.domain.repository.TradeasyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [com.tradeasy.di.Module::class])
@InstallIn(SingletonComponent::class)
class LoginModule {

    @Singleton
    @Provides
    fun provideLoginApi(retrofit: Retrofit): TradeasyApi {
        return retrofit.create(TradeasyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideLoginRepository(loginApi: TradeasyApi): TradeasyRepository {
        return TradeasyImplementation(loginApi)
    }
}