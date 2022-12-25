package com.tradeasy.ui.selling

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.R
import com.tradeasy.databinding.FragmentSellingBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.home.SellingFragmentState
import com.tradeasy.ui.home.UserSellingViewModel
import com.tradeasy.ui.selling.userSelling.UserSellingAdapter
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class SellingFragment : Fragment() {
    private lateinit var binding: FragmentSellingBinding

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: UserSellingViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentSellingBinding.inflate(inflater, container, false)
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
        if(sharedPrefs.getUser() == null ){

            findNavController().navigate(R.id.loginFragment)


        }
        binding.goToAddProduct.setOnClickListener {
            findNavController().navigate(R.id.addProductFragment)
        }
        setupRecyclerView()

        observe()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

sharedPrefs.clearBidDuration()
        sharedPrefs.clearProdCategory()


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
    private fun handleState(state: SellingFragmentState) {
        when (state) {
            is SellingFragmentState.IsLoading -> handleLoading(state.isLoading)
            is SellingFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is SellingFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setupRecyclerView() {
        val mAdapter = UserSellingAdapter(mutableListOf(), onItemClick = {
            val imagesArray = Array(it.image!!.size) { i -> it.image[i] }
            val action = SellingFragmentDirections.actionSellingFragmentToProductItemFragment(

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


        binding.userSellingRV.apply {
            adapter = mAdapter
            layoutManager =
                GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun handleProducts(products: List<Product>) {
        binding.userSellingRV.adapter?.let {
            if (it is UserSellingAdapter) {
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