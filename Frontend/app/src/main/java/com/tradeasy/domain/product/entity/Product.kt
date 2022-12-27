package com.tradeasy.domain.product.entity

import com.google.gson.annotations.SerializedName


data class Product(

    @SerializedName("user_id") val userId: String?,
    @SerializedName("category") val category: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("price") val price: Float?,
    @SerializedName("image") val image: List<String>?,
    @SerializedName("quantity") val quantity: Int?,
    @SerializedName("added_date") val addedDate: Number?,
    @SerializedName("for_bid") val forBid: Boolean?,
    @SerializedName("bid_end_date") val bidEndDate: Number?,
    @SerializedName("bade") val bade: Boolean?,
    @SerializedName("sold") val sold: Boolean?,
    @SerializedName("username") val username: String?,
    @SerializedName("userPhoneNumber") val userPhoneNumber: String?,
    @SerializedName("userProfilePicture") val userProfilePicture: String?,
    @SerializedName("selling") val selling: Boolean?,
    @SerializedName("_id") val productId: String?,


    )
// data class constructor
{
    constructor() : this(
        "",
        "",
        "",
        "",
        0.0f,
        listOf(),
        0,
        0,
        true,
        System.currentTimeMillis() + 860000,
        false,
        false,
        "",
        "",
        "",
        false,
        ""
    )
}






