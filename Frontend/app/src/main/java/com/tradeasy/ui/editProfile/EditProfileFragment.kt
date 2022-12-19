package com.tradeasy.ui.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.databinding.FragmentEditProfileBinding
import com.tradeasy.ui.navigation.editProfileToUpdateEmail
import com.tradeasy.ui.navigation.editProfileToUpdatePassword
import com.tradeasy.ui.navigation.editProfileToUpdatePn
import com.tradeasy.ui.navigation.editProfileToUpdateUsername
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint

class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs

// make this fragment a child of ProfileFragment


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)

// change selected item in bottom navigation bar to profile fragment
        toolbar.text = "Edit profile"
        constraintsNavigation()

        sharedPrefs.getUser()?.let {
            binding.usernameView.text = it.username
            binding.emailView.text = it.email
            binding.phoneNumberView.text = it.phoneNumber.toString()

        }

        return binding.root
    }


    override fun onResume() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onResume()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.VISIBLE


    }


    private fun constraintsNavigation() {

        binding.updatePasswordConstraint.setOnClickListener {
            editProfileToUpdatePassword(requireView())
        }
        binding.updateUsernameConstraint.setOnClickListener {


            editProfileToUpdateUsername(requireView())
        }
        binding.updateEmailConstraint.setOnClickListener {

            editProfileToUpdateEmail(requireView())

        }
        binding.updatePnConstraint.setOnClickListener {

            editProfileToUpdatePn(requireView())

        }


    }

// pop this fragment from backstack when back button is pressed





}