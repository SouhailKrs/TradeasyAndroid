package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName


data class UpdateUsernameReq(

    @SerializedName("newUsername") val newUsername: String?,

)



