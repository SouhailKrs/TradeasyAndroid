package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class ResetPasswordReq(

    @SerializedName("email") val email: String?,
    @SerializedName("otp") val otp: String?,
    @SerializedName("newPassword") val newPassword: String?,

    )