package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class ForgotPasswordReq(

    @SerializedName("email") val email: String?,

    )