package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class User(

    @SerializedName("username") val username: String?,
    @SerializedName("phoneNumber") val phoneNumber: Number?,
    @SerializedName("email") val email: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("profilePicture") val profilePicture: String?,
    @SerializedName("isVerified") val isVerified: Boolean?,
    @SerializedName("token") val token: String?
)
    // data class constructor
{
    constructor():this("",null,"","","None",false,"")


}



