package com.tradeasy.ui.home.productsByCategory

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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.data.product.remote.dto.GetByCatReq
import com.tradeasy.databinding.FragmentProductsByCategoryBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProductsByCategoryFragment : Fragment() {

private lateinit var binding: FragmentProductsByCategoryBinding
    private val viewModel: ProductsByCategoryViewModel by viewModels()
    private val args : ProductsByCategoryFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as MainActivity?)?.setupToolBar(args.category.capitalize(), false, false)
        binding = FragmentProductsByCategoryBinding.inflate(inflater, container, false)
        observe()
        setupRecyclerView()
        fetchProductsByCategory()
        return binding.root
    }
    // fucntion to capitialize the first letter of the category
    private fun String.capitalize() = this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }
    private  fun fetchProductsByCategory(){
        val req = GetByCatReq(args.category)
        viewModel.fetchProdByCat(req)
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {

                val req = GetByCatReq("mmmm")
                viewModel.fetchProdByCat(req)
            }
        }
    }
    private fun setupRecyclerView() {
        binding.noProductsTxt.visibility = View.GONE
        binding.noProdImg.visibility = View.GONE
        val mAdapter = ProductsByCatAdapter(mutableListOf(), onItemClick = {
//            val action = SearchFragmentDirections.actionSearchFragmentToSearchDetailsFragment(
//
//                it.name!!
//            )
//            findNavController().navigate(action)
        })

        binding.prodByCatRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun observe() {
        observeState()
        observeProducts()
    }

    private fun observeState() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeProducts() {
        viewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleProducts(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleState(state: ProductsByCategoryState) {
        when (state) {
            is ProductsByCategoryState.IsLoading -> handleLoading(state.isLoading)
            is ProductsByCategoryState.ShowToast -> {
               binding.noProductsTxt.visibility = View.VISIBLE
                binding.noProductsTxt.text = state.message
                binding.noProdImg.visibility = View.VISIBLE
            }

            is ProductsByCategoryState.Init -> Unit
            else -> {

            }
        }
    }


    private fun handleProducts(products: List<Product>) {
        binding.prodByCatRV.adapter?.let {
            if (it is ProductsByCatAdapter) {
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