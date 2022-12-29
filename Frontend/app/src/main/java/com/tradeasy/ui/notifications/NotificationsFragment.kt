package com.tradeasy.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.data.user.remote.dto.DeleteNotificationReq
import com.tradeasy.databinding.FragmentNotificationsBinding
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.RecentlyViewedDataViewModel
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.ui.notifications.deleteNotification.DeleteNotificationFragmentState
import com.tradeasy.ui.notifications.deleteNotification.DeleteNotificationViewModel
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private val notificationsViewModel: NotificationsViewModel by viewModels()
    private val deleteNotificationVM: DeleteNotificationViewModel by viewModels()
    private val recentlyViewedDataViewModel: RecentlyViewedDataViewModel by activityViewModels()
    val notificationList = mutableListOf<Notification>()
    var selectedItem: Number? = 0
    var selectedItemPosition: Int? = 0
    var nbrOfNotification: Number? = 0
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private var nbrOfNotifications by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setUpRecyclerView()
        observeNotifications()
        observeDeleteNotifications()
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {

                notificationsViewModel.fetchNotifications()
            }
        }


    }


    // remove the recycleview divider

// function to do swipe to delete on the notification


    private fun setUpRecyclerView() {

        val mAdapter = NotificationsAdapter(mutableListOf(), onItemClick = {

        })
        binding.notificationsRV.apply {

            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, true)
        }

        nbrOfNotifications = mAdapter.notification.size


        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.notificationsRV) {

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                // get the data from the position
                val notificationPosition = mAdapter.notification[position]
                selectedItemPosition = position
                selectedItem = notificationPosition.date

                super.onSwiped(viewHolder, direction)
            }

            override fun instantiateUnderlayButton(position: Int): List<SwipeHelper.UnderlayButton> {
                val buttons: List<UnderlayButton>
                val deleteButton = deleteButton()
                buttons = listOf(deleteButton)
                return buttons
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.notificationsRV)
    }

    @SuppressLint("ResourceType")
    private fun deleteButton(): SwipeHelper.UnderlayButton {

        Color.parseColor("#00ff00")
        val idDrawable: Int = com.tradeasy.R.drawable.ic_outline_delete_24
        val bitmap: Bitmap = getBitmapFromVectorDrawable(context, idDrawable)
        return SwipeHelper.UnderlayButton(requireActivity(), bitmap,

            com.tradeasy.R.color.buttonColor, object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {
                    deleteNotificationVM.deleteNotification(DeleteNotificationReq(selectedItem))
                    for (i in notificationList) {

                        if (i.date == selectedItem) {
                            notificationList.remove(i)
                            break
                        }

                    }
                    println("adapter size is " + binding.notificationsRV.adapter!!.itemCount)
                    setNewUser(notificationList)
when (binding.notificationsRV.adapter!!.itemCount) {
                        1 -> {
                            binding.notificationsRV.visibility = View.GONE
                            binding.notificationsDay.visibility = View.GONE
                            binding.noNotificationsIcon.visibility = View.VISIBLE
                            binding.noNotificationsTxt.visibility = View.VISIBLE

                        }

                    }
                    binding.notificationsRV.adapter!!.notifyDataSetChanged()
                    println("shred pref size is " + sharedPrefs.getUser()!!.notifications!!.size)
                    Snackbar.make(binding.root, "Notification deleted ", Snackbar.LENGTH_LONG)
                        .show()

                }
            }


        )
    }

// recyclerview observer to get the notifications


    private fun observeNotifications() {
        observeNotificationState()
        observeNotificationsVM()
    }

    private fun observeNotificationState() {
        notificationsViewModel.mState.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).onEach { state ->
                handleNotificationState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeNotificationsVM() {
        notificationsViewModel.mNotifications.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { notifications ->
            handleNotifications(notifications)

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleNotificationState(state: NotificationsFragmentState) {
        when (state) {
            is NotificationsFragmentState.IsLoading -> handleLoading(state.isLoading)
            is NotificationsFragmentState.ShowToast -> {
                Toast.makeText(
                    requireActivity(), state.message, Toast.LENGTH_SHORT
                ).show()

            }
          is NotificationsFragmentState.SuccessLoading-> handleSuccessLoadingNotification()
            is NotificationsFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun handleSuccessLoadingNotification() {
        binding.notificationConstraint.visibility = View.VISIBLE
        binding.notificationLoading.visibility = View.GONE
        println("loading done ")
    }
    private fun handleNotifications(notification: List<Notification>) {
        binding.notificationsRV.adapter?.let {
            if (it is NotificationsAdapter) {
                it.updateList(notification)
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        binding.notificationConstraint.visibility = View.GONE
        binding.notificationLoading.visibility = View.VISIBLE
        println("loading")
    }

    fun getTimeAgo(time: Long): String? {
        val SECOND_MILLIS = 1000
        val MINUTE_MILLIS = 60 * SECOND_MILLIS
        val HOUR_MILLIS = 60 * MINUTE_MILLIS
        val DAY_MILLIS = 24 * HOUR_MILLIS
        val WEEK_MILLIS = 7 * DAY_MILLIS
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
                "1 hour ago"
            }
            diff < 24 * HOUR_MILLIS -> {
                if (diff / HOUR_MILLIS == 1L) {
                    "1 hour ago"
                } else {
                    (diff / HOUR_MILLIS).toString() + " hours ago"
                }

            }
            diff < 48 * HOUR_MILLIS -> {
                if (diff / DAY_MILLIS == 1L) {
                    "1 day ago"
                } else {
                    (diff / DAY_MILLIS).toString() + " days ago"
                }

            }
            // get weeks ago

            else -> {
                if (diff / DAY_MILLIS == 1L) {
                    "1 day ago"
                } else {
                    (diff / DAY_MILLIS).toString() + " days ago"
                }

            }
        }
    }

    private fun setupView() {


        (activity as MainActivity?)?.setupToolBar("Notifications", false, false)
        if (sharedPrefs.getUser() == null) {

            findNavController().navigate(com.tradeasy.R.id.loginFragment)

        }

        setupNotificationView()
    }

    private fun setupNotificationView() {

        binding.noNotificationsIcon.visibility =
            if (sharedPrefs.getUser()?.notifications?.size == 0) View.VISIBLE else View.GONE
        binding.noNotificationsTxt.visibility =
            if (sharedPrefs.getUser()?.notifications?.size == 0) View.VISIBLE else View.GONE
        binding.notificationsDay.visibility =
            if (sharedPrefs.getUser()?.notifications?.size != 0) View.VISIBLE else View.GONE
        binding.notificationsRV.visibility =
            if (sharedPrefs.getUser()?.notifications?.size != 0) View.VISIBLE else View.GONE
    }

    private fun observeDeleteNotifications() {
        observeDeleteNotificationState()
        observeDeleteNotificationsVM()
    }

    private fun observeDeleteNotificationState() {
        deleteNotificationVM.mState.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).onEach { state ->
                handleDeleteNotificationState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeDeleteNotificationsVM() {
        deleteNotificationVM.mNotifications.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { notifications ->
            handleDeleteNotifications(notifications)

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleDeleteNotificationState(state: DeleteNotificationFragmentState) {
        when (state) {
            is DeleteNotificationFragmentState.IsLoading -> handleDeleteLoading(state.isLoading)
            is DeleteNotificationFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()

            is DeleteNotificationFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleDeleteNotifications(notification: List<Notification>) {
        binding.notificationsRV.adapter?.let {
            if (it is NotificationsAdapter) {
                it.updateList(notification)
            }
        }
    }

    private fun handleDeleteLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }


// function to remove items from the recyclerview


    private fun setNewUser(list: MutableList<Notification>) {

        val user = User(
            sharedPrefs.getUser()?.username,
            sharedPrefs.getUser()?.phoneNumber,
            sharedPrefs.getUser()?.email,
            sharedPrefs.getUser()?.password,
            sharedPrefs.getUser()?.profilePicture,
            sharedPrefs.getUser()?.isVerified,
            sharedPrefs.getUser()?.notificationToken,
            list,
            sharedPrefs.getUser()?.savedProducts,
            sharedPrefs.getUser()?.otp,
            sharedPrefs.getUser()?.countryCode,
            sharedPrefs.getUser()?.token,


            )
        sharedPrefs.setUser(user)

    }


}