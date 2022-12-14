package com.tradeasy.ui.login.forgotPassword.verifyOtp

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.chaos.view.PinView
import com.tradeasy.databinding.FragmentVerifyOtpBinding
import com.tradeasy.data.user.remote.dto.VerifyOtpReq
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class VerifyOtpFragment : Fragment() {
    private val viewModel: VerfiyOtpViewModel by viewModels()

    private lateinit var binding:FragmentVerifyOtpBinding
    private lateinit var pinView: PinView

    private val args: VerifyOtpFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyOtpBinding.inflate(inflater, container, false)

        println("email is ${args.email}")
        pinView = binding.pinView
        pinView.requestFocus()
        val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)

        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 6) {
                    Toast.makeText(context, "PinView: $s", Toast.LENGTH_SHORT).show()
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verifyOtp()
        observe()
    }
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }
    private  fun verifyOtp() {



        binding.verifyOtp.setOnClickListener {

            println("email is ${args.email}")
            println("otp is ${ binding.pinView.text.toString()}")
            val otp = binding.pinView.text.toString()
            val req= VerifyOtpReq(otp,"seifeddine.abdelkader@esprit.tn")
            if (otp.isEmpty()) {
                binding.pinView.error = "OTP is required"
                binding.pinView.requestFocus()
                return@setOnClickListener
            }
            viewModel.verifyOtp(req)
        }
    }


    private fun handleStateChange(state: VerifyOtpFragmentState) {
        when (state) {
            is VerifyOtpFragmentState.Init -> Unit
            is VerifyOtpFragmentState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is VerifyOtpFragmentState.SuccessUpdate -> handleVerified()
            is VerifyOtpFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is VerifyOtpFragmentState.IsLoading -> handleLoading(state.isLoading)
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

    private fun handleVerified() {
        println("email is ${args.email}")
        println("otp is ${ binding.pinView.text.toString()}")
    findNavController().navigate(VerifyOtpFragmentDirections.actionVerifyOtpFragmentToResetPassFragment(

        args.email,

        binding.pinView.text.toString()


    ))
    }

}