package com.tradeasy.ui.profile.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.R
import com.tradeasy.databinding.FragmentSavedProductsBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@AndroidEntryPoint
class SavedProductsFragment : Fragment() {

    private lateinit var binding: FragmentSavedProductsBinding

    private val viewModel: SavedProductsViewModel by viewModels()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSavedProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        (activity as MainActivity?)?.setupToolBar("Saved", false, false)
        observe()
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {
                viewModel.fetchSavedProducts()
            }
        }
    }

    private fun observe() {
        observeState()
        observeSavedProducts()
    }

    private fun observeState() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSavedProducts() {
        viewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleSavedProducts(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun handleState(state: SavedProductsFragmentState) {
        when (state) {
            is SavedProductsFragmentState.IsLoading -> handleLoading(state.isLoading)
            is SavedProductsFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is SavedProductsFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val mAdapter = SavedProductsAdapter(mutableListOf(), onItemClick = {
            val imagesArray = Array(it.image!!.size) { i -> it.image[i] }


            sharedViewModel.setProdName(it.name!!)
            sharedViewModel.setProdDesc(it.description!!)
            sharedViewModel.setProdCat(it.category!!)
            sharedViewModel.setProdPrice(it.price!!)
            sharedViewModel.setBidEndDate(it.bidEndDate!!)
            sharedViewModel.setProdQuantity(it.quantity!!)
            sharedViewModel.setAddedDate(it.addedDate!!)
            sharedViewModel.setForBid(it.forBid!!)
            sharedViewModel.setProdId(it.productId!!)
            sharedViewModel.setOwnerUsername(it.username!!)
            sharedViewModel.setOwnerPhoneNumber(it.userPhoneNumber!!)
            sharedViewModel.setOwnerProfilePicture(it.userProfilePicture!!)
            sharedViewModel.setProdImages(imagesArray)


         findNavController().navigate(R.id.action_savedProductsFragment_to_productItemFragment)

        })


        binding.savedProdsRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun handleSavedProducts(products: List<Product>) {
        binding.savedProdsRV.adapter?.let {
            if (it is SavedProductsAdapter) {
                it.updateList(products)
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }

}