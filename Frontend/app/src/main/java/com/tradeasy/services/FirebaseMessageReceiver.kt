package com.tradeasy.services

import android.R
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tradeasy.utils.SharedPrefs
import javax.inject.Inject


class FirebaseMessageReceiver : FirebaseMessagingService() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val ADMIN_CHANNEL_ID = "admin_channel"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //FirebaseMessaging.getInstance().subscribeToTopic("general")

    }
// save the token in shared prefs

    fun getToken():String {
        return FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed" + task.exception)
                return@addOnCompleteListener
            }
            else{
                println("token is "+task.result)
                return@addOnCompleteListener
            }
// check if task is completed


        }.result.toString()
    }



    private fun notify(title: String?, message: String?) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "notification_channel")
                .setSmallIcon(R.drawable.btn_plus)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(123, builder.build())
    }


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            notify(message.notification?.title, message.notification?.body)
        }
    }

}