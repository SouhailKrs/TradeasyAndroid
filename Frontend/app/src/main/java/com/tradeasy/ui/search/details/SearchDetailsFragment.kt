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
import com.tradeasy.databinding.FragmentSearchDetailsBinding
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.SearchReq
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
        println("zzzzzz")
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
        println("loadingggggggg7")
        observeState()
        observeSearchDetails()
    }

    private fun observeState() {
        println("loadingggggggg6")
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSearchDetails() {
        println("loadingggggggg5")
        viewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleSearch(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: SearchFragmentSate) {
        println("loadingggggggg4")
        when (state) {
            is SearchFragmentSate.IsLoading -> handleLoading(state.isLoading)
            is SearchFragmentSate.ShowToast -> {
                println("loadingggggggg3")
                Toast.makeText(
                    requireActivity(), state.message, Toast.LENGTH_SHORT
                ).show()
                println(state.message)
            }

            is SearchFragmentSate.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSearch(products: List<Product>) {
        println("loadingggggggg2")
        binding.searchDetailsRV.adapter?.let {
            if (it is SearchDetailsAdapter) {
                it.updateList(products)
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        println("loadingggggggg1")
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }
}