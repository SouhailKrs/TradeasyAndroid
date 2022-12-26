package com.tradeasy.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tradeasy.R
import com.tradeasy.databinding.NotificationItemBinding
import com.tradeasy.domain.user.entity.Notification

class NotificationsAdapter(private val notification: MutableList<Notification>, val onItemClick:(Notification)->Unit) : RecyclerView.Adapter<NotificationsAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            NotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = notification[position]
        holder.bind(currentItem)
        holder.itemView.setOnClickListener{
            onItemClick(currentItem)
        }

    }

    override fun getItemCount(): Int {

        return notification.size

    }
    fun updateList(mnotification: List<Notification>){
        notification.clear()
        notification.addAll(mnotification)
        notifyDataSetChanged()
    }





    class MyViewHolder(val binding : NotificationItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
// convert number to long

            binding.notificationTitle.text = notification.title
            binding.notificationDesc.text = notification.description
            binding.notificationDate.text = NotificationsFragment().getTimeAgo(notification.date!!.toLong())
            binding.notificationIcon.setImageResource(R.drawable.notifications_gavel)

        }

    }

}
