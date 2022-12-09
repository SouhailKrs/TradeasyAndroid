package com.tradeasy.ui.selling.product


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tradeasy.R
import com.tradeasy.databinding.FragmentAddProductBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductFragment : Fragment(R.layout.fragment_add_product) {
    private lateinit var binding: FragmentAddProductBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


binding.goToAdditionalInfo.setOnClickListener {
    goToAdditionalDetails()
        }
    }


private fun goToAdditionalDetails() {

    val action=AddProductFragmentDirections.actionAddProductFragmentToAdditionalInfoFragment(

        binding.productName.text.toString(),
        binding.productDescription.text.toString(),
        binding.productCategory.text.toString(),
        binding.productPrice.text.toString(),

    )


    findNavController().navigate(action)



    }






}




