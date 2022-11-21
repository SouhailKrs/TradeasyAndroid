package com.tradeasy.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentNotificationsBinding
import com.tradeasy.domain.model.Notification
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
        private lateinit var notificationCount: String
    private var toast: Toast? = null
    private lateinit var notificationList: MutableList<Notification>
    private var numberOfNotifications :TextView? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)

        toolbar.text = "Notifications"
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        // hide bottom navigation bar
        val toolbarTxt: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        toolbarTxt.visibility = View.GONE

        setUpRecyclerView()
        return binding.root
    }


    private fun getTimeAgo(time: Long): String? {
        val SECOND_MILLIS = 1000
        val MINUTE_MILLIS = 60 * SECOND_MILLIS
        val HOUR_MILLIS = 60 * MINUTE_MILLIS
        val DAY_MILLIS = 24 * HOUR_MILLIS
        var timeAgo = time

        if (timeAgo < 1000000000000L) {
            timeAgo *= 1000
        }

        val now = System.currentTimeMillis()
        if (timeAgo > now || timeAgo <= 0) {
            return null
        }

        val diff: Long = now - timeAgo
        return when {
            diff < MINUTE_MILLIS -> {
                "just now"
            }
            diff < 2 * MINUTE_MILLIS -> {
                "1 minute ago"
            }
            diff < 50 * MINUTE_MILLIS -> {
                (diff / MINUTE_MILLIS).toString() + " minutes ago"
            }
            diff < 90 * MINUTE_MILLIS -> {
                "an hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                (diff / HOUR_MILLIS).toString() + " hours ago"
            }
            diff < 48 * HOUR_MILLIS -> {
                "yesterday"
            }
            else -> {
                (diff / DAY_MILLIS).toString() + " days ago"
            }
        }


    }

    // remove the recycleview divider


    private fun setUpRecyclerView() {

        val sCalendar = Calendar.getInstance()
        val dayLongName: String? =
            sCalendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())

// remove the recycleview divider

        // timestamp
        val timestamp = 1669063578092
        val time = getTimeAgo(timestamp)
        println("time $time")
        notificationList = mutableListOf<Notification>(

            Notification("You have a new bid on your product", "$time", "Bid"),
            Notification("Your item has been sold", "$time", "Bid"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),
            Notification("Your item has been sold", "$time", "Sold"),


            )


        if (notificationList.size == 1) {

            notificationCount = notificationList.size.toString() + " notification"


        }
        if (notificationList.isEmpty()) {
            notificationCount = " no notifications"
        } else if (notificationList.size > 1) {
            notificationCount = notificationList.size.toString() + " notifications"
        }


         numberOfNotifications = binding.numberOfNotifications
        numberOfNotifications!!.text = HtmlCompat.fromHtml(
            "<span>You have </span><span><font color=#4B78A8>$notificationCount</span><span> today </span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.notificationsDay.text = dayLongName


        val notificationRV = binding.notificationsRV
        val adapter = NotificationsAdapter(notificationList)
        notificationRV.adapter = adapter

        notificationRV.setHasFixedSize(true)
        // remove divider
        notificationRV.addItemDecoration(
            DividerItemDecoration(
                notificationRV.context,
                DividerItemDecoration.VERTICAL,

            )
        )

        for (i in 0 until notificationRV.itemDecorationCount) {
            if (notificationRV.getItemDecorationAt(i) is DividerItemDecoration) notificationRV.removeItemDecorationAt(
                i
            )
        }
       // notificationRV.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        notificationRV.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(notificationRV) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
                var buttons = listOf<UnderlayButton>()
                val deleteButton = deleteButton(position)


                buttons = listOf(deleteButton)


                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(notificationRV)
    }
    @SuppressLint("ResourceType")
    private fun deleteButton(position: Int): SwipeHelper.UnderlayButton {


        val greenColorValue:Int = Color.parseColor("#00ff00")

        val idDrawable: Int = com.tradeasy.R.drawable.ic_outline_delete_24
        val bitmap: Bitmap = getBitmapFromVectorDrawable(context, idDrawable)
        return SwipeHelper.UnderlayButton(

            requireActivity(),
            bitmap,
            // set a custom color for the button

            R.color.buttonColor, object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    val deletedNotification: Notification = notificationList[position]

                    val adapter = binding.notificationsRV.adapter as NotificationsAdapter
                    notificationList.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    val numberOfNotifications = binding.numberOfNotifications
                    notificationCount = notificationList.size.toString() + " notifications"
                    numberOfNotifications.text = HtmlCompat.fromHtml(
                        "<span>You have </span><span><font color=#4B78A8>$notificationCount</span><span> today </span>",
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                    println(notificationCount)
                    Snackbar.make(binding.root, "Notification deleted ", Snackbar.LENGTH_LONG)
                        .setAction("Undo") {
                            notificationList.add(position, deletedNotification)
                            notificationCount = notificationList.size.toString() + " notifications"

                            numberOfNotifications.text = HtmlCompat.fromHtml(
                                "<span>You have </span><span><font color=#4B78A8>$notificationCount</span><span> today </span>",
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            )
                            adapter.notifyItemInserted(position)
                        }.show()

                }
            })
    }

}