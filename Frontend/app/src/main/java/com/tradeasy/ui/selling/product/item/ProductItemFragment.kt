package com.tradeasy.ui.selling.product.item

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.denzcoskun.imageslider.models.SlideModel
import com.tradeasy.databinding.FragmentProductItemBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.home.buyNow.BuyNowActivityState
import com.tradeasy.ui.home.buyNow.BuyNowViewModel
import com.tradeasy.utils.ImageLoader
import com.tradeasy.utils.WrappedResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class ProductItemFragment : Fragment() {

    private lateinit var binding: FragmentProductItemBinding
    private val viewModel: AddToSavedViewModel by viewModels()
    private val buyNowViewModel: BuyNowViewModel by viewModels()
    private val args: com.tradeasy.ui.selling.product.item.ProductItemFragmentArgs by navArgs()
    var PERMISSION_CODE = 100
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductItemBinding.inflate(inflater, container, false)

        println("Zezdzdzdz ${args.image[1]}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addProductToSaved()
        observe()
        // get screen size


        setupView()

        buyingObserve()
    }

    private fun setupView() {
        val displayMetrics = resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels
        // get the difference between height and width
        val difference = height / width

        // get the percentage of the difference between height and width
        binding.sellerProfilePicture.layoutParams.height = height/17
        binding.sellerProfilePicture.layoutParams.width = ((height/17)* difference)

        binding.apply {
            val imageList = ArrayList<SlideModel>()
            for (i in args.image.indices) {
                imageList.add(SlideModel(args.image[i].toString()))
            }
            println("here ${imageList.toString()}")
            imageSlider.setImageList(imageList)
            productName.text = args.productName
            productPrice.text = args.productPrice.toString() + " TND"
            sellerUsername.text = args.username
            ImageLoader(args.userProfilePicture, sellerProfilePicture)
            sellerPhoneNumber.text=args.userPhoneNumber
        }
            makePhoneCall()

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

    private fun buyingObserve() {
        buyNowViewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleBuyingStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun addProductToSaved() {
//        binding.addToSavedBtn.setOnClickListener {
//            println(args.productId)
//            val req = AddToSavedReq(args.productId)
//            viewModel.addProductToSaved(req)
//        }


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

    private fun handleErrorBuying(response: WrappedResponse<Product>) {
        AlertDialog.Builder(requireActivity()).apply {
            setMessage(response.message)
            setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

    private fun handleBuyingStateChange(state: BuyNowActivityState) {
        when (state) {
            is BuyNowActivityState.Init -> Unit
            is BuyNowActivityState.ErrorBuying -> handleErrorBuying(state.rawResponse)
            is BuyNowActivityState.SuccessBuying -> handleSuccessBuying()
            is BuyNowActivityState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is BuyNowActivityState.IsLoading -> handleLoading(state.isLoading)
        }
    }
private  fun makePhoneCall(){
    if (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CALL_PHONE),
            PERMISSION_CODE
        )
    }

    binding.sellerPhoneNumber.setOnClickListener(View.OnClickListener {
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${args.userPhoneNumber}")
        startActivity(i)
    })
}
    private fun handleSuccessBuying() {


    }

// function to trigger a phone call


}