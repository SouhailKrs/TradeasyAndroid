package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName


data class ForgotPasswordReq(

    @SerializedName("email") val email: String?,

    )