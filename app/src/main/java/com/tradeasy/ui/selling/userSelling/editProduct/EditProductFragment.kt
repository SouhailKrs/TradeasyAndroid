package com.tradeasy.ui.selling.userSelling.editProduct

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.tradeasy.R
import com.tradeasy.databinding.FragmentEditProductBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
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
class EditProductFragment : Fragment() {
    private lateinit var binding: FragmentEditProductBinding
    private val viewModel: EditProductViewModel by viewModels()

    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
    private val requestGallery = 2121
    private var file = mutableListOf<File>()
    private val imageLink = mutableListOf<String>()
    private val images = mutableListOf<Uri>()


    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        editProduct()
        observe()
        setupView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // setupView()
        (activity as MainActivity?)?.setupToolBar("Edit Product", false, false)




        editProdButtonHandler()
        goToBidChoices()
        bidDurationState()
        setupRecyclerView()
        binding.uploadImage.setOnClickListener {
            ImagePicker.with(this).cropSquare().start(requestGallery)
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Edit Product", false, false)

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


        binding.editProductBtn.setOnClickListener {
            if (validate()) {
                val bidState: Boolean = binding.forBid.isChecked
                val imageList = mutableListOf<MultipartBody.Part>()
                val name = binding.editProductName
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


                lateinit var prodId: MultipartBody.Part
                sharedDataViewModel.prodId.observe(viewLifecycleOwner) { productId ->

                    prodId = MultipartBody.Part.createFormData("prod_id", productId)
                }


                val image = file.map {
                    MultipartBody.Part.createFormData(
                        "image", it.name, it.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                }
                val prodName = MultipartBody.Part.createFormData("name", name.text.toString())
                val prodDesc =
                    MultipartBody.Part.createFormData("description", description.text.toString())
                val prodPrice = MultipartBody.Part.createFormData("price", price.text.toString())
                val quantity = MultipartBody.Part.createFormData("quantity", 0.toString())
                val forBid = MultipartBody.Part.createFormData("for_bid", bidState.toString())
                val bidEndDate =
                    MultipartBody.Part.createFormData("bid_end_date", endTime.toString())
               sharedDataViewModel.prodCat.observe(viewLifecycleOwner) { category ->
                    val prodCategory =
                        MultipartBody.Part.createFormData("category", category)
                    viewModel.editProduct(
                        prodCategory,
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




            }

        }

    }

    private fun validate(): Boolean {
        if (file.isEmpty()) {
            Snackbar.make(binding.root, "Please add at least one image", Snackbar.LENGTH_SHORT)
                .show()
            return false
        }
        if (sharedPrefs.getBidDuration() == null && binding.forBid.isChecked) {

            AlertDialog.Builder(requireContext()).setMessage("Please select bid duration")
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return false
        }

        if(binding.editProductName.text.toString().isEmpty()){
            binding.editProductName.error = "Please enter product name"
            return false
        }
        if(binding.productDescription.text.toString().isEmpty()){
            binding.productDescription.error = "Please enter product description"
            return false
        }
        if(binding.productPrice.text.toString().isEmpty()){
            binding.productPrice.error = "Please enter product price"
            return false
        }
if(binding.productPrice.text.toString()=="0"){
    binding.productPrice.error = "Price cannot be 0"
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
                    binding.root, "Product updated successfully", Snackbar.LENGTH_SHORT
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
        binding.bidDurationField.visibility = View.GONE
        binding.forBid.setOnCheckedChangeListener { _, isChecked ->
            binding.bidDurationField.visibility = if (isChecked) View.VISIBLE else View.GONE
            if (!isChecked) {
                sharedPrefs.clearBidDuration()
                binding.bidDuration.setText("Category")
            }
        }

    }

    private fun setupView() {

        sharedDataViewModel.prodName.observe(viewLifecycleOwner) { prodName ->
            binding.editProductName.setText(prodName)

        }


        sharedDataViewModel.prodPrice.observe(viewLifecycleOwner) { prodPrice ->
            binding.productPrice.setText(prodPrice.toString())

        }
        sharedDataViewModel.prodDesc.observe(viewLifecycleOwner) { prodDesc ->
            binding.productDescription.setText(prodDesc.toString())


        }


        sharedDataViewModel.selling.observe(viewLifecycleOwner) { selling ->
            binding.forBidConstraint.visibility = if (selling) View.GONE else View.VISIBLE

        }
        sharedDataViewModel.prodImages.observe(viewLifecycleOwner) { prodImages ->

// create a list of string images
            val images = mutableListOf<String>()
            prodImages.forEach {
                images.add(it)
            }
            val mAdapter = EditProductImagesAdapter(images)


            binding.prodImagesRV.adapter = mAdapter

        }


    }



   private fun editProdButtonHandler() {





//        val productName = binding.editProductName
//        val productDesc = binding.productDescription
//        val productCat = binding.productCategory
//        val productPrice = binding.productPrice
//        val editProductBtn = binding.editProductBtn
//        editProductBtn.isEnabled = false
//        editProductBtn.alpha = 0.5f
//
//        productName.addTextChangedListener {
//
//            editProductBtn.isEnabled =
//                productCat.text.toString() != sharedDataViewModel.prodCat.value&& productName.text.toString() != sharedDataViewModel.prodName.value &&  productDesc.text.toString() != sharedDataViewModel.prodDesc.value && productName.text!!.isNotBlank() && productDesc.text!!.isNotBlank() && binding.productCategory.text!!.isNotBlank() && binding.productPrice.text!!.isNotEmpty()   || productCat.text.toString() != sharedDataViewModel.prodCat.value && productPrice.text.toString() != sharedDataViewModel.prodPrice.value.toString()  && productPrice.text.toString() != "0" && productCat.text.toString() != "Category"
//            editProductBtn.alpha = if (editProductBtn.isEnabled) 1f else 0.5f
//            Toast.makeText(requireContext(), productName.text, Toast.LENGTH_SHORT).show()
//
//
//        }
//
//        //  && productCat.text.toString() != sharedDataViewModel.prodCat.value && productPrice.text.toString() != sharedDataViewModel.prodPrice.value.toString()
//
////        binding.productDescription.addTextChangedListener {
////
////            editProductBtn.isEnabled =
////                productName != sharedDataViewModel.prodName.value && productDesc != sharedDataViewModel.prodDesc.value && productCat != sharedDataViewModel.prodCat.value && productPrice != sharedDataViewModel.prodPrice.value.toString() && productName.isNotBlank() && productDesc.isNotBlank() && productCat.isNotBlank() && productPrice.isNotEmpty() && productPrice != "0" && productCat != "Category"
////            editProductBtn.alpha = if (editProductBtn.isEnabled) 1f else 0.5f
////
////        }
////        binding.productCategory.addTextChangedListener {
////
////            editProductBtn.isEnabled =
////                productName != sharedDataViewModel.prodName.value && productDesc != sharedDataViewModel.prodDesc.value && productCat != sharedDataViewModel.prodCat.value && productPrice != sharedDataViewModel.prodPrice.value.toString() && productName.isNotBlank() && productDesc.isNotBlank() && productCat.isNotBlank() && productPrice.isNotEmpty() && productPrice != "0" && productCat != "Category"
////            editProductBtn.alpha = if (editProductBtn.isEnabled) 1f else 0.5f
////
////        }
////        binding.productPrice.addTextChangedListener {
////
////            editProductBtn.isEnabled =
////                productName != sharedDataViewModel.prodName.value && productDesc != sharedDataViewModel.prodDesc.value && productCat != sharedDataViewModel.prodCat.value && productPrice != sharedDataViewModel.prodPrice.value.toString() && productName.isNotBlank() && productDesc.isNotBlank() && productCat.isNotBlank() && productPrice.isNotEmpty() && productPrice != "0" && productCat != "Category"
////            editProductBtn.alpha = if (editProductBtn.isEnabled) 1f else 0.5f
////
////        }
//
//
    }

}