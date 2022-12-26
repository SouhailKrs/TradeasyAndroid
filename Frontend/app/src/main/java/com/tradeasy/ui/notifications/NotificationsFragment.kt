package com.tradeasy.ui.notifications

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.R
import com.tradeasy.databinding.FragmentNotificationsBinding
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    private lateinit var binding: FragmentNotificationsBinding
    private val viewModel: NotificationsViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

// get the number of notifications from the shared prefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {


// get item count of notification


        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        setupView ()

        observe()

        setUpRecyclerView()
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {
                viewModel.fetchNotifications()
            }
        }
        (activity as MainActivity?)?.setupToolBar("Notifications", false, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.numberOfNotifications.text = HtmlCompat.fromHtml(
            "<span>You have </span><span><font color=#4B78A8>${sharedPrefs.getUser()?.notifications?.size}</span><span> notifications today </span>",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
    }


    // remove the recycleview divider


    private fun setUpRecyclerView() {

        val mAdapter = NotificationsAdapter(mutableListOf(), onItemClick = {

        })

        binding.notificationsRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
        val itemTouchHelper = ItemTouchHelper(object : SwipeHelper(binding.notificationsRV) {
            override fun instantiateUnderlayButton(position: Int): List<UnderlayButton> {
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


        val greenColorValue: Int = Color.parseColor("#00ff00")

        val idDrawable: Int = com.tradeasy.R.drawable.ic_outline_delete_24
        val bitmap: Bitmap = getBitmapFromVectorDrawable(context, idDrawable)
        return SwipeHelper.UnderlayButton(

            requireActivity(), bitmap,
            // set a custom color for the button

            R.color.buttonColor, object : SwipeHelper.UnderlayButtonClickListener {
                override fun onClick() {

                }
            })
    }


    private fun observe() {
        observeState()
        observeNotifications()
    }

    private fun observeState() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeNotifications() {
        viewModel.mNotifications.flowWithLifecycle(
            viewLifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        ).onEach { notifications ->
                handleSavedProducts(notifications)

        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: NotificationsFragmentState) {
        when (state) {
            is NotificationsFragmentState.IsLoading -> handleLoading(state.isLoading)
            is NotificationsFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()

            is NotificationsFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
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


private fun setupView (){
    if (sharedPrefs.getUser() == null) {

        findNavController().navigate(R.id.loginFragment)

    }
    binding.numberOfNotifications.visibility = if(sharedPrefs.getUser()?.notifications?.size != 0) View.VISIBLE else View.GONE
    binding.noNotificationsIcon.visibility = if(sharedPrefs.getUser()?.notifications?.size == 0) View.VISIBLE else View.GONE
    binding.noNotificationsTxt.visibility = if(sharedPrefs.getUser()?.notifications?.size == 0) View.VISIBLE else View.GONE
    binding.notificationsDay.visibility = if(sharedPrefs.getUser()?.notifications?.size != 0) View.VISIBLE else View.GONE
    binding.notificationsRV.visibility = if(sharedPrefs.getUser()?.notifications?.size != 0) View.VISIBLE else View.GONE

}
    private fun handleSavedProducts(notification: List<Notification>) {
        binding.notificationsRV.adapter?.let {
            if (it is NotificationsAdapter) {
                it.updateList(notification)
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }
}