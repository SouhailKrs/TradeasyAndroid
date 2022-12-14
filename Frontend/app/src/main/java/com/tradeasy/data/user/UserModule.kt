package com.tradeasy.data.user

import com.tradeasy.data.module.NetworkModule
import com.tradeasy.data.user.remote.api.UserApi
import com.tradeasy.data.user.repository.UserRepoImpl
import com.tradeasy.domain.user.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class UserModule {

    @Singleton
    @Provides
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Singleton
    @Provides
    fun provideUserRepo(userApi: UserApi): UserRepo {
        return UserRepoImpl(userApi)
    }


}