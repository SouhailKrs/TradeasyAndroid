package com.tradeasy.ui.editProfile.updatePassword

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.data.user.remote.dto.UpdatePasswordRequest
import com.tradeasy.databinding.FragmentUpdatePasswordBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {
    private lateinit var binding: FragmentUpdatePasswordBinding
    private val viewModel: UpdatePasswordViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpdatePasswordBinding.inflate(inflater, container, false)

        (activity as MainActivity?)?.setupToolBar("Update password", false, false,0.5f)
        updatePasswordBtnHandler()
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

            if(newPassword.length<8){
                binding.newPassword.error="Password must be at least 8 characters"
                binding.newPassword.requestFocus()
                return@setOnClickListener
            }
            else{
                val req = UpdatePasswordRequest(currentPassword,newPassword,"")
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
            is UpdatePasswordActivityState.ShowToast -> {}
            is UpdatePasswordActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorUpdate(response: WrappedResponse<User>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
                binding.updatePasswordBtn.hideProgress("Update password")
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
    private fun handleSuccessUpdate(user: User) {
sharedPrefs.setUser(user)
        //sncakbar
        Snackbar.make(requireView(), "Password Updated Successfully", Snackbar.LENGTH_LONG).show()
        findNavController().navigateUp()
    }
    private  fun updatePasswordBtnHandler(){
        val currentPassword = binding.currentPassword
        val newPassword = binding.newPassword
        val confirmPassword = binding.confirmPassword
        val updatePasswordBtn = binding.updatePasswordBtn

        updatePasswordBtn.isEnabled = false
        updatePasswordBtn.alpha = 0.5f
        currentPassword.addTextChangedListener {

            updatePasswordBtn.isEnabled =
                currentPassword.text!!.isNotEmpty() && newPassword.text!!.isNotEmpty() && confirmPassword.text!!.isNotEmpty()
            updatePasswordBtn.alpha = if (updatePasswordBtn.isEnabled) 1f else 0.5f


        }
        newPassword.addTextChangedListener {

            updatePasswordBtn.isEnabled =
                currentPassword.text!!.isNotEmpty() && newPassword.text!!.isNotEmpty() && confirmPassword.text!!.isNotEmpty()
            updatePasswordBtn.alpha = if (updatePasswordBtn.isEnabled) 1f else 0.5f

        }
        confirmPassword.addTextChangedListener {

            updatePasswordBtn.isEnabled =
                currentPassword.text!!.isNotEmpty() && newPassword.text!!.isNotEmpty() && confirmPassword.text!!.isNotEmpty()
            updatePasswordBtn.alpha = if (updatePasswordBtn.isEnabled) 1f else 0.5f

        }




    }
}