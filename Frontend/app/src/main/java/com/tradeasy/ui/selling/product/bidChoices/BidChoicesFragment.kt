package com.tradeasy.ui.selling.product.bidChoices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tradeasy.databinding.FragmentBidChoicesBinding
import com.tradeasy.ui.selling.product.AdditionalInfoFragmentArgs
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class BidChoicesFragment : Fragment() {
    private lateinit var binding: FragmentBidChoicesBinding
    private val args: AdditionalInfoFragmentArgs? by navArgs()


    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentBidChoicesBinding.inflate(inflater, container, false)
        val arrayAdapter: ArrayAdapter<*>
        val bidChoices = arrayOf(
            "1 Hour", "1 Day", "1 Week"
        )

        // access the listView from xml file
        arrayAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_list_item_1, bidChoices
        )
        getSelectedItem()
        binding.bidList.adapter = arrayAdapter
        return binding.root
    }

    // fucntion to get the selected item from the list
    private fun getSelectedItem()   {

        binding.bidList.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position).toString()
sharedPrefs.setBidDuration(selectedItem)

findNavController().navigateUp()

        }

    }




}