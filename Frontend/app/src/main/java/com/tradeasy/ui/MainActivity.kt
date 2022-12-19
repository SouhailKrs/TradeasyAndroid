package com.tradeasy.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUiSaveStateControl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.DeviceViewModel
import com.tradeasy.databinding.ActivityMainBinding
import com.tradeasy.ui.home.HomeViewModel
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val deviceViewModel: DeviceViewModel by viewModels()
private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var binding: ActivityMainBinding

    @OptIn(NavigationUiSaveStateControl::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
setKeepOnScreenCondition(viewModel._isLoading::value)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        //  println("here   " + FirebaseMessageReceiver().getToken())
        //FirebaseMessageReceiver().getToken()
        //requestedOrientation = android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val channel = NotificationChannel(
            "notification_channel", "notification_channel", NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)

// remove dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        val toolbar: Toolbar = findViewById(com.tradeasy.R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)


// NAV HOST WITH BOTTOM NAVIGATION VIEW
        val navHostFragment =
            supportFragmentManager.findFragmentById(com.tradeasy.R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavView =
            findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavView, navController,false)
// set selected item in bottom navigation bar to profile fragment

        bottomNavView.setOnItemReselectedListener { item ->
            // Pop everything up to the reselected item
            val reselectedDestinationId = item.itemId
            navController.popBackStack(reselectedDestinationId, inclusive = false)
        }
        // START APP IN FULLSCREEN
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // disable bottom nav view tooltip

        //FirebaseMessageReceiver().getToken()
        /*var token: String = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@addOnCompleteListener
            }
            token = task.result
            println("token issss " + task.result)

            sharedPrefs.setNotificationToken(task.result)
            println("AAAAA" + sharedPrefs.getNotificationToken())

        }*/
        deviceViewModel.resToken.observe(this){
            when (it){
                is UiState.Success -> {
                    println("success "+ it.data)
                    sharedPrefs.setNotificationToken(it.data)
                    println(sharedPrefs.getNotificationToken())
                }
                is UiState.Failure -> {
                    println("failure: "+it.message)
                }
                UiState.Loading -> {
                    println("loading")
                }

            }
        }
        deviceViewModel.getToken()
    }
    // bottom nav view items on click listener

}