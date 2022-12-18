package com.tradeasy.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.databinding.FragmentRegisterBinding
import com.tradeasy.domain.user.entity.Notification
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.login.LoginFragment
import com.tradeasy.ui.navigation.registerToHome
import com.tradeasy.ui.navigation.registerToLogin
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()
    private val verifyUsernameViewModel: VerifyUsernameViewModel by viewModels()
    val loginFragment = LoginFragment()

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

// make the drawable spin
println("aaaaa" + binding.countrycodePicker.selectedCountryCodeWithPlus)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.GONE
        binding.navToLogin.setOnClickListener {
            registerToLogin(requireView())

        }
        binding.closeRegisterFragment.setOnClickListener {

            registerToHome(requireView())
        }

        registerBtnHandler()
        verifyUsername()
        usernameObserve()
        register()
        observe()
    }

    // REGISTER
    private fun register() {
        binding.registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val phoneNumber = binding.phoneNumberField.text.toString().trim()
            val email = binding.emailField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            val notificationList = mutableListOf<Notification>()
            if (username.isNotEmpty() || phoneNumber.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                val user = User(
                    username,
                    phoneNumber.toInt(),
                    email,
                    password,
                    "None",
                    true,
                    sharedPrefs.getNotificationToken(),
                    null,
                    null,
                    0,
                    binding.countrycodePicker.selectedCountryCodeWithPlus,
                    ""
                )

                // pass a value to notification list


                viewModel.userRegister(user)

            }


        }
    }

    // STATE OBSERVER
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    // STATE HANDLER
    private fun handleStateChange(state: UserRegisterActivityState) {
        when (state) {
            is UserRegisterActivityState.Init -> Unit
            is UserRegisterActivityState.RegisterError -> handleRegisterError(state.rawResponse)

            is UserRegisterActivityState.RegisterSuccess -> handleRegisterSuccess(state.user)
            is UserRegisterActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is UserRegisterActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // ERROR HANDLER
    private fun handleRegisterError(response: WrappedResponse<User>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    // LOADING HANDLER
    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
    }

    // SUCCESS HANDLER
    private fun handleRegisterSuccess(userRegisterEntity: User) {
        //save to shared prefs
        sharedPrefs.setUser(userRegisterEntity)
        userRegisterEntity.token?.let { sharedPrefs.setToken(it) }


    findNavController().navigate(R.id.homeFragment)
    }

    private fun registerBtnHandler() {

        val username = binding.usernameField
        val phoneNumber = binding.phoneNumberField
        val email = binding.emailField
        val password = binding.passwordField
        val registerBtn = binding.registerButton
        registerBtn.isEnabled = false
        registerBtn.alpha = 0.5f
        username.addTextChangedListener {

            registerBtn.isEnabled =
                username.text!!.isNotBlank() && phoneNumber.text!!.isNotBlank() && email.text!!.isNotBlank() && password.text!!.isNotEmpty()
            registerBtn.alpha = if (registerBtn.isEnabled) 1f else 0.5f


        }
        phoneNumber.addTextChangedListener {

            registerBtn.isEnabled =
                username.text!!.isNotBlank() && phoneNumber.text!!.isNotBlank() && email.text!!.isNotBlank() && password.text!!.isNotEmpty()
            registerBtn.alpha = if (registerBtn.isEnabled) 1f else 0.5f

        }
        email.addTextChangedListener {

            registerBtn.isEnabled =
                username.text!!.isNotBlank() && phoneNumber.text!!.isNotBlank() && email.text!!.isNotBlank() && password.text!!.isNotEmpty()
            registerBtn.alpha = if (registerBtn.isEnabled) 1f else 0.5f

        }
        password.addTextChangedListener {

            registerBtn.isEnabled =
                username.text!!.isNotBlank() && phoneNumber.text!!.isNotBlank() && email.text!!.isNotBlank() && password.text!!.isNotEmpty()
            registerBtn.alpha = if (registerBtn.isEnabled) 1f else 0.5f

        }


    }
    private fun usernameObserve() {
        verifyUsernameViewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleVerifyStateChange(state) }.launchIn(lifecycleScope)
    }
    private fun handleVerifyStateChange(state: VerifyUsernameFragmentState) {
        when (state) {
            is VerifyUsernameFragmentState.Init -> Unit
            is VerifyUsernameFragmentState.UsernameAvailable -> handleUsernameAvailable(state.rawResponse)
            is VerifyUsernameFragmentState.UsernameExists -> handleUsernameExists(state.message)
            is VerifyUsernameFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is VerifyUsernameFragmentState.IsLoading -> handleVerificationLoading(state.isLoading)
        }
    }
    private fun handleUsernameAvailable(response: WrappedResponse<String>) {
    //.usernameLayout.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context,R.drawable.drawableRight), null)

        binding.usernameField.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_check_24), null)
       binding.usernameField.addTextChangedListener {
           if(binding.usernameField.text!!.isBlank()){
               binding.usernameField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
           }

       }
    }
    private fun handleVerificationLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
       // Toast.makeText(requireActivity(), "Loeeading", Toast.LENGTH_SHORT).show()
    }
    private fun handleUsernameExists(response: String) {
        binding.usernameField.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(requireContext(),R.drawable.ic_baseline_close_24_red), null)
        binding.registerButton.isEnabled = false
        binding.registerButton.alpha = 0.5f
        if(binding.usernameField.text.isNullOrBlank()){
            binding.usernameField.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        }
    }
    private fun verifyUsername() {
       binding.usernameField.addTextChangedListener {
           val username = binding.usernameField.text.toString().trim()
           if (username.isNotBlank()) {
               val req = UpdateUsernameReq(username)
               verifyUsernameViewModel.verifyUsername(req)
           }
       }
    }
    // add progress bar to username field



}