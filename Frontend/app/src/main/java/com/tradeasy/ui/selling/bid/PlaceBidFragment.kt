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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentPlaceBidBinding
import com.tradeasy.domain.product.entity.Bid
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class PlaceBidFragment : Fragment(R.layout.fragment_place_bid) {
    private lateinit var binding: FragmentPlaceBidBinding
    private val viewModel: PlaceBidViewModel by viewModels()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
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
        (activity as MainActivity?)?.setupToolBar("Place bid ", false)
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
                findNavController().popBackStack(R.id.placeBidFragment, true)
                findNavController().popBackStack(R.id.productItemFragment, true)
                findNavController().popBackStack(R.id.homeFragment, false)
                findNavController().navigate(R.id.productItemFragment)

              //  findNavController().navigateUp()
                sharedViewModel.prodName.observe(viewLifecycleOwner) { prodName ->
                    Snackbar.make(requireView(), "You have bid for $prodName ", Snackbar.LENGTH_LONG).show()
                }

            }
            is PlaceBidFragmentState.ShowToast -> {}
            is PlaceBidFragmentState.Init -> Unit
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun placeBid() {

        var timer = 0
        val userId = sharedPrefs.getUser()?.username

        sharedViewModel.bidEndDate.observe(viewLifecycleOwner) { bidEndDate ->
           timer = bidEndDate.toInt()
        }

        val countdown = getTimeLeft(timer.toLong())
        binding.bidTimer.isCountDown = true
        binding.bidTimer.base = SystemClock.elapsedRealtime() + countdown

        sharedViewModel.prodPrice.observe(viewLifecycleOwner) { prodPrice ->
            binding.lastBid.text = ("Last bid " + prodPrice.toString())
        }


        binding.bidTimer.start()
sharedViewModel.forBid.observe(viewLifecycleOwner) { forBid ->
    if (!forBid) {



    }
        }


        binding.placeBidBtn.setOnClickListener {

            val bidInput = binding.bidInput.text.toString().trim()

            sharedViewModel.prodPrice.observe(viewLifecycleOwner) { prodPrice ->
                if (bidInput.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter bid amount", Toast.LENGTH_SHORT)
                        .show()
                } else if (bidInput.toFloat() < prodPrice.toFloat()) {
                    Toast.makeText(
                        requireContext(),
                        "Bid should be greater than current price",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                //    viewModel.placeBid(Bid(userId, sharedViewModel.prodId.value, bidInput.toFloat()))
                    sharedViewModel.prodId.observe(viewLifecycleOwner) { prodId ->
                        viewModel.placeBid(Bid(userId, prodId, bidInput.toFloat()))

                    }


                }
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

