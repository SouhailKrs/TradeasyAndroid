package com.tradeasy.ui.selling.userProducts

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentEditProdBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.selling.product.ProductImagesAdapter
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
class EditProdFragment : Fragment() {
    private lateinit var binding: FragmentEditProdBinding
    private val viewModel: EditProductViewModel by viewModels()

    private val args: EditProdFragmentArgs by navArgs()
    private val requestGallery = 2121
    private val file = mutableListOf<File>()
    private val imageLink = mutableListOf<String>()
    private val images = mutableListOf<Uri>()


    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProdBinding.inflate(inflater, container, false)
       editProduct()
       observe()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)?.setupToolBar("Edit Product",false,false)

        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true) // show back button
        binding.productCategory.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }
        goToBidChoices()
        bidDurationState()
        setupRecyclerView()
        binding.uploadImage.setOnClickListener {
            ImagePicker.with(this).start(requestGallery)
        }

    }
    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Edit Product", false, false)

        binding.productCategory.setText(sharedPrefs.getProdCategory())
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
            findNavController().navigate(R.id.bidChoicesFragment)
        }
    }

        private fun editProduct() {
            val currentTime = System.currentTimeMillis()
            var endTime: Long = 0
            var test : String = ""

            Toast.makeText(requireContext(), args.prodId, Toast.LENGTH_SHORT).show()


        binding.addProduct.setOnClickListener {
            if (validate()) {
                val bidState: Boolean = binding.forBid.isChecked
                val imageList = mutableListOf<MultipartBody.Part>()
                val name = binding.productName
                val description = binding.productDescription
                val price = binding.productPrice

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


                val prodId = MultipartBody.Part.createFormData("prod_id", args.prodId)
                val image = file.map {
                    MultipartBody.Part.createFormData(
                        "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                val prodName = MultipartBody.Part.createFormData("name", name.text.toString())
                val prodDesc = MultipartBody.Part.createFormData("description", description.text.toString())
                val prodPrice = MultipartBody.Part.createFormData("price", price.text.toString())
                val quantity = MultipartBody.Part.createFormData("quantity", 0.toString())
                val forBid = MultipartBody.Part.createFormData("for_bid", bidState.toString())
                val bidEndDate =
                    MultipartBody.Part.createFormData("bid_end_date", endTime.toString())
                val category =
                    MultipartBody.Part.createFormData("category", args.category)

                println("& "+ prodName)

                viewModel.editProduct(
                    category,
                    prodName,
                    prodDesc,
                    prodPrice,
                    image,
                    quantity,
                    forBid,
                    bidEndDate,
                    prodId
                )

            }
            println("after: "+ args.prodId)
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

    private fun observe() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: EditProductFragmentSate) {
        when (state) {
            is EditProductFragmentSate.IsLoading -> handleLoading(state.isLoading)
            is EditProductFragmentSate.SuccessCreate -> {
                Snackbar.make(
                    binding.root,
                    "Product added successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
                setResultOkToPreviousFragment()
                findNavController().navigate(R.id.sellingFragment)
            }
            is EditProductFragmentSate.ShowToast -> {

            }
            is EditProductFragmentSate.Init -> Unit
        }
    }

    private fun setResultOkToPreviousFragment() {
        val r = Bundle().apply {
            putBoolean("success_edit", true)
        }
        setFragmentResult("success_edit", r)
    }

    private fun handleLoading(isLoading: Boolean) {
       /* binding.addProduct.isEnabled = !isLoading
        binding.addProduct.showProgress {
            progressColor = Color.WHITE
        }*/
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

    }

}