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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.models.SlideModel
import com.tradeasy.R
import com.tradeasy.data.product.remote.dto.ProdIdReq
import com.tradeasy.databinding.FragmentProductItemBinding
import com.tradeasy.domain.user.entity.User
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
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
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
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

        sharedViewModel.prodName.observe(viewLifecycleOwner) { prodName ->
            (activity as MainActivity?)?.setupToolBar(prodName, false)
        }
        val height = getScreenSize(requireContext()).first.toDouble()
        val width = getScreenSize(requireContext()).second.toDouble()
        // get the difference between height and width
        val difference = height.toDouble() / width.toDouble()
        // get the percentage of the difference between height and width
        binding.sellerProfilePicture.layoutParams.height = ((height / 17).toInt())
        binding.sellerProfilePicture.layoutParams.width = (((width / 17) * difference).toInt())

        binding.apply {
            val imageList = ArrayList<SlideModel>()


            sharedViewModel.prodPrice.observe(viewLifecycleOwner) { prodPrice ->
                productPrice.text = "$prodPrice TND"

            }
            sharedViewModel.prodName.observe(viewLifecycleOwner) { prodName ->
                productName.text = prodName.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }
            }
            sharedViewModel.prodImages.observe(viewLifecycleOwner) { prodImages ->

                for (i in prodImages.indices) {
                    imageList.add(SlideModel(prodImages[i].toString()))
                }
                imageSlider.setImageList(imageList)
            }
            sharedViewModel.ownerUsername.observe(viewLifecycleOwner) { ownerUsername ->
                sellerUsername.text = ownerUsername
            }
            sharedViewModel.ownerProfilePicture.observe(viewLifecycleOwner) { ownerProfilePicture ->
                if (ownerProfilePicture.isNotEmpty()) {
                    imageLoader(ownerProfilePicture, binding.sellerProfilePicture)
                } else {
                    binding.sellerProfilePicture.setImageResource(R.drawable.default_profile_picture)
                }
            }
            sharedViewModel.ownerPhoneNumber.observe(viewLifecycleOwner) { ownerPhoneNumber ->
                sellerPhoneNumber.text = ownerPhoneNumber
            }
        }
// PLACE BID BTN VISIBILITY
        sharedViewModel.forBid.observe(viewLifecycleOwner) { forBid ->
            binding.goToPlaceBidBtn.visibility = if (forBid) View.VISIBLE else View.GONE
        }


        makePhoneCall()
    }

    private fun handleSavedButton() {

        if (isLoggedIn(sharedPrefs)) {
            if (sharedPrefs.getUser()!!.savedProducts!!.size != 0) {
                for (i in sharedPrefs.getUser()!!.savedProducts!!.indices) {
                    sharedViewModel.prodId.observe(viewLifecycleOwner) { prodId ->
                        if (sharedPrefs.getUser()!!.savedProducts!![i].productId == prodId) {
                            binding.addToSavedBtn.setImageResource(com.tradeasy.R.drawable.ic_baseline_bookmark_24_filled)
                        } else {
                            binding.addToSavedBtn.setImageResource(com.tradeasy.R.drawable.ic_outline_bookmark_border_24)
                        }

                    }

                }
            }
        } else {
            binding.addToSavedBtn.setOnClickListener {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun goToBidFragment() {

        binding.goToPlaceBidBtn.setOnClickListener {

            if (isLoggedIn(sharedPrefs)) {

                findNavController().navigate(R.id.action_productItemFragment_to_placeBidFragment)
            } else {
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

            sharedViewModel.prodId.observe(viewLifecycleOwner) { prodId ->
                viewModel.addProductToSaved(ProdIdReq(prodId))
            }
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
                sharedViewModel.ownerPhoneNumber.observe(viewLifecycleOwner) { ownerPhoneNumber ->
                    // remove whitespace from phone number
                    val phoneNumber = ownerPhoneNumber.replace("\\s".toRegex(), "")
              println("PHONE NUMBER $phoneNumber")
                    i.data = Uri.parse("tel:$phoneNumber")
                }

                startActivity(i)
            }
        })
    }


// function to trigger a phone call


}