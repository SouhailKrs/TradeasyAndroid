package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class VerifyAccountReq(
    @SerializedName("otp") val otp: String?,
)