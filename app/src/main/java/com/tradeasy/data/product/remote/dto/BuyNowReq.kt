package com.tradeasy.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class BuyNowReq(
    @SerializedName ("id") val product_id: String?,
)