package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName

data class SearchReq(

    @SerializedName("name") val name : String?,

    )