package com.tradeasy.ui.editProfile.updatePassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentUpdatePasswordBinding
import com.tradeasy.domain.model.UpdatePasswordRequest
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePasswordBinding
    private val viewModel: UpdatePasswordViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)

        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        val toolbarTxt: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        toolbarTxt.visibility = View.GONE
        toolbar.text = "Update Password"

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE

        updatePassword()
        observe()
    }

    //LOGIN
    private fun updatePassword() {
        binding.updatePasswordBtn.setOnClickListener {
            val currentPassword=binding.currentPassword.text.toString().trim()
            val newPassword=binding.newPassword.text.toString().trim()
            val confirmPassword=binding.confirmPassword.text.toString().trim()
           if(currentPassword.isEmpty()){
               binding.currentPassword.error="Enter Current Password"
               binding.currentPassword.requestFocus()
               return@setOnClickListener
           }
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
            else{
                val req = UpdatePasswordRequest(currentPassword,newPassword)
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
    private fun handleStateChange(state: UpdatePasswordActivityState) {
        when (state) {
            is UpdatePasswordActivityState.Init -> Unit
            is UpdatePasswordActivityState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is UpdatePasswordActivityState.SuccessUpdate -> handleSuccessUpdate(state.user)
            is UpdatePasswordActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is UpdatePasswordActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorUpdate(response: WrappedResponse<User>) {
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
    private fun handleSuccessUpdate(user: User) {

        //sncakbar
        Snackbar.make(requireView(), "Password Updated Successfully", Snackbar.LENGTH_LONG).show()
        findNavController().navigate(R.id.action_updatePasswordFragment_to_editProfileFragment)
    }
}