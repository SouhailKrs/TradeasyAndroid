package com.tradeasy.utils

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun ImageLoader(url:String,img: ImageView){


    Picasso.get().load(url).into(img)

}