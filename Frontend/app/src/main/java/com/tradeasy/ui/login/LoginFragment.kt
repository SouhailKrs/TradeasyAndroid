package com.tradeasy.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentLoginBinding
import com.tradeasy.domain.model.UserLogin
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility=View.GONE
        binding.newMember.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        login();
    }
    private fun login(){
        binding.registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            if(username.isNotEmpty()  || password.isNotEmpty())
            {
               val userLogin= UserLogin(username,password)
                viewModel.login( userLogin)
            } else {
                binding.usernameField.error = "Please enter username"
                binding.passwordField.error = "Please enter password"
            }





        }}
}