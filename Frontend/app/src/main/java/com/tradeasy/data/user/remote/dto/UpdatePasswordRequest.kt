package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName


data class UpdatePasswordRequest(

    @SerializedName("currentPassword") val currentPassword : String?,
    @SerializedName("newPassword") val newPassword: String?,
    @SerializedName("email") val email: String?,

    )
{
    constructor() : this(
        "",
        "",""
    )

}




