package com.tradeasy.ui.selling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tradeasy.R
import com.tradeasy.databinding.FragmentSellingBinding
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SellingFragment : Fragment() {
    private lateinit var binding: FragmentSellingBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentSellingBinding.inflate(inflater, container, false)

        if(sharedPrefs.getUser() == null ){

            findNavController().navigate(R.id.loginFragment)


        }
        binding.goToAddProduct.setOnClickListener {
            findNavController().navigate(R.id.addProductFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




    }
}