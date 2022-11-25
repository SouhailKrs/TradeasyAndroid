package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.ui.navigation.profileToLogin
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onResume() {
        super.onResume()
    }

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val toolbar :TextView= requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbar.text = "Profile"
        binding = FragmentProfileBinding.inflate(inflater, container, false)


        val toolbarTxt: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        toolbarTxt.visibility = View.GONE
        val user = sharedPrefs.getUser()
        if (user == null) {

            binding.editProfileBtn.text = "Login"
            binding.username.visibility = View.GONE
            binding.logoutConstraint.visibility = View.GONE
            binding.profileSpacer.layoutParams.height = 50
            constraintsOnClickOffline()
            binding.editProfileBtn.setOnClickListener {
                findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)

            }
        } else {

            binding.editProfileBtn.text = "Edit Profile"
            binding.username.visibility = View.VISIBLE
            binding.logoutConstraint.visibility = View.VISIBLE
            binding.username.text = user.username
            binding.profileSpacer.layoutParams.height = 200
            binding.editProfileBtn.setOnClickListener {
                findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_editProfileFragment)


            }



            if (sharedPrefs.getUser()?.profilePicture == "None") {

                binding.profilePicture.setImageResource(com.tradeasy.R.drawable.default_profile_picture)
            }
            sharedPrefs.getUser()?.let {
                binding.username.text = it.username


            }

        }
        return binding.root
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar :TextView= requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbar.text = "Profile"
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.VISIBLE



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
    private fun constraintsOnClickOffline(){
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





}