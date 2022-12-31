package com.tradeasy.ui.selling.smsToVerify

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentSmsToVerifyBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SmsToVerifyFragment : Fragment() {
private lateinit var binding: FragmentSmsToVerifyBinding
    private val viewModel: SmsToVerifyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
// hide the toolbar
        (activity as MainActivity?)?.setupToolBar("", false, false)
        binding = FragmentSmsToVerifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sendSms()
        observe()
    }
    private fun sendSms() {
binding.sendSmsBtn.setOnClickListener {
           viewModel.sendSms()
        }
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: SmsToVerifyState) {
        when (state) {
            is SmsToVerifyState.Init -> Unit
            is SmsToVerifyState.ErrorSent -> handleErrorSent(state.rawResponse)
            is SmsToVerifyState.SuccessSent -> handleSuccessSent()
            is SmsToVerifyState.ShowToast -> {}
            is SmsToVerifyState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorSent(response: WrappedResponse<String>) {
        (activity as MainActivity?)?.setupToolBar("", false, false)
        binding.sendSmsBtn.hideProgress(
            "Send code"
        )
        // snackbar
        Snackbar.make(binding.root, response.message, Snackbar.LENGTH_SHORT).show()

    }

    private fun handleSuccessSent() {
        findNavController().navigate(R.id.VerifyAccountFragment)

        Snackbar.make(requireView(), "Code Sent", Snackbar.LENGTH_LONG).show()
        //findNavController().navigate(R.id.action_updatePasswordFragment_to_editProfileFragment)
    }

    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/

        binding.sendSmsBtn.showProgress {
            progressColor =  Color.WHITE

        }
    }
}