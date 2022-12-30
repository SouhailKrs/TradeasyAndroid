package com.tradeasy.ui.selling.product

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentAdditionalInfoBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.ProdImagesDataViewModel
import com.tradeasy.ui.selling.product.bidChoices.BidChoicesFragment
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject


@AndroidEntryPoint
class AdditionalInfoFragment : Fragment() {
    private lateinit var binding: FragmentAdditionalInfoBinding
    private val viewModel: AddProductViewModel by viewModels()
    private val args: AdditionalInfoFragmentArgs by navArgs()
    private val requestGallery = 2121
    private val file = mutableListOf<File>()
    private val bidChoicesFragment = BidChoicesFragment()
    private val imageLink = mutableListOf<String>()
    private val images = mutableListOf<Uri>()
    private val prodImagesViewModel: ProdImagesDataViewModel by activityViewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAdditionalInfoBinding.inflate(inflater, container, false)
        addProduct()
        observe()
 val test : ImageButton? = requireActivity().findViewById(R.id.deleteImgBtn)


        return binding.root
    }

    // create a list of images
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        goToBidChoices()
        setupRecyclerView()
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
        bidDurationState()
        binding.uploadImage.setOnClickListener {

            ImagePicker.with(this).start(requestGallery)

        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Sell a product", false, false)
        binding.bidDuration.setText(sharedPrefs.getBidDuration())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == requestGallery && data != null) {
                val fileUri = data.data
                //setImage(fileUri!!)
                // binding.prodImagesRV.adapter = ProductImagesAdapter(fileUri!!)
                ImagePicker.getFile(data)?.let {
                    file.add(it)
                    imageLink.add(fileUri.toString())
                }
                images.add(fileUri!!)
                binding.prodImagesRV.adapter = ProductImagesAdapter(images)

            }
        }
    }

    private fun goToBidChoices() {
        binding.bidDuration.setOnClickListener {
            findNavController().navigate(R.id.action_additionalInfoFragment_to_bidChoicesFragment)

        }
    }

    private fun addProduct() {
 // check if the user added images or not

        // get current time kotlin in mills
        val currentTime = System.currentTimeMillis()
        var endTime: Long = 0


        binding.addProduct.setOnClickListener {
            if (validate()) {
                val bidState: Boolean = binding.forBid.isChecked
// send images to server as a list
                mutableListOf<MultipartBody.Part>()


                when (sharedPrefs.getBidDuration()) {
                    "1 Hour" -> {

                        endTime = currentTime + 3600000

                    }
                    "1 Day" -> {
                        endTime = (currentTime + 86400000)
                    }
                    "1 Week" -> {
                        endTime = (currentTime + 604800000)
                    }
                }
                val category = MultipartBody.Part.createFormData(
                    "category", sharedPrefs.getProdCategory()!!
                )
                val name = MultipartBody.Part.createFormData("name", args.prodName.trimEnd())
                val description = MultipartBody.Part.createFormData("description", args.prodDesc.trimEnd())
                val price = MultipartBody.Part.createFormData("price", args.prodPrice)
                val image = file.map {
                    MultipartBody.Part.createFormData(
                        "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                val quantity = MultipartBody.Part.createFormData("quantity", 0.toString())
                val forBid = MultipartBody.Part.createFormData("for_bid", bidState.toString())
                val bidEndDate =
                    MultipartBody.Part.createFormData("bid_end_date", endTime.toString())

                viewModel.addProduct(
                    category, name, description, price, image, quantity, forBid, bidEndDate
                )

            }
        }

    }
private fun validate() : Boolean {
    if(file.isEmpty()) {
        Snackbar.make(binding.root, "Please add at least one image", Snackbar.LENGTH_SHORT).show()
        return false
    }
    if (sharedPrefs.getBidDuration() == null && binding.forBid.isChecked) {

        AlertDialog.Builder(requireContext())
            .setMessage("Please select bid duration")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
        return false
    }
    return true
    }

    private fun setupRecyclerView() {
        val mAdapter = ProductImagesAdapter(mutableListOf())


        binding.prodImagesRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    // upload image from gallery

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

    private fun handleState(state: AddProductFragmentSate) {
        when (state) {
            is AddProductFragmentSate.IsLoading -> handleLoading(state.isLoading)
            is AddProductFragmentSate.SuccessCreate -> {
                Snackbar.make(
                    binding.root,
                    "Product added successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
                setResultOkToPreviousFragment()
                findNavController().navigate(R.id.sellingFragment)
            }
            is AddProductFragmentSate.ShowToast -> {

            }
            is AddProductFragmentSate.Init -> Unit
        }
    }


// get image path from uri


    private fun handleLoading(isLoading: Boolean) {
        binding.addProduct.isEnabled = !isLoading
        binding.addProduct.showProgress {
            progressColor = Color.WHITE
        }
    }
    private fun bidDurationState() {
binding.bidDurationField.visibility= View.GONE
binding.forBid.setOnCheckedChangeListener { _, isChecked ->
    binding.bidDurationField.visibility= if (isChecked) View.VISIBLE else View.GONE
    if(!isChecked){
      sharedPrefs.clearBidDuration()
        binding.bidDuration.setText("Category")
    }
    }

}}