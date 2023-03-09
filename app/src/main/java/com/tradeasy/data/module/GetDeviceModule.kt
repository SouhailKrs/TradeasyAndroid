package com.tradeasy.data.module

import com.tradeasy.domain.repository.GetDeviceTokenImpl
import com.tradeasy.domain.repository.GetDeviceToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object GetDeviceModule {

        @Provides
        @Singleton
        fun provideGetDeviceToken(): GetDeviceToken {
            return GetDeviceTokenImpl()
        }
}