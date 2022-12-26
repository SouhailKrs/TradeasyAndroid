package com.tradeasy.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class GetByCatReq(

    @SerializedName("category") val category : String?,

    )