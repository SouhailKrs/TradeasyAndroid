package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.domain.model.User
import com.tradeasy.ui.navigation.profileToLogin
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val viewModel: UserDetailsViewModel by viewModels()
    private lateinit var binding: FragmentProfileBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbar.text = "Profile"

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


        }
        return binding.root
    }






    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.VISIBLE



        // WHAT TO DO WHEN USER IS NOT LOGGED IN

        viewModel.getData()
        observe()
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

    private fun observe() {
        println("observe")
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: ActivityState) {
        when (state) {
            is ActivityState.Init -> Unit
            is ActivityState.SuccessGettingUserData -> {

                val username = binding.username

                username.text = state.user.username

                if (state.user.profilePicture == "None") {

                    binding.profilePicture.setImageResource(com.tradeasy.R.drawable.default_profile_picture)
                }

            }
            is ActivityState.ErrorGettingUserData -> handleErrorLogin(state.rawResponse)
            is ActivityState.ShowToast -> {
                Toast.makeText(requireActivity(), state.message, Toast.LENGTH_SHORT).show()
            }
            is ActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorLogin(response: WrappedResponse<User>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
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