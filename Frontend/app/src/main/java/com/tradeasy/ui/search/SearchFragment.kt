package com.tradeasy.ui.search

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.databinding.FragmentSearchBinding
import com.tradeasy.domain.model.Product
import com.tradeasy.domain.model.SearchReq
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        observe()
        setupRecyclerView()
        setupSearchView()



        // search view text change listener

        setFragmentResultListener("success_create") { requestKey, bundle ->
            if (bundle.getBoolean("success_create")) {
                val searchReq = SearchReq(binding.searchView.query.toString())
                viewModel.fetchSearchedProducts(searchReq)
            }
        }

        return binding.root

    }

    private fun setupRecyclerView() {
        val mAdapter = SearchAdapter(mutableListOf())


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
                val searchReq = SearchReq(newText)
                viewModel.fetchSearchedProducts(searchReq)
                return true
            }
        })
// check if search view is focused
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            binding.searchRV.isVisible = hasFocus
        }

 //  binding.searchRV.isVisible=checkFocusRec(searchView)





    }

}