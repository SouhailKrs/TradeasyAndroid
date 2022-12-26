package com.tradeasy.ui.login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentLoginBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.navigation.loginToRegister
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        (activity as MainActivity?)?.setupToolBar("", false, false)
//        view?.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.GONE
        binding.navToRegister.setOnClickListener {
            loginToRegister(requireView())


        }
        closeLoginHandler()
// CLOSE THE LOGIN FRAGMENT AND GO TO HOME FRAGMENT

        binding.navToForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.forgotPasswordFragment)

        }
        loginButtonHandler()
        login()
        observe()
    }

    //LOGIN
    private fun login() {

        binding.loginButton.setOnClickListener {


            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            if (username.isNotEmpty() || password.isNotEmpty()) {
                val user = User(
                    username,
                    1,
                    "",
                    password,
                    null,
                    true,
                    sharedPrefs.getNotificationToken(),
                    null,
                    null,
                    0,
                    "",
                    ""
                )
                viewModel.login(user)
            }


        }

    }

    // STATE OBSERVER
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    //  STATE HANDLER
    private fun handleStateChange(state: LoginActivityState) {
        when (state) {
            is LoginActivityState.Init -> Unit
            is LoginActivityState.ErrorLogin -> handleErrorLogin(state.rawResponse)
            is LoginActivityState.SuccessLogin -> handleSuccessLogin(state.user)
            is LoginActivityState.ShowToast -> { Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
                binding.loginButton.hideProgress("Login")
            }
            is LoginActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorLogin(response: WrappedResponse<User>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
                binding.loginButton.hideProgress("Login")
            }
        }.show()
    }

    // IF LOGGING IN IS LOADING
    private fun handleLoading(isLoading: Boolean) {
        binding.loginButton.isEnabled = !isLoading
        binding.loginButton.showProgress {

// set progress indicator color to black
            progressColor = Color.WHITE


        }

        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessLogin(loginEntity: User) {
        sharedPrefs.setUser(loginEntity)
        sharedPrefs.setToken(loginEntity.token!!)
        //  shoppingCartVisibility()

        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)



    }


    private fun loginButtonHandler() {

        val username = binding.usernameField
        val password = binding.passwordField
        val loginBtn = binding.loginButton
        loginBtn.isEnabled = false
        loginBtn.alpha = 0.5f
        username.addTextChangedListener {
            loginBtn.isEnabled = username.text!!.isNotBlank() && password.text!!.isNotEmpty()
            loginBtn.alpha = if (loginBtn.isEnabled) 1f else 0.5f

        }
        password.addTextChangedListener {
            loginBtn.isEnabled = username.text!!.isNotBlank() && password.text!!.isNotEmpty()
            loginBtn.alpha = if (loginBtn.isEnabled) 1f else 0.5f
        }
    }

    private fun closeLoginHandler() {
        binding.closeLoginFragment.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
            findNavController().popBackStack(R.id.loginFragment, true)
            val currentFragment = findNavController().currentDestination?.label

            if (currentFragment == "fragment_selling" || currentFragment == "fragment_notifications") {
                findNavController().popBackStack()
                findNavController().navigate(R.id.homeFragment)
            }
            if (currentFragment == "fragment_profile") {
              //  findNavController().popBackStack()
                findNavController().navigate(R.id.profileFragment)
// bottom nav bar visibility
                view?.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
                    View.VISIBLE

            }

        }
    }
}