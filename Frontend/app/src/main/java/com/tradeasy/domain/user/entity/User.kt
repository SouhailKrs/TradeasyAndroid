package com.tradeasy.domain.user.entity

import com.google.gson.annotations.SerializedName
import com.tradeasy.domain.product.entity.Product


data class User(

    @SerializedName("username") val username: String?,
    @SerializedName("phoneNumber") val phoneNumber: Number?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("isVerified") val isVerified: Boolean?,
    @SerializedName("notificationToken") val notificationToken: String?,
    @SerializedName("notification") val notification: MutableList<Notification>?,
    @SerializedName("savedProducts") val savedProducts: MutableList<Product>?,
    @SerializedName("otp") val otp: Number?,
    @SerializedName("token") val token: String?,


    )
// data class constructor
{
    constructor() : this(
        "",
        0,
        "",
        "",
        "",
        false,
        "",
        mutableListOf<Notification>(),
        mutableListOf<Product>(),
        0,
        ""
    )


}







