package com.tradeasy.domain.model

import com.google.gson.annotations.SerializedName


data class UpdateUsernameReq(

    @SerializedName("newUsername") val newUsername: String?,

)



