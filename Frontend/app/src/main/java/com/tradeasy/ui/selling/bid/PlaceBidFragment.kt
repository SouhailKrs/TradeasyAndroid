package com.tradeasy.ui.selling.bid

import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tradeasy.R
import com.tradeasy.databinding.FragmentPlaceBidBinding
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PlaceBidFragment : Fragment(R.layout.fragment_place_bid) {
    private lateinit var binding: FragmentPlaceBidBinding
    private val args: com.tradeasy.ui.selling.bid.PlaceBidFragmentArgs by navArgs()
    private val viewModel: PlaceBidViewModel by viewModels()

    @Inject
    // shared preference
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaceBidBinding.inflate(inflater, container, false)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeBid()
        observe()

    }

    private fun setResultOkToPreviousFragment() {
        val r = Bundle().apply {
            putBoolean("success_create", true)
        }
        setFragmentResult("success_create", r)
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: PlaceBidFragmentState) {
        when (state) {
            is PlaceBidFragmentState.IsLoading -> handleLoading(state.isLoading)
            is PlaceBidFragmentState.SuccessCreate -> {
                setResultOkToPreviousFragment()
                findNavController().navigateUp()
            }
            is PlaceBidFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is PlaceBidFragmentState.Init -> Unit
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun placeBid() {


        val userId = sharedPrefs.getUser()?.username
        val timer = args.bidTime
        val countdown = getTimeLeft(timer.toLong())
        binding.bidTimer.isCountDown = true
        binding.bidTimer.base = SystemClock.elapsedRealtime() + countdown
        binding.lastBid.text = ("Last bid " + args.productPrice.toString())
        binding.bidTimer.start()

        if (!args.forBid) {
            Toast.makeText(requireContext(), "Bid is closed", Toast.LENGTH_SHORT).show()


        }

        binding.placeBidBtn.setOnClickListener {

            val bidInput = binding.bidInput.text.toString().trim()
            if (bidInput.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter bid amount", Toast.LENGTH_SHORT)
                    .show()
            } else if (bidInput < args.productPrice.toString()) {
                Toast.makeText(
                    requireContext(),
                    "Bid should be greater than current price",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.placeBid(Bid(userId, args.productId, bidInput.toInt()))
            }
        }
    }


    private fun handleLoading(isLoading: Boolean) {
        //   binding.saveButton.isEnabled = !isLoading
    }

    private fun getTimeLeft(time: Long): Long {
        return time - System.currentTimeMillis()
    }


}

