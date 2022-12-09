package com.tradeasy.ui.selling.product

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.databinding.FragmentAdditionalInfoBinding
import com.tradeasy.domain.model.Product
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class AdditionalInfoFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    private lateinit var binding: FragmentAdditionalInfoBinding
    private val viewModel: AddProductViewModel by viewModels()
    private val args: com.tradeasy.ui.selling.product.AdditionalInfoFragmentArgs by navArgs()

    // create a list of images
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
        binding.prodImagesRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.prodImagesRV.adapter = ProductImagesAdapter(mutableListOf())
        binding.uploadImage.setOnClickListener {
            openGallery()
        }
    }


    private val startForResultOpenGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data!!.data
                images.add(selectedImageUri!!)
                println(images.toString())
                // set the selected image to the image view of recycler view item
                binding.prodImagesRV.adapter = ProductImagesAdapter(images)


            }
        }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        startForResultOpenGallery.launch(intent)

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
            is AddProductFragmentSate.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is AddProductFragmentSate.Init -> Unit
        }
    }

    private fun addProduct() {

        var bidState: Boolean = false
        // get current time kotlin in mills
        val currentTime = System.currentTimeMillis()
        var endTime :Long = 0
        val forBid = binding.forBid
        bidState = forBid.isChecked





        binding.addProduct.setOnClickListener {
            println("bidState $bidState")
            println("second: ${forBid.isChecked}")
            val name = args.prodName
            val description = args.prodDesc
            val category = args.prodCategory
            val price = args.prodPrice
println("AAAA  $name $description $category $price")
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
            println(currentTime)
            println("BBB $endTime")
            viewModel.addProduct(
                Product(
                    "",
                    category,
                    name,
                    description,
                    price.toFloat(),
                    images.toString(),
                    0,
                    currentTime,
                    forBid.isChecked,
                    endTime,
                    false,
                    false,
                    ""
                )
            )

        }

    }
// get image path from uri










    private fun handleLoading(isLoading: Boolean) {
        //   binding.saveButton.isEnabled = !isLoading
    }

}