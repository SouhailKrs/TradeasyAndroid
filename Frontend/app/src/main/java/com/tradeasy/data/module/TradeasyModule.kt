package com.tradeasy.data.module

import com.tradeasy.data.remote.TradeasyApi
import com.tradeasy.data.repository.TradeasyImplementation
import com.tradeasy.di.NetworkModule
import com.tradeasy.domain.repository.TradeasyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class TradeasyModule {

    @Singleton
    @Provides
    fun provideTradeasyApi(retrofit: Retrofit): TradeasyApi {
        return retrofit.create(TradeasyApi::class.java)
    }

    @Singleton
    @Provides
    fun provideTradeasyRepository(tradeasyApi: TradeasyApi): TradeasyRepository {
        return TradeasyImplementation(tradeasyApi)
    }
}