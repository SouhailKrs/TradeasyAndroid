package com.tradeasy.utils

sealed class BaseResult<T>(val U: Any? = null, val message: String? = null) {
    class Success<T>(U: Any) : BaseResult<T>(U)
    class Error<T : Any>(message: String, data: T? = null) : BaseResult<T>(data, message)
    class Loading<T>(data: T? = null) : BaseResult<T>(data)
}

