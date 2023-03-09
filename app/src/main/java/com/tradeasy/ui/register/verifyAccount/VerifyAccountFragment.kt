package com.tradeasy.ui.register.verifyAccount

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
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
import com.tradeasy.data.user.remote.dto.VerifyAccountReq
import com.tradeasy.databinding.FragmentVerifyAccountBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class VerifyAccountFragment : Fragment() {

private lateinit var binding: FragmentVerifyAccountBinding
    private val viewModel: VerfiyAccountViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)?.setupToolBar("Verify account", false, false)

        // get the last fragment in the backstack nav graph

        setupView()
        validateOtp()
        verifyAccount()
        observe()
    }
private fun setupView(){
    binding.verifyLater.setOnClickListener {
        findNavController().navigate(R.id.homeFragment)


    }
    val lastFragment = findNavController().previousBackStackEntry?.destination?.label
    if (lastFragment == "fragment_register") {
        binding.verifyLater.visibility = View.VISIBLE
    } else {
        binding.verifyLater.visibility = View.GONE
    }

}
    private fun verifyAccount() {

        binding.verifyAccountBtn.setOnClickListener {


            val otp = binding.pinView.text.toString()

            if (otp.isEmpty()) {
                binding.pinView.error = "OTP is required"
                binding.pinView.requestFocus()
                return@setOnClickListener
            }
            viewModel.verifyAccount(VerifyAccountReq(otp))
        }
    }
    private fun validateOtp()  {
        val otp = binding.pinView
       otp.addTextChangedListener {
           binding.verifyAccountBtn.isEnabled = otp.text.toString().length == 6
       }

    }
    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: VerifyAccountState) {
        when (state) {
            is VerifyAccountState.Init -> Unit
            is VerifyAccountState.ErrorVerify -> handleErrorVerify(state.rawResponse)
            is VerifyAccountState.SuccessVerify -> handleSuccessVerify(state.user)
            is VerifyAccountState.ShowToast ->{}
            is VerifyAccountState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorVerify(response: WrappedResponse<User>) {
        binding.verifyAccountBtn.hideProgress("Verify")

        // snackbar
    Snackbar.make(
        binding.root,
        response.message.toString(),
        Snackbar.LENGTH_LONG
    ).show()
    }

    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
        binding.verifyAccountBtn.showProgress { progressColor = Color.WHITE }


    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessVerify(user: User) {

        sharedPrefs.setUser(user)
        Snackbar.make(
            binding.root,
           "Yahoo ! Account verified ",
            Snackbar.LENGTH_LONG
        ).show()

        findNavController().navigate(R.id.homeFragment)
    }

}