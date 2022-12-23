package com.tradeasy.ui.selling.product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.tradeasy.databinding.FragmentAdditionalInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


@AndroidEntryPoint
class AdditionalInfoFragment : Fragment() {
    private lateinit var binding: FragmentAdditionalInfoBinding
    private val viewModel: AddProductViewModel by viewModels()
    private val args: com.tradeasy.ui.selling.product.AdditionalInfoFragmentArgs by navArgs()
    private val requestGallery = 2121
    private val file = mutableListOf<File>()

    private val imageLink = mutableListOf<String>()
    private val images = mutableListOf<Uri>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentAdditionalInfoBinding.inflate(inflater, container, false)
        addProduct()
        observe()

        return binding.root
    }

    // create a list of images
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        binding.prodImagesRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.prodImagesRV.adapter = ProductImagesAdapter(mutableListOf())

        binding.uploadImage.setOnClickListener {

   ImagePicker.with(this).start(requestGallery)

        }

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

    private fun addProduct() {


        // get current time kotlin in mills
        val currentTime = System.currentTimeMillis()
        var endTime: Long = 0

//        file.map {
//            MultipartBody.Part.createFormData(
//                "image",
//                it.name,
//                it.asRequestBody("image/*".toMediaTypeOrNull())
//            )
//        }
        binding.addProduct.setOnClickListener {

            val bidState: Boolean = binding.forBid.isChecked
// send images to server as a list
            val imageList = mutableListOf<MultipartBody.Part>()


            when (binding.bidDuration.text.toString().trim()) {
                "1m" -> {
                    endTime = (currentTime + 60000)

                }
                "1d" -> {
                    endTime = (currentTime + 86400000)
                }
                "1w" -> {
                    endTime = (currentTime + 604800000)
                }
            }
            val category = MultipartBody.Part.createFormData("category", args.prodCategory)
            val name = MultipartBody.Part.createFormData("name", args.prodName)
            val description = MultipartBody.Part.createFormData("description", args.prodDesc)
            val price = MultipartBody.Part.createFormData("price", args.prodPrice)
            val image =
                file.map {
                    MultipartBody.Part.createFormData(
                        "image",
                        it.name,
                        it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
            val quantity = MultipartBody.Part.createFormData("quantity", 0.toString())
            val forBid = MultipartBody.Part.createFormData("for_bid", bidState.toString())
            val bidEndDate = MultipartBody.Part.createFormData("bid_end_date", endTime.toString())
            println(currentTime)
            println("BBB $image")
            viewModel.addProduct(
                category, name, description, price, image, quantity, forBid, bidEndDate
            )

        }

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
                setResultOkToPreviousFragment()
                findNavController().navigateUp()
            }
            is AddProductFragmentSate.ShowToast -> {
                println(state.message)
            }
            is AddProductFragmentSate.Init -> Unit
        }
    }


// get image path from uri


    private fun handleLoading(isLoading: Boolean) {
        //   binding.saveButton.isEnabled = !isLoading
    }

}