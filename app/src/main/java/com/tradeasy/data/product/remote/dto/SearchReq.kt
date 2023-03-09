package com.tradeasy.data.product.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchReq(

    @SerializedName("name") val name : String?,

    )