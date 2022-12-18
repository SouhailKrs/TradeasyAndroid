package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.ui.navigation.profileToLogin
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val user = sharedPrefs.getUser()
      
        setupToolbar()
        if (user == null) {
            fragmentSetupOffline()
        } else {
            if (sharedPrefs.getUser()?.profilePicture == "None") {
                binding.profilePicture.setImageResource(com.tradeasy.R.drawable.default_profile_picture)
            }

            sharedPrefs.getUser()?.let {
                binding.username.text = it.username

            }
            fragmentSetupOnline()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        // WHAT TO DO WHEN USER IS NOT LOGGED IN


        binding.logoutConstraint.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                sharedPrefs.clear()
                findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }


    // where to go when the user in offline
    private fun constraintsOnClickOffline() {
        binding.savedConstraint.setOnClickListener {
            profileToLogin(requireView())
        }
        binding.bidsConstraint.setOnClickListener {
            profileToLogin(requireView())
        }
        binding.purchasesConstraint.setOnClickListener {
            profileToLogin(requireView())
        }
        binding.recentlyViewedConstraint.setOnClickListener {
            profileToLogin(requireView())
        }
        binding.paymentConstraint.setOnClickListener {
            profileToLogin(requireView())
        }


    }

    private fun constraintsOnClickOnline() {
        binding.savedConstraint.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_savedProductsFragment)
        }
        binding.bidsConstraint.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.bidsFragment)
        }
        binding.purchasesConstraint.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.purchasesFragment)
        }
        binding.recentlyViewedConstraint.setOnClickListener {

        }
        binding.paymentConstraint.setOnClickListener {
            profileToLogin(requireView())
        }


    }

    private fun fragmentSetupOnline() {
        constraintsOnClickOnline()
        binding.editProfileBtn.text = "Edit Profile"
        binding.username.visibility = View.VISIBLE
        binding.logoutConstraint.visibility = View.VISIBLE
        binding.profileSpacer.layoutParams.height = 200
        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_editProfileFragment)
            val item = requireActivity().findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).menu.findItem(R.id.profileFragment)
            NavigationUI.onNavDestinationSelected(item, findNavController())
        }

    }

    private fun fragmentSetupOffline() {
        binding.editProfileBtn.text = "Login"
        binding.username.visibility = View.GONE
        binding.logoutConstraint.visibility = View.GONE
        binding.profileSpacer.layoutParams.height = 50
        constraintsOnClickOffline()
        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)

        }


    }

    private fun setupToolbar() {
        val toolbarTitle: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbarTitle.text = "Profile"


        val toolbarTxt: TextView =
            requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        toolbarTxt.visibility = View.GONE
    }

}