package com.tradeasy

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.databinding.FragmentForgotPasswordBinding
import com.tradeasy.domain.model.ForgotPasswordReq
import com.tradeasy.domain.model.User
import com.tradeasy.ui.login.ForgotPasswordActivityState
import com.tradeasy.ui.login.ForgotPasswordViewModel
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import hilt_aggregated_deps._com_tradeasy_ForgotPasswordFragment_GeneratedInjector
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
private lateinit var binding: FragmentForgotPasswordBinding
private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
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

            findNavController().navigate(R.id.action_forgotPasswordFragment_to_newPasswordFragment)
            if (email.isEmpty()) {
                binding.currentEmail.error = "Email is required"
                binding.currentEmail.requestFocus()

                return@setOnClickListener
            }

            viewModel.sendResetCode(req)
        }

    }


    //  STATE HANDLER
    private fun handleStateChange(state: ForgotPasswordActivityState) {
        when (state) {
            is ForgotPasswordActivityState.Init -> Unit
            is ForgotPasswordActivityState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is ForgotPasswordActivityState.SuccessUpdate -> handleSent(state.user)
            is ForgotPasswordActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is ForgotPasswordActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }
    private fun handleErrorUpdate(response: WrappedResponse<User>) {
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
        Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

    private fun handleSent(user: User) {

        //sncakbar
        Snackbar.make(requireView(), "Reset Code Sent", Snackbar.LENGTH_SHORT).show()
    }

    }


