package com.tradeasy.ui.selling.product


import android.app.Activity.RESULT_OK
import android.content.Intent
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
import com.tradeasy.R
import com.tradeasy.databinding.FragmentAddProductBinding
import com.tradeasy.domain.model.Product
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class AddProductFragment : Fragment(R.layout.fragment_add_product){
    private lateinit var binding: FragmentAddProductBinding
    private var SELECT_PICTURE = 200
    private val viewModel : AddProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addProduct()
        observe()

    }

    private fun setResultOkToPreviousFragment(){
        val r = Bundle().apply {
            putBoolean("success_create", true)
        }
        setFragmentResult("success_create", r)
    }

    private fun observe(){
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state -> handleState(state) }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: AddProductFragmentSate){
        when(state){
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

    private fun addProduct(){

       var bidState:Boolean = false
        // get current time kotlin in mills
        val currentTime = System.currentTimeMillis()
        var endTime=0;
        val forBid= binding.forBid
        bidState = forBid.isChecked


        /*val bitmap = binding.uploadedImage.drawToBitmap();
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val profile_pic = stream.toByteArray()*/
        binding.uploadImage.setOnClickListener {
            imageChooser()
        }

        binding.addProduct.setOnClickListener {
            println("bidState $bidState")
            println("secodn: ${forBid.isChecked}")
            val name = binding.productName.text.toString().trim()
            val description = binding.productDescription.text.toString().trim()
            val category = binding.productCategory.text.toString().trim()
            val price = binding.productPrice.text.toString().trim()

            when (binding.bidDuration.text.toString().trim()) {
                "1m" -> {
                    endTime = (currentTime + 60000).toInt()

                }
                "1d" -> {
                    endTime = (currentTime + 86400000).toInt()
                }
                "1w" -> {
                    endTime = (currentTime + 604800000).toInt()
                }
            }




                viewModel.addProduct(Product("",category,name,description,price.toFloat(),"",0,currentTime,forBid.isChecked,endTime,false,false,"") )

        }
    }



    private fun handleLoading(isLoading: Boolean) {
     //   binding.saveButton.isEnabled = !isLoading
    }





    override fun onDestroy() {
        super.onDestroy()
      //  _binding = null
    }

    private fun imageChooser() {

        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)  {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                val selectedImageUri = data?.data
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.uploadedImage.setImageURI(selectedImageUri)

                }
            }
        }
    }
}




