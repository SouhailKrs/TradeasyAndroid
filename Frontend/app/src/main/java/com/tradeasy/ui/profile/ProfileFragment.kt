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
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.navigation.profileToLogin
import com.tradeasy.ui.profile.deleteAccount.DeleteAccountState
import com.tradeasy.ui.profile.deleteAccount.DeleteAccountViewModel
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import com.tradeasy.utils.imageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: DeleteAccountViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Your Title"
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val user = sharedPrefs.getUser()

        (activity as MainActivity?)?.setupToolBar("Profile", false, false)

        if (user == null) {
            fragmentSetupOffline()
        } else {


            sharedPrefs.getUser()?.let {
                binding.username.text = it.username

            }
            fragmentSetupOnline()
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  setupToolbar()
        deleteAccount()
        observe()
        logout()
//HomeFragment().setupToolBar("test",true)

        // WHAT TO DO WHEN USER IS NOT LOGGED IN
    }


    private fun deleteAccount() {
        binding.deleteConstraint.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteAccount()
                    findNavController().navigate(R.id.homeFragment)
                    sharedPrefs.clearUser()
                }.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: DeleteAccountState) {
        when (state) {
            is DeleteAccountState.Init -> Unit
            is DeleteAccountState.ErrorDelete -> handleErrorUpdate(state.rawResponse)
            is DeleteAccountState.SuccessDelete -> handleSuccessUpdate()
            is DeleteAccountState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is DeleteAccountState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorUpdate(response: WrappedResponse<String>) {

        // snackbar
        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleSuccessUpdate() {
        sharedPrefs.clearUser()
        findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)
        Snackbar.make(requireView(), "Username Updated Successfully", Snackbar.LENGTH_LONG).show()
        //findNavController().navigate(R.id.action_updatePasswordFragment_to_editProfileFragment)
    }

    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

    // where to go when the user in offline
    private fun constraintsOnClickOffline() {
        binding.savedConstraint.setOnClickListener {
            profileToLogin(requireView())
        }
        binding.bidsConstraint.setOnClickListener {
            profileToLogin(requireView())
        }

        binding.recentlyViewedConstraint.setOnClickListener {
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

        binding.recentlyViewedConstraint.setOnClickListener {

        }


    }

    private fun fragmentSetupOnline() {
        constraintsOnClickOnline()
        binding.editProfileBtn.text = "Edit Profile"
        binding.accountCardView.visibility = View.VISIBLE
        binding.accountTxtView.visibility = View.VISIBLE
        binding.profileSpacer.layoutParams.height = resources.displayMetrics.heightPixels / 7

// get screen height
        if (!sharedPrefs.getUser()?.profilePicture.isNullOrEmpty()) {
            imageLoader(sharedPrefs.getUser()!!.profilePicture!!, binding.profilePicture)
        } else if (sharedPrefs.getUser()?.profilePicture.isNullOrEmpty()) {
            binding.profilePicture.setImageResource(com.tradeasy.R.drawable.default_profile_picture)
        }
        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_editProfileFragment)
            val item =
                requireActivity().findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).menu.findItem(
                    com.tradeasy.R.id.profileFragment
                )
            NavigationUI.onNavDestinationSelected(item, findNavController())
        }

    }

    private fun fragmentSetupOffline() {
        binding.username.visibility = View.GONE
        binding.editProfileBtn.text = "Login"
        binding.accountCardView.visibility = View.GONE
        binding.accountTxtView.visibility = View.GONE
        binding.profileSpacer.layoutParams.height = resources.displayMetrics.heightPixels / 8
        constraintsOnClickOffline()
        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)

        }


    }

    private fun logout() {
        binding.logoutConstraint.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                sharedPrefs.clearUser()
                findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
    // give me a fucntion to get time ago in days and weeks and months and years
}