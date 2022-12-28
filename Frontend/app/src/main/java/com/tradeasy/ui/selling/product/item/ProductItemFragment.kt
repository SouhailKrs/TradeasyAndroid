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
import com.tradeasy.R
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.databinding.FragmentProductItemBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class ProductItemFragment : Fragment() {
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private lateinit var binding: FragmentProductItemBinding
    private val viewModel: AddToSavedViewModel by viewModels()
    private val args: ProductItemFragmentArgs by navArgs()
    private var PERMISSION_CODE = 100
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addProductToSaved()
        observe()
        // get screen size
        handleSavedButton()
        goToBidFragment()
        setupView()
    }

    private fun setupView() {

        val height = getScreenSize(requireContext()).first.toDouble()
        val width = getScreenSize(requireContext()).second.toDouble()
        // get the difference between height and width
        val difference = height.toDouble() / width.toDouble()
        // get the percentage of the difference between height and width
        binding.sellerProfilePicture.layoutParams.height = ((height / 17).toInt())
        binding.sellerProfilePicture.layoutParams.width = (((width / 17) * difference).toInt())
        binding.goToPlaceBidBtn.visibility = if (args.forBid) View.VISIBLE else View.GONE

        binding.apply {
            val imageList = ArrayList<SlideModel>()
            for (i in args.image.indices) {
                imageList.add(SlideModel(args.image[i].toString()))
            }

            imageSlider.setImageList(imageList)
            productName.text = args.productName.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }
            productPrice.text = args.productPrice.toString() + " TND"
            sellerUsername.text = args.username

            if (args.userProfilePicture.isNotEmpty()) {
                imageLoader(args.userProfilePicture, binding.sellerProfilePicture)

            } else {
                binding.sellerProfilePicture.setImageResource(R.drawable.default_profile_picture)
            }
            sellerPhoneNumber.text = args.userPhoneNumber
        }
// PLACE BID BTN VISIBILITY
        binding.goToPlaceBidBtn.visibility = if (args.forBid) View.VISIBLE else View.GONE

        makePhoneCall()
    }

    private fun handleSavedButton() {

        if(isLoggedIn(sharedPrefs)) {
            if (sharedPrefs.getUser()!!.savedProducts!!.size != 0) {
                for (i in sharedPrefs.getUser()!!.savedProducts!!.indices) {

                    if (sharedPrefs.getUser()!!.savedProducts!![i].productId == args.productId) {
                        binding.addToSavedBtn.setImageResource(com.tradeasy.R.drawable.ic_baseline_bookmark_24_filled)
                    } else {
                        binding.addToSavedBtn.setImageResource(com.tradeasy.R.drawable.ic_outline_bookmark_border_24)
                    }
                }
            }
        }
        else{
            binding.addToSavedBtn.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun goToBidFragment() {

        binding.goToPlaceBidBtn.setOnClickListener {

            if(isLoggedIn(sharedPrefs)) {
                val action =
                    ProductItemFragmentDirections.actionProductItemFragmentToPlaceBidFragment(
                        args.productId, args.bidTime, args.productPrice, args.forBid
                    )
                findNavController().navigate(action)
            }
            else{
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun observe() {
        viewModel.mState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleStateChange(state) }.launchIn(lifecycleScope)
    }

    private fun addProductToSaved() {
        binding.addToSavedBtn.setOnClickListener {
            binding.addToSavedBtn.setImageResource(com.tradeasy.R.drawable.ic_baseline_bookmark_24_filled)
            viewModel.addProductToSaved(ProdIdReq(args.productId))
        }
    }


    //  STATE HANDLER
    private fun handleStateChange(state: AddProductToSavedFragmentSate) {
        when (state) {
            is AddProductToSavedFragmentSate.Init -> Unit
            is AddProductToSavedFragmentSate.ErrorSaving -> handleErrorSaving(state.rawResponse)
            is AddProductToSavedFragmentSate.SuccessSaving -> handleSuccessSaving(state.user)
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
        // Toast.makeText(requireActivity(), "Loading", Toast.LENGTH_SHORT).show()
    }

    private fun handleSuccessSaving(user: User) {

        sharedPrefs.setUser(user)

    }


    private fun checkPhonePermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), PERMISSION_CODE
            )
            false
        }
    }

    private fun makePhoneCall() {


        binding.sellerPhoneNumber.setOnClickListener(View.OnClickListener {

            if (checkPhonePermission()) {
                val i = Intent(Intent.ACTION_CALL)
                i.data = Uri.parse("tel:${args.userPhoneNumber}")
                startActivity(i)
            }
        })
    }


// function to trigger a phone call


}