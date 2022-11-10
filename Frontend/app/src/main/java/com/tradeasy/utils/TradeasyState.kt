package com.tradeasy.utils

data class TradeasyState(
    val isLoading: Boolean = false,
    val success: Boolean? = false,
    val error: String = ""

)
