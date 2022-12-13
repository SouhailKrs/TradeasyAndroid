package com.tradeasy.utils

import okhttp3.Interceptor
import okhttp3.Response


class RequestInterceptor constructor(private val pref: SharedPrefs) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = pref.getToken()
        return if (token != null) {
            val newRequest = chain.request().newBuilder()
                .addHeader("jwt", token)
                .build()
            println("token: $newRequest")
            chain.proceed(newRequest)
        } else {
            val newRequest = chain.request().newBuilder()
                .build()
            chain.proceed(newRequest)
        }
    }
}