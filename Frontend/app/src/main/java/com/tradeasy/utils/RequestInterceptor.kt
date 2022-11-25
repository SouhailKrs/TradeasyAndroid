package com.tradeasy.utils

import okhttp3.Interceptor
import okhttp3.Response


class RequestInterceptor constructor(private val pref: SharedPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = pref.getToken()
        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", token.toString())
            .build()
        return chain.proceed(newRequest)
    }
}