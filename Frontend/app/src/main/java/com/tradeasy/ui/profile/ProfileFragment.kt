package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.domain.model.User
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserDetailsViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onResume() {
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        super.onResume()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        binding.editProfileBtn.setOnClickListener{findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)}
        viewModel.getData()
        observe()
        binding.logoutBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                sharedPrefs.clear()
                findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    private fun observe() {
        println("observe")
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: ActivityState) {
        when (state) {
            is ActivityState.Init->Unit
            is ActivityState.SuccessGettingUserData -> {

                val username= binding.username
                val email = binding.email
                username.text = state.user.username
                email.text = state.user.email
                if(state.user.profilePicture=="None"){

                    binding.profilePicture.setImageResource(R.drawable.default_profile_picture)
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

}