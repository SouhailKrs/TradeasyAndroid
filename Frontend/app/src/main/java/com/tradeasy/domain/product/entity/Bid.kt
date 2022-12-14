package com.tradeasy.domain.product.entity

import com.google.gson.annotations.SerializedName


data class Bid(

    @SerializedName("user_id") val userId: String?,
    @SerializedName("product_id") val productId: String?,
    @SerializedName("bid_amount") val bidAmount: Number?,


)
    // data class constructor
{
constructor() : this("", "", 0)
}







