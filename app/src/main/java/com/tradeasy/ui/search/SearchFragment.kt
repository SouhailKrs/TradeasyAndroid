package com.tradeasy.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
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
import com.tradeasy.data.product.remote.dto.SearchReq
import com.tradeasy.databinding.FragmentSearchBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()


    @Inject
    lateinit var sharedPrefs: SharedPrefs
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        setupView()
        observe()
        setupRecyclerView()
        setupSearchView()


        // search view text change listener

        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {

                val searchReq = SearchReq(binding.searchView.query.toString())
                viewModel.fetchSearchedProducts(searchReq)
            }
        }

        return binding.root

    }
private fun setupView(){

    binding.searchImg.visibility = View.VISIBLE
    binding.searchTxt.visibility = View.VISIBLE

}
    override fun onDestroy() {
        super.onDestroy()
        // show toolbar
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
    private fun setupRecyclerView() {
        val mAdapter = SearchAdapter(mutableListOf(), onItemClick = {
            val imagesArray = Array(it.image!!.size) { i -> it.image[i] }
            sharedDataViewModel.setProdName(it.name!!)
            sharedDataViewModel.setProdDesc(it.description!!)
            sharedDataViewModel.setProdCat(it.category!!)
            sharedDataViewModel.setProdPrice(it.price!!)
            sharedDataViewModel.setBidEndDate(it.bidEndDate!!)
            sharedDataViewModel.setProdQuantity(it.quantity!!)
            sharedDataViewModel.setAddedDate(it.addedDate!!)
            sharedDataViewModel.setForBid(it.forBid!!)
            sharedDataViewModel.setProdId(it.productId!!)
            sharedDataViewModel.setOwnerUsername(it.username!!)
            sharedDataViewModel.setOwnerPhoneNumber(it.userPhoneNumber!!)
            sharedDataViewModel.setOwnerProfilePicture(it.userProfilePicture!!)
            sharedDataViewModel.setProdImages(imagesArray)
            sharedDataViewModel.setProdSelling(it.selling!!)
            sharedDataViewModel.setProdBade(it.bade!!)
            findNavController().navigate(R.id.action_searchFragment_to_searchDetailsFragment)
        })



        binding.searchRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observe() {
        observeState()
        observeSearch()
    }

    private fun observeState() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeSearch() {
        viewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleSearch(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: SearchFragmentSate) {
        when (state) {
            is SearchFragmentSate.IsLoading -> handleLoading(state.isLoading)
            is SearchFragmentSate.ShowToast -> {
                binding.searchTxt.visibility = View.VISIBLE
                binding.searchTxt.text = " No results found for " + binding.searchView.query.toString() + " !"


            }




            is SearchFragmentSate.Init -> Unit
            else -> {

            }
        }
    }

    private fun handleSearch(products: List<Product>) {
        binding.searchRV.adapter?.let {
            if (it is SearchAdapter) {
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
    private fun setupSearchView() {
val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(binding.searchView.query.isNullOrBlank()){
                    binding.searchRV.isVisible = false
                    binding.searchImg.visibility = View.VISIBLE
                    binding.searchTxt.visibility = View.VISIBLE
                    binding.searchTxt.text = "Search for you next favourite item ! "
                    return false
                }
                else {
                    binding.searchImg.visibility = View.INVISIBLE
                    binding.searchTxt.visibility = View.GONE
                    binding.searchRV.isVisible = true
                    val searchReq = SearchReq(newText)
                    viewModel.fetchSearchedProducts(searchReq)
                    return true
                }

            }
        })
// check if search view is focused
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            binding.searchRV.isVisible = hasFocus

        }

    }

}