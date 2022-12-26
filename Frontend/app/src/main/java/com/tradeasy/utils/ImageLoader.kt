package com.tradeasy.utils

import android.content.Context
import android.widget.ImageView
import com.squareup.picasso.Picasso

fun imageLoader(url:String,img: ImageView){


    Picasso.get().load(url).into(img)

}
// fucntion that returns height and width of the screen
fun getScreenSize(context: Context): Pair<Double, Double> {
    val displayMetrics = context.resources.displayMetrics
    return Pair(displayMetrics.heightPixels.toDouble(), displayMetrics.widthPixels.toDouble())
}

// check if user is logged in or not
fun isLoggedIn(sharedPrefs: SharedPrefs): Boolean {
    return sharedPrefs.getUser() != null
}
