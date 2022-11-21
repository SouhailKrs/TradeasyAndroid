package com.tradeasy.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.domain.model.Notification


class NotificationsAdapter(var notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val notificationContent : TextView = itemView.findViewById(com.tradeasy.R.id.notificationContent)
        val oneNotificationDate: TextView = itemView.findViewById(com.tradeasy.R.id.oneNotificationDate)
        val notificationIcon: ImageView = itemView.findViewById(com.tradeasy.R.id.notificationIcon)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(com.tradeasy.R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)

    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val notification = notifications[position]
        holder.notificationContent.text = notification.content
        holder.oneNotificationDate.text = notification.date
        if(notification.type=="Sold") {
            holder.notificationIcon.setImageResource(com.tradeasy.R.drawable.ic_baseline_sell_24)
        }
        if(notification.type=="Bid") {
            holder.notificationIcon.setImageResource(com.tradeasy.R.drawable.notifications_gavel)
        }


    }

    override fun getItemCount(): Int {

        return notifications.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

//    fun updateNotifications(notifications: List<Notification>) {
//        this.notifications = notifications
//        notifyDataSetChanged()
//    }


}