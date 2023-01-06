package com.tradeasy.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
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
import com.tradeasy.ui.home.ForBidViewModel

import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val deviceViewModel: DeviceViewModel by viewModels()
private val viewModel: ForBidViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var binding: ActivityMainBinding

    @OptIn(NavigationUiSaveStateControl::class)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
setKeepOnScreenCondition(viewModel._isLoading::value)
            // set it for 3 seconds

        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

      // ask for notification permission with the new android 1
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

        deviceViewModel.resToken.observe(this){
            when (it){
                is UiState.Success -> {

                    sharedPrefs.setNotificationToken(it.data)

                }
                is UiState.Failure -> {

                }
                UiState.Loading -> {

                }

            }
        }
        deviceViewModel.getToken()
    }
// function to check if device is connected to wifi

    // bottom nav view items on click listener
fun setupToolBar(title: String, doneVisibility: Boolean=false,progressVisibility: Boolean=false,opacity: Float=1f) {
        binding.toolbar.toolbarTitle.text = title
        binding.toolbar.toolbarRightText.visibility = if (doneVisibility) View.VISIBLE else View.GONE
        binding.toolbar.toolbarRightText.alpha = opacity
        binding.toolbar.progressBar.visibility = if (progressVisibility) View.VISIBLE else View.GONE
    }
// a function that prints hello world every 5 seconds


    // make firesbase notification service
}