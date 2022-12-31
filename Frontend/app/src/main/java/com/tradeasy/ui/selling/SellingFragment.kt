package com.tradeasy.ui.selling

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tradeasy.R
import com.tradeasy.databinding.FragmentSellingBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.ui.selling.userProducts.UserProductsAdapter
import com.tradeasy.ui.selling.userProducts.UserProductsState
import com.tradeasy.ui.selling.userProducts.UserProductsViewModel
import com.tradeasy.ui.selling.userSelling.SellingFragmentState
import com.tradeasy.ui.selling.userSelling.UserSellingAdapter
import com.tradeasy.ui.selling.userSelling.UserSellingViewModel
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
    private val userProdViewModel: UserProductsViewModel by viewModels()
    private val sharedDataViewModel: SharedDataViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentSellingBinding.inflate(inflater, container, false)
        setupRecyclerView()
        observeUserProd()
        setupUserProdRV()
        observe()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()

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
            is SellingFragmentState.ShowToast -> {}
            is SellingFragmentState.Init -> Unit
            else -> {

            }
        }
    }

    private fun setupRecyclerView() {
        val mAdapter = UserSellingAdapter(mutableListOf(), onItemClick = {
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
            findNavController().navigate(R.id.action_sellingFragment_to_userProductItemFragment)



        }

        )


        binding.userSellingRV.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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


    private fun observeUserProd() {
        observeUserProdState()
        observeUserProducts()
    }

    private fun observeUserProdState() {
        userProdViewModel.mState.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleUserProdState(state)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeUserProducts() {
        userProdViewModel.mProducts.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleUserProd(products)
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleUserProdState(state: UserProductsState) {
        when (state) {
            is UserProductsState.IsLoading -> handleUserProdLoading(state.isLoading)
            is UserProductsState.ShowToast -> {}
            is UserProductsState.Init -> Unit
            else -> {

            }
        }
    }

    private fun setupUserProdRV() {
        val mAdapter = UserProductsAdapter(mutableListOf(), onItemClick = {
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
            findNavController().navigate(R.id.action_sellingFragment_to_userProductItemFragment)

        })


        binding.userProductsRV.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun handleUserProd(products: List<Product>) {
        binding.userProductsRV.adapter?.let {
            if (it is UserProductsAdapter) {
                it.updateList(products)
            }
        }
    }

    private fun handleUserProdLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }
    private fun setupView(){
        sharedPrefs.clearBidDuration()
        sharedPrefs.clearProdCategory()
        (activity as MainActivity?)?.setupToolBar("Selling", false, false)
        if (sharedPrefs.getUser() == null) {

            findNavController().navigate(R.id.loginFragment)

        }
        binding.goToAddProduct.setOnClickListener {
            if(sharedPrefs.getUser()!!.isVerified==true) {
                findNavController().navigate(R.id.addProductFragment)
            }else{
                AlertDialog.Builder(requireContext())
                    .setTitle("Verify your account")
                    .setMessage("Please verify your account so you can start selling items")
                    .setPositiveButton("Verify") { _, _ ->
                        findNavController().navigate(R.id.smsToVerifyFragment)
                    }
                    .setNegativeButton("Later") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }

    }
}