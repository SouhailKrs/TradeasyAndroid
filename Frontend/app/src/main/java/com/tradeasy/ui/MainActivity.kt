package com.tradeasy.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.databinding.ActivityMainBinding
import com.tradeasy.ui.login.LoginFragment
import com.tradeasy.ui.profile.ProfileFragment
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint


class MainActivity : AppCompatActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val profileFragment = ProfileFragment()
    private val loginFragment = LoginFragment()


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)



        val view = binding.root
        setContentView(view)

        //remove dark mode
        

      // handleNavigation()
// NAV HOST WITH BOTTOM NAVIGATION VIEW
         val navHostFragment =
            supportFragmentManager.findFragmentById(com.tradeasy.R.id.fragmentContainerView) as NavHostFragment
         val navController = navHostFragment.navController
        val bottomNavView =
            findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavView, navController)

        // ACTION BAR PARAMS
        supportActionBar!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    com.tradeasy.R.color.appBackground
                )
            )
        )
        supportActionBar!!.elevation = 0F
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        // START APP IN FULLSCREEN
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // disable bottom nav view tooltip


    }
    // change action bar color to white


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(com.tradeasy.R.menu.action_bar, menu)

        return true

    }
/*private fun handleNavigation(){
    val navHostFragment =
        supportFragmentManager.findFragmentById(com.tradeasy.R.id.fragmentContainerView) as NavHostFragment
    val navController = navHostFragment.navController
    val graph = navController.navInflater.inflate(com.tradeasy.R.navigation.navigation)
    // if user is logged in then show profile fragment

    if(sharedPrefs.getUser() != null){

   // change navigation start destination if user is logged in

        graph.setStartDestination(com.tradeasy.R.id.homeFragment)
        navController.graph = graph
    }
    else{
        // change navigation start destination if user is not logged in

        graph.setStartDestination(com.tradeasy.R.id.loginFragment)
        navController.graph = graph
    }*/


/*private  fun handleBottomNavView(){
    binding.bottomNavigationView.menu.findItem(com.tradeasy.R.id.profileFragment).setOnMenuItemClickListener {


        if(sharedPrefs.getUser() == null){
            supportFragmentManager.beginTransaction().replace(com.tradeasy.R.id.fragmentContainerView, loginFragment).commit()
            true
        }
        else{
            false
        }



    }*/






}