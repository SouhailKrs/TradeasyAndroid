package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class UserRegister(

    @SerializedName("username") val username: String,
    @SerializedName("phoneNumber") val phoneNumber: Number,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,


    )