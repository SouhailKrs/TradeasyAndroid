package com.tradeasy.ui.login.forgotPassword.resetPass

import android.graphics.Color
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
import androidx.navigation.fragment.navArgs
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.data.user.remote.dto.ResetPasswordReq
import com.tradeasy.databinding.FragmentResetPassBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ResetPassFragment : Fragment() {
    private lateinit var binding: FragmentResetPassBinding
    private val viewModel: ResetPasswordViewModel by viewModels()
// safe args
    val args: ResetPassFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetPassBinding.inflate(inflater, container, false)

        (activity as MainActivity?)?.setupToolBar("Reset Password", false, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.GONE

        updatePassword()
        observe()
    }

    //LOGIN
    private fun updatePassword() {
        binding.updatePasswordBtn.setOnClickListener {
            val newPassword=binding.newPassword.text.toString().trim()
            val confirmPassword=binding.confirmPassword.text.toString().trim()
            if(newPassword.isEmpty()){
                binding.newPassword.error="Enter New Password"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }
            if(confirmPassword.isEmpty()){
                binding.confirmPassword.error="Enter Confirm Password"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }
            if(newPassword!=confirmPassword){
                binding.confirmPassword.error="Password Not Match"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }
            if(newPassword.length<8){
                binding.newPassword.error="Password must be at least 8 character"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }
            else{
                val req = ResetPasswordReq(args.email,args.otp,newPassword)
                viewModel.updatePassword(req)
            }

        }
    }

    // STATE OBSERVER
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    //  STATE HANDLER
    private fun handleStateChange(state: ResetPasswordActivityState) {
        when (state) {
            is ResetPasswordActivityState.Init -> Unit
            is ResetPasswordActivityState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is ResetPasswordActivityState.SuccessUpdate -> handleSuccessUpdate(state.message)
            is ResetPasswordActivityState.ShowToast ->{}
            is ResetPasswordActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorUpdate(response: WrappedResponse<String>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                binding.updatePasswordBtn.hideProgress("reset password")
                dialog.dismiss()
            }
        }.show()
    }

    // IF LOGGING IN IS LOADING
    private fun handleLoading(isLoading: Boolean) {
    binding.updatePasswordBtn.showProgress {
        progressColor = Color.WHITE

    }

    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessUpdate(message:String) {

        //sncakbar
        Snackbar.make(requireView(), "Password Reset Successfully", Snackbar.LENGTH_LONG).show()
        findNavController().navigate(R.id.loginFragment)
    }
}