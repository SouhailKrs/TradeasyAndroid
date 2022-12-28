package com.tradeasy.ui.profile.pushNotifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tradeasy.databinding.FragmentPushNotificationsBinding
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PushNotificationsFragment : Fragment() {
private lateinit var binding: FragmentPushNotificationsBinding
@Inject
lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPushNotificationsBinding.inflate(layoutInflater)
        setupNotification ()
        return binding.root
    }

private  fun setupNotification (){
    binding.notificationsSwitch.isChecked = sharedPrefs.getNotificationAllowed()
    binding.notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
        sharedPrefs.setNotificationAllowed(isChecked)
    }



}
}