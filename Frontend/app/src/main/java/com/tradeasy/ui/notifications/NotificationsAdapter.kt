package com.tradeasy.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.databinding.NotificationItemBinding
import com.tradeasy.domain.user.entity.Notification


class NotificationsAdapter(var notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {
    class NotificationViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        //        val notificationContent : TextView = itemView.findViewById(com.tradeasy.R.id.notificationContent)
//        val oneNotificationDate: TextView = itemView.findViewById(com.tradeasy.R.id.notificationDate)
//        val notificationIcon: ImageView = itemView.findViewById(com.tradeasy.R.id.notificationIcon)
        fun bind(notification: Notification) {
            binding.notificationTitle.text = notification.title
            binding.notificationDesc.text = notification.description
            binding.notificationDate.text = notification.date.toString()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(itemView)

    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val notification = notifications[position]
        holder.bind(notification)

//        if(notification.type=="Sold") {
//            holder.notificationIcon.setImageResource(com.tradeasy.R.drawable.ic_baseline_sell_24)
//        }
//        if(notification.type=="Bid") {
//            holder.notificationIcon.setImageResource(com.tradeasy.R.drawable.notifications_gavel)
//        }


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