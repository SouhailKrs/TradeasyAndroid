package com.tradeasy.ui.editProfile.updateUsername

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.data.user.remote.dto.UpdateUsernameReq
import com.tradeasy.databinding.FragmentUpdateUsernameBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@AndroidEntryPoint
class UpdateUsernameFragment : Fragment() {
    private lateinit var binding: FragmentUpdateUsernameBinding
    private val viewModel: UpdateUsernameViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateUsernameBinding.inflate(inflater, container, false)
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        // (activity as AppCompatActivity?)!!.supportActionBar!!.title = Html.fromHtml("<font color='#000000'>Update username</font>")
        //val toolbar: Toolbar = requireActivity().findViewById(com.tradeasy.R.id.toolbar)
        // toolbar.setTitleTextColor(R.color.black)
        sharedPrefs.getUser()?.let {
            binding.newUsername.setText(it.username)
        }

        // change the custom toolbar title

        (activity as MainActivity?)?.setupToolBar("Update username", true, false,0.5f)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUsername()
        observe()

    }

    private fun updateUsername() {
        val toolbarTxt: TextView =
            requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)


        val newUsername = binding.newUsername

        newUsername.addTextChangedListener {
            if (newUsername.text!!.isEmpty() || newUsername.text.toString().lowercase()
                    .trim() == sharedPrefs.getUser()?.username
            ) {
                toolbarTxt.alpha = 0.5f
                toolbarTxt.isClickable = false

            } else {

                toolbarTxt.alpha = 1f
                toolbarTxt.isClickable = true
                toolbarTxt.setOnClickListener {

                    val req = UpdateUsernameReq(newUsername.text.toString().trim().lowercase())

                    viewModel.updateUsername(req)
                }
            }

        }

    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: UpdateUsernameActivityState) {
        when (state) {
            is UpdateUsernameActivityState.Init -> Unit
            is UpdateUsernameActivityState.ErrorUpdate -> handleErrorUpdate(state.rawResponse)
            is UpdateUsernameActivityState.SuccessUpdate -> handleSuccessUpdate(state.user)
            is UpdateUsernameActivityState.ShowToast -> Toast.makeText(
                requireActivity(), "Network Error", Toast.LENGTH_SHORT
            ).show()
            is UpdateUsernameActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }

    private fun handleErrorUpdate(response: WrappedResponse<User>) {

        // snackbar
        snackBar(response.message)
        (activity as MainActivity?)?.setupToolBar("Update username", true, false,0.5f)
    }

    private fun handleLoading(isLoading: Boolean) {
        /*binding.loginButton.isEnabled = !isLoading
        binding.registerButton.isEnabled = !isLoading
        binding.loadingProgressBar.isIndeterminate = isLoading
        if(!isLoading){
            binding.loadingProgressBar.progress = 0
        }*/
        (activity as MainActivity?)?.setupToolBar("Update username", false, true,1f)

    }

    // IF LOGGED IN SUCCESSFULLY
    private fun handleSuccessUpdate(user: User) {

        sharedPrefs.setUser(user)
        snackBar("Username Updated Successfully")
val toolBar: TextView =
            requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        findNavController().navigateUp()
    }

    private fun snackBar(message: String) {
        val snackbar = Snackbar.make(
            requireActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG
        )

        val snackbarView = snackbar.view
        //val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
      //  params.setMargins(10, 0, 10, 120)
        //snackbarView.layoutParams = params
        val snackbarText =
            snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        snackbarText.setCompoundDrawablesWithIntrinsicBounds(
            com.tradeasy.R.drawable.ic_outline_info_24,
            0,
            0,
            0
        )
// get keyboard height

        // set snackbar in a specific position
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.setMargins(10, 0, 10,   100)
        snackbarView.layoutParams = params

        snackbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)

        snackbarView.elevation = 0f

        snackbarText.setPadding(0, 0, 0, 0)

        snackbarText.textAlignment = View.TEXT_ALIGNMENT_CENTER

        snackbar.show()
        // make snackbar appear on top of keyboard
        snackbar.view.setOnApplyWindowInsetsListener { v, insets ->
            v.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
}