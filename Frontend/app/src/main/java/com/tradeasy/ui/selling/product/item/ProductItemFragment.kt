package com.tradeasy.ui.selling.product.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tradeasy.databinding.FragmentProductItemBinding
import com.tradeasy.domain.model.AddToSavedReq
import com.tradeasy.domain.model.User
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProductItemFragment : Fragment() {

    private lateinit var binding: FragmentProductItemBinding
    private val viewModel: AddToSavedViewModel by viewModels()
    private val args: com.tradeasy.ui.selling.product.item.ProductItemFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductItemBinding.inflate(inflater, container, false)
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addProductToSaved()
        observe()
    }

    private fun setupView() {
        binding.apply {
            prodItemName.text = args.productName
            prodItemPrice.text = args.productPrice.toString()

        }
    }


    fun goToBidFragment() {
        val action = ProductItemFragmentDirections.actionProductItemFragmentToPlaceBidFragment(
            args.productId, args.bidEndDate, args.productPrice, args.forBid


        )
        findNavController().navigate(action)
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun addProductToSaved() {
        binding.addToSavedBtn.setOnClickListener {
            println(args.productId)
            val req = AddToSavedReq(args.productId)
            viewModel.addProductToSaved(req)
        }


    }

    //  STATE HANDLER
    private fun handleStateChange(state: AddProductToSavedFragmentSate) {
        when (state) {
            is AddProductToSavedFragmentSate.Init -> Unit
            is AddProductToSavedFragmentSate.ErrorSaving -> handleErrorSaving(state.rawResponse)
            is AddProductToSavedFragmentSate.SuccessSaving -> handleSuccessSaving()
            is AddProductToSavedFragmentSate.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is AddProductToSavedFragmentSate.IsLoading -> handleLoading(state.isLoading)
        }
    }

    // IF THERE IS AN ERROR WHILE LOGGING IN
    private fun handleErrorSaving(response: WrappedResponse<User>) {
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

    private fun handleSuccessSaving() {


    }
}