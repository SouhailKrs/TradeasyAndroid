package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class VerifyOtpReq(
    @SerializedName("otp") val otp: String?,
    @SerializedName("email") val email: String?,
)