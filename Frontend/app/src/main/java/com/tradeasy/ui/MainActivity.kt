package com.tradeasy.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.tradeasy.R.layout.activity_main)
        //val navView = findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView)
//        navView.setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.black));
       // navView.setBackgroundDrawable( ColorDrawable(android.graphics.Color.TRANSPARENT));
        val navHostFragment =
            supportFragmentManager.findFragmentById(com.tradeasy.R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavView = findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView)
        NavigationUI.setupWithNavController(bottomNavView, navController)
    }
    // change action bar color to white



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
      inflater.inflate(com.tradeasy.R.menu.action_bar, menu)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, com.tradeasy.R.color.appBackground)))
        supportActionBar!!.elevation = 0F;
        // return true so that the menu pop up is opened
        supportActionBar!!.setDisplayShowTitleEnabled(false);
        return true

    }
}