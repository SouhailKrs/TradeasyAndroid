package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class UpdatePasswordRequest(

    @SerializedName("currentPassword") val currentPassword : String?,
    @SerializedName("newPassword") val newPassword: String?,

)



