package com.tradeasy.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class ProdIdReq(

    @SerializedName("product_id") val product_id : String?,

    )