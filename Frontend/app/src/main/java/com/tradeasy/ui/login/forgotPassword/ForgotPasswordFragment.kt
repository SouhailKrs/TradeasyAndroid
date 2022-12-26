package com.tradeasy.ui.login.forgotPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.databinding.FragmentForgotPasswordBinding
import com.tradeasy.data.user.remote.dto.ForgotPasswordReq
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendResetCode()
        observe()

    }
    private fun sendResetCode() {
        binding.sendResetCode.setOnClickListener {

            val email = binding.currentEmail.text.toString()
            val req = ForgotPasswordReq(email)

            if (email.isEmpty()) {
                binding.currentEmail.error = "Email is required"
                binding.currentEmail.requestFocus()
                return@setOnClickListener
            }

            viewModel.sendResetCode(req)
        }

    }
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    //  STATE HANDLER
    private fun handleStateChange(state: ForgotPasswordActivityState) {
        when (state) {
            is ForgotPasswordActivityState.Init -> Unit
            is ForgotPasswordActivityState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is ForgotPasswordActivityState.SuccessUpdate -> handleSent()
            is ForgotPasswordActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is ForgotPasswordActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }
    private fun handleErrorUpdate(response: WrappedResponse<String>) {
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
        Toast.makeText(requireActivity(), "Loeeading", Toast.LENGTH_SHORT).show()
    }

    private fun handleSent() {

       val action = ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToVerifyOtpFragment(


           binding.currentEmail.text.toString()

       )
        findNavController().navigate(action)

        Snackbar.make(requireView(), "Reset Code Sent", Snackbar.LENGTH_SHORT).show()
    }

}