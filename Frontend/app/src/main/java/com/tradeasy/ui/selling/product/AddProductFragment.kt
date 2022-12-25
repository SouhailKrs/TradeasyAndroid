package com.tradeasy.ui.selling.product


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tradeasy.R
import com.tradeasy.databinding.FragmentAddProductBinding
import com.tradeasy.ui.MainActivity
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var binding: FragmentAddProductBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        nextButtonState()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
        binding.productCategory.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }



        binding.goToAdditionalInfo.setOnClickListener {
            if (sharedPrefs.getProdCategory() == null) {
                AlertDialog.Builder(requireContext()).setMessage("Please select a category")
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()

                    }.show()
            } else  {
            goToAdditionalDetails()
        }}
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
        binding.productCategory.setText(sharedPrefs.getProdCategory())
    }

    private fun goToAdditionalDetails() {

        val action = AddProductFragmentDirections.actionAddProductFragmentToAdditionalInfoFragment(

            binding.productName.text.toString(),
            binding.productDescription.text.toString(),
            binding.productPrice.text.toString(),


            )


        findNavController().navigate(action)


    }

    private fun nextButtonState() {


        val name = binding.productName
        val description = binding.productDescription
        val price = binding.productPrice
        val nextBtn = binding.goToAdditionalInfo
        nextBtn.isEnabled = false
        nextBtn.alpha = 0.5f
        name.addTextChangedListener {

            nextBtn.isEnabled =
                name.text!!.isNotBlank() && description.text!!.isNotBlank() && price.text!!.isNotEmpty()
            nextBtn.alpha = if (nextBtn.isEnabled) 1f else 0.5f


        }
        description.addTextChangedListener {

            nextBtn.isEnabled =
                name.text!!.isNotBlank() && description.text!!.isNotBlank() && price.text!!.isNotEmpty()
            nextBtn.alpha = if (nextBtn.isEnabled) 1f else 0.5f

        }

        price.addTextChangedListener {

            nextBtn.isEnabled =
                name.text!!.isNotBlank() && description.text!!.isNotBlank() && price.text!!.isNotEmpty()
            nextBtn.alpha = if (nextBtn.isEnabled) 1f else 0.5f

        }


    }
// save fragment state


}

// save fragment state on resume




