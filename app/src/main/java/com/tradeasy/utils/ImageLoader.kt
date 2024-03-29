package com.tradeasy.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide

fun imageLoader(url:String,img: ImageView){

   // Picasso.get().load(url).fit().centerInside().transform(CircleTransform()).into(img)
    Glide.with(img.context).load(url).into(img)

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

 fun isWifiConnected(context: Context): Boolean {
    val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
    return mWifi!!.isConnected

}
fun View.gone(){
    visibility = View.GONE
}

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}