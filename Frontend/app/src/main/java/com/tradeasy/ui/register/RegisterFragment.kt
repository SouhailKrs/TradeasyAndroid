package com.tradeasy.ui.register

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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentRegisterBinding
import com.tradeasy.domain.model.User
import com.tradeasy.ui.login.LoginFragment
import com.tradeasy.ui.navigation.registerToHome
import com.tradeasy.ui.navigation.registerToLogin
import com.tradeasy.ui.navigation.registerToProfile
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
    val loginFragment = LoginFragment()

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE
        binding.navToLogin.setOnClickListener {
          registerToLogin(requireView())

        }
        binding.closeRegisterFragment.setOnClickListener {

           registerToHome(requireView())



        }


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
            if (username.isNotEmpty() || phoneNumber.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                val user = User(username, phoneNumber.toInt(), email, password, "None", false)
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





        registerToProfile(requireView())
    }
}