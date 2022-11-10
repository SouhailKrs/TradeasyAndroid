package com.tradeasy.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: UserDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.rootView.findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility =
            View.VISIBLE
        viewModel.getData()
        observe()
    }

    private fun observe() {
        println("observe")
        viewModel.mState
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }
            .launchIn(lifecycleScope)
    }

    private fun handleStateChange(state: ActivityState) {
        when (state) {
            is ActivityState.IsLoading -> {
               println("loading")
                Toast.makeText(requireActivity(), "loading", Toast.LENGTH_SHORT).show()
            }
            is ActivityState.SuccessLogin -> {

                val username= binding.username
                val email = binding.email
                username.text = state.user.username
                email.text = state.user.email
                if(state.user.profilePicture=="None"){

                    binding.profilePicture.setImageResource(R.drawable.default_profile_picture)
                }

            }
            is ActivityState.ErrorLogin -> {

                Toast.makeText(requireActivity(), state.rawResponse, Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(requireActivity(), "else", Toast.LENGTH_SHORT).show()
            }
        }
    }

}