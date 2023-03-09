package com.tradeasy.domain.category.entity

import com.google.gson.annotations.SerializedName


data class Category(

    @SerializedName("name") val name: String?,

    )
// data class constructor
{
    constructor() : this(
        "",
    )
}






