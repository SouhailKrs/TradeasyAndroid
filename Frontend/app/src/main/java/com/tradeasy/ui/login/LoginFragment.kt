package com.tradeasy.ui.login

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
import com.tradeasy.databinding.FragmentLoginBinding
import com.tradeasy.domain.model.User
import com.tradeasy.ui.navigation.Navigation
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

//        view?.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility = View.GONE
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        view.rootView.findViewById<BottomNavigationView>(com.tradeasy.R.id.bottomNavigationView).visibility =
            View.GONE
        binding.navToRegister.setOnClickListener {
            Navigation().loginToRegister(requireView())

        }
// Close the  login fragment
        binding.closeLoginFragment.setOnClickListener {
           // findNavController().popBackStack()
          Navigation().loginToHome(requireView())

        }

        login()
        observe()
    }

    //LOGIN
    private fun login() {
        binding.registerButton.setOnClickListener {
            val username = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()
            if (username.isNotEmpty() || password.isNotEmpty()) {
                val user = User(username, null, "", password, "None", false)
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
            is LoginActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is LoginActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorLogin(response: WrappedResponse<User>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    // IF LOGGING IN IS LOADING
    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessLogin(loginEntity: User) {
        sharedPrefs.setUser(loginEntity)
      //  shoppingCartVisibility()

        Navigation().loginToProfile(requireView())


    }



}