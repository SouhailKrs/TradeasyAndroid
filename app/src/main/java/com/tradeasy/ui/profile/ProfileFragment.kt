package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.tradeasy.R
import com.tradeasy.databinding.FragmentProfileBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.navigation.profileToLogin
import com.tradeasy.ui.profile.deleteAccount.DeleteAccountState
import com.tradeasy.ui.profile.deleteAccount.DeleteAccountViewModel
import com.tradeasy.ui.profile.logout.LogoutState
import com.tradeasy.ui.profile.logout.LogoutViewModel
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import com.tradeasy.utils.getScreenSize
import com.tradeasy.utils.imageLoader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: DeleteAccountViewModel by viewModels()
    private val logoutVM: LogoutViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setupView()
// change app language

        return binding.root
    }

private fun setupView() {

binding.profilePicture.layoutParams.height = (getScreenSize(requireContext()).first *0.2).toInt()
    binding.profilePicture.layoutParams.width = (getScreenSize(requireContext()).first *0.2).toInt()

    val user = sharedPrefs.getUser()
    (activity as MainActivity?)?.setupToolBar("Profile", false, false)
    if (user == null) {
        fragmentSetupOffline()
    } else {

        disableFirebaseService()
        sharedPrefs.getUser()?.let {
            binding.username.text = it.username

        }
        fragmentSetupOnline()
    }
}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
      //  setupToolbar()
   logout()
        observeLogout()
        deleteAccount()
        observe()
//HomeFragment().setupToolBar("test",true)

        // WHAT TO DO WHEN USER IS NOT LOGGED IN
    }


    private fun deleteAccount() {
        binding.deleteConstraint.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteAccount()

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
            is DeleteAccountState.ErrorDelete -> handleErrorDelete(state.rawResponse)
            is DeleteAccountState.SuccessDelete -> handleSuccessDelete()
            is DeleteAccountState.ShowToast ->{}
            is DeleteAccountState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorDelete(response: WrappedResponse<String>) {

        // snackbar
        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleSuccessDelete() {
        findNavController().navigate(R.id.homeFragment)
        sharedPrefs.clearUser()
        Snackbar.make(requireView(), "Account deleted", Snackbar.LENGTH_LONG).show()
        //findNavController().navigate(R.id.action_updatePasswordFragment_to_editProfileFragment)
    }

    private fun handleLoading(isLoading: Boolean) {


    }

    // where to go when the user in offline
    private fun constraintsOnClickOffline() {
        binding.savedConstraint.setOnClickListener {
            profileToLogin(requireView())
        }

        binding.pushNotificationsConstraint.setOnClickListener {
            findNavController().navigate(R.id.pushNotificationsFragment)
        }

binding.privacyPolicyConstraint.setOnClickListener {
            findNavController().navigate(R.id.termsAndConditionsFragment)
        }

    }

    private fun constraintsOnClickOnline() {
        binding.savedConstraint.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_savedProductsFragment)
        }
        binding.privacyPolicyConstraint.setOnClickListener {
            findNavController().navigate(R.id.termsAndConditionsFragment)
        }
binding.pushNotificationsConstraint.setOnClickListener {
    findNavController().navigate(R.id.pushNotificationsFragment)
}



    }

    private fun fragmentSetupOnline() {
        constraintsOnClickOnline()
        binding.editProfileBtn.text = "Edit Profile"
        binding.accountCardView.visibility = View.VISIBLE
        binding.accountTxtView.visibility = View.VISIBLE
      //  binding.profileSpacer.layoutParams.height = resources.displayMetrics.heightPixels / 7

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
       // binding.profileSpacer.layoutParams.height = resources.displayMetrics.heightPixels / 8
        constraintsOnClickOffline()
        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(com.tradeasy.R.id.action_profileFragment_to_loginFragment)

        }


    }

    private fun observeLogout() {
        logoutVM.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleLogoutStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleLogoutStateChange(state: LogoutState) {
        when (state) {
            is LogoutState.Init -> Unit
            is LogoutState.ErrorLogout -> handleErrorLogout(state.rawResponse)
            is LogoutState.SuccessLogout -> handleSuccessLogout()
            is LogoutState.ShowToast -> {}
            is LogoutState.IsLoading -> handleLogoutLoading(state.isLoading)
        }
    }

    private fun handleErrorLogout(response: WrappedResponse<String>) {

        // snackbar
        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleSuccessLogout() {
        sharedPrefs.clearUser()
        findNavController().navigate(R.id.loginFragment)
        Snackbar.make(requireView(), "Logout Successful", Snackbar.LENGTH_LONG).show()
        //findNavController().navigate(R.id.action_updatePasswordFragment_to_editProfileFragment)
    }

    private fun handleLogoutLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/

    }

    // where to go when the user in offline




    private fun logout() {
        binding.logoutConstraint.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Logout")
            builder.setMessage("Are you sure you want to logout?")
            builder.setPositiveButton("Yes") { _, _ ->
                logoutVM.logout()

            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }
    }
    // function to change app language
    fun disableFirebaseService() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("general")
    }
}