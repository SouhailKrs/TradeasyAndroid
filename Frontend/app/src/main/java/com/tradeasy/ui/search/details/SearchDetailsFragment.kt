package com.tradeasy.ui.search.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.data.product.remote.dto.SearchReq
import com.tradeasy.databinding.FragmentSearchDetailsBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.search.SearchFragmentSate
import com.tradeasy.ui.search.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchDetailsFragment : Fragment() {

    private lateinit var binding: FragmentSearchDetailsBinding
    private val args: com.tradeasy.ui.search.details.SearchDetailsFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentSearchDetailsBinding.inflate(inflater, container, false)
        observe()
        setupRecyclerView()
        setupSearchDetails()
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {
                val searchReq = SearchReq(args.searchInput)
                viewModel.fetchSearchedProducts(searchReq)

            }
        }

        return binding.root
    }
private fun setupSearchDetails(){
    val searchReq = SearchReq(args.searchInput)
    viewModel.fetchSearchedProducts(searchReq)



}
    private fun setupRecyclerView() {
        val mAdapter = SearchDetailsAdapter(mutableListOf(), onItemClick = {
            val imagesArray = Array(it.image!!.size) { i -> it.image[i] }
val action = SearchDetailsFragmentDirections.actionSearchDetailsFragmentToProductItemFragment(

    it.name!!,
    it.description!!,
    it.category!!,
    it.price!!,
    it.bidEndDate.toString(),
    it.quantity!!,
    it.addedDate.toString(),
    it.forBid!!,
    it.bidEndDate.toString(),
    it.productId!!,
    it.username!!,
    it.userPhoneNumber!!,
    it.userProfilePicture!!,
    imagesArray
)
findNavController().navigate(action)
        })



        binding.searchDetailsRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observe() {

        observeState()
        observeSearchDetails()
    }

    private fun observeState() {

        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSearchDetails() {

        viewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleSearch(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: SearchFragmentSate) {

        when (state) {
            is SearchFragmentSate.IsLoading -> handleLoading(state.isLoading)
            is SearchFragmentSate.ShowToast -> {

                Toast.makeText(
                    requireActivity(), state.message, Toast.LENGTH_SHORT
                ).show()

            }

            is SearchFragmentSate.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSearch(products: List<Product>) {

        binding.searchDetailsRV.adapter?.let {
            if (it is SearchDetailsAdapter) {
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