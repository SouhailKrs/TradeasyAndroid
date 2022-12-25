package com.tradeasy.ui.selling.product.categories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.databinding.FragmentCategoriesBinding
import com.tradeasy.domain.category.entity.Category
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

private lateinit var binding:  FragmentCategoriesBinding
    private val viewModel: CategoriesViewModel by viewModels()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
      binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe()



    }
    private fun observe() {
        observeState()
        observeCategories()
    }
    private fun observeState() {
        viewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeCategories() {
        viewModel.mCategories.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { categories ->
                handleCategories(categories)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun handleState(state: CategoriesFragmentState) {
        when (state) {
            is CategoriesFragmentState.IsLoading -> handleLoading(state.isLoading)
            is CategoriesFragmentState.ShowToast -> {Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()

                println(state.message)
            }
            is CategoriesFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupRecyclerView() {

        val mAdapter = CategoriesAdapter(mutableListOf(), onItemClick = {


            sharedPrefs.setProdCategory(it.name!!)
            println("éééé ${it.name}")
            println("aaaa " + sharedPrefs.getProdCategory())
            findNavController().navigateUp()
        })


        binding.categoriesRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun handleCategories(categories: List<Category>) {
        println("list2 $categories" )
        binding.categoriesRV.adapter?.let {
            if (it is CategoriesAdapter) {
                it.updateList(categories)
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
// a function that takes a string and returns a string

}