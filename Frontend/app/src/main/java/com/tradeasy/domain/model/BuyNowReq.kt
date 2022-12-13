package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName

data class BuyNowReq(
    @SerializedName ("id") val product_id: String?,
)