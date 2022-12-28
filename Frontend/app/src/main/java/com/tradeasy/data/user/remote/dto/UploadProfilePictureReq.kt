package com.tradeasy.data.user.remote.dto

import com.google.gson.annotations.SerializedName

data class UploadProfilePictureReq(
    @SerializedName("image") val profilePicture: String?
)