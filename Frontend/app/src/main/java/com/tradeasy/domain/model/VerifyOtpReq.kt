package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName

data class VerifyOtpReq(
    @SerializedName("otp") val otp: String?,
    @SerializedName("email") val email: String?,
)