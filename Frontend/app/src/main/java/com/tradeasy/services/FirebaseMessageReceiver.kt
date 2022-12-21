package com.tradeasy.services

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirebaseMessageReceiver : FirebaseMessagingService() {

 var sharedPrefs: SharedPrefs? = null
    // lazy
    private val ADMIN_CHANNEL_ID = "admin_channel"
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //FirebaseMessaging.getInstance().subscribeToTopic("general")

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        if (message.notification != null) {
            notify(message.notification?.title, message.notification?.body)
        }
    }

    private fun notify(title: String?, message: String?) {
//        val notificationLayout = RemoteViews(packageName, com.tradeasy.R.layout.notification_layout)
//        notificationLayout.setTextViewText(com.tradeasy.R.id.pushNotificationTitle, title)
//        notificationLayout.setTextViewText(com.tradeasy.R.id.pushNotificationMsg, message)
//        notificationLayout.setImageViewResource(com.tradeasy.R.id.notificationImg, com.tradeasy.R.drawable.app_logo_notification)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "notification_channel")
                .setSmallIcon(com.tradeasy.R.drawable.app_logo_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
        // set custom notification layout
        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(123, builder.build())
    }


}