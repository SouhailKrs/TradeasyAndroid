package com.tradeasy.ui

<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tradeasy.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
=======
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
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
        NavigationUI.setupWithNavController(bottomNavView, navController)

        // ACTION BAR PARAMS
//        supportActionBar!!.setBackgroundDrawable(
//            ColorDrawable(
//                ContextCompat.getColor(
//                    this, com.tradeasy.R.color.white
//                )
//            )
//        )

 //change action bar title color



       // this.supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

        // Displaying the custom layout in the ActionBar


       // supportActionBar!!.elevation = 0F
        //supportActionBar!!.setDisplayShowTitleEnabled(true)

        // START APP IN FULLSCREEN
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        // disable bottom nav view tooltip


    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//
//        val inflater = menuInflater
//        inflater.inflate(com.tradeasy.R.menu.app_bar, menu)
//        val item: MenuItem = menu.findItem(com.tradeasy.R.id.shopping_cart)
//        item.isVisible = sharedPrefs.getUser() != null
//
//
//        return true
//
//    }
//
//override fun onPrepareOptionsMenu(menu: Menu): Boolean {
//    val item: MenuItem = menu.findItem(com.tradeasy.R.id.shopping_cart)
//    item.isVisible = sharedPrefs.getUser() != null
//
//    // refresh menu
//    invalidateOptionsMenu()
//    return true
//
//}

>>>>>>> Souhail
}