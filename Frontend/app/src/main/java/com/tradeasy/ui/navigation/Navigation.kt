package com.tradeasy.ui.navigation



import android.view.View
import androidx.navigation.Navigation.findNavController

import com.tradeasy.R

class Navigation {
// nav controller
    fun profileToLogin(view: View) {
        findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
    }


    fun loginToProfile(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_loginFragment_to_profileFragment)
    }

    fun loginToRegister(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_loginFragment_to_registerFragment)
    }

    fun registerToLogin(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_registerFragment_to_loginFragment)
    }
    fun registerToProfile(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_registerFragment_to_profileFragment)
    }
    fun registerToHome(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_registerFragment_to_homeFragment)
    }
    fun loginToHome(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_loginFragment_to_homeFragment)
    }
fun notificationsToLogin(view: View) {
        findNavController(view).navigate(com.tradeasy.R.id.action_notificationsFragment_to_loginFragment)
    }


}