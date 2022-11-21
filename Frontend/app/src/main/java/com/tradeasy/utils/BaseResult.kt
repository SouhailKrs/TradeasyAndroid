package com.tradeasy.utils

<<<<<<< HEAD
sealed class BaseResult<T>(val U: Any? = null, val message: String? = null) {
    class Success<T>(U: Any) : BaseResult<T>(U)
    class Error<T : Any>(message: String, data: T? = null) : BaseResult<T>(data, message)
    class Loading<T>(data: T? = null) : BaseResult<T>(data)
}

=======
sealed class BaseResult <out T : Any, out U : Any> {
    data class Success <T: Any>(val data : T) : BaseResult<T, Nothing>()
    data class Error <U : Any>(val rawResponse: U) : BaseResult<Nothing, U>()
}
>>>>>>> Souhail
