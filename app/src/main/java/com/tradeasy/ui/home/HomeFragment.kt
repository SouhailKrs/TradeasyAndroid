package com.tradeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentHomeBinding
import com.tradeasy.domain.product.entity.Product
import com.tradeasy.ui.MainActivity
import com.tradeasy.ui.SharedDataViewModel
import com.tradeasy.ui.home.forBid.ProductsForBid
import com.tradeasy.ui.home.recentlyAdded.RecentlyAddedAdapter
import com.tradeasy.ui.home.recentlyAdded.RecentlyAddedState
import com.tradeasy.ui.home.recentlyAdded.RecentlyAddedViewModel
import com.tradeasy.utils.SharedPrefs
import com.tradeasy.utils.isWifiConnected
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var title: Array<String>
    lateinit var description: Array<String>
    lateinit var price: Array<Int>
    private val forBidViewModel: ForBidViewModel by activityViewModels()
    private val recentlyAddedVM: RecentlyAddedViewModel by activityViewModels()
    private val sharedViewModel: SharedDataViewModel by activityViewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchProductsForBid()
fetchRecentlyAdded()
    }
    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }


    // stop the view model from fetching data each time the fragment is recreated


    private fun fetchProductsForBid() {
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {
                forBidViewModel.fetchProductsForBid()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as MainActivity?)?.setupToolBar("Home", false)

        setupView()


        setupForBidRecyclerView()
        goToProductByCat()
        observeForBid()
        fetchRecentlyAdded()
        observeRecentlyAdded()
        setupRecentlyAddedRecyclerView()

        view.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
    }

    private fun observeForBid() {
        observeForBidState()
        observeProductsForBid()
    }

    private fun observeForBidState() {
        forBidViewModel.mState.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { state ->
            handleForBidState(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeProductsForBid() {

        forBidViewModel.products.observe(viewLifecycleOwner) { products ->
            handleProductsForBid(products)
        }
    }


    private fun handleForBidState(state: ForBidState) {
        when (state) {
            is ForBidState.IsLoading -> handleForBidLoading(state.isLoading)
            is ForBidState.ShowToast ->{}
            is ForBidState.Init -> Unit
            else -> {

            }
        }
    }

    private fun setupView() {

        if (isWifiConnected(requireContext())) {
            binding.noInternetConstraint.visibility = View.GONE
            binding.contentConstraint.visibility = View.VISIBLE

        } else {
            binding.noInternetConstraint.visibility = View.VISIBLE
            binding.contentConstraint.visibility = View.GONE
        }
        binding.tapToRetryBtn.setOnClickListener {
         findNavController().popBackStack(R.id.homeFragment, true)
            findNavController().navigate(R.id.homeFragment)
        }
        binding.ShowAllImageButton.setOnClickListener {
            findNavController().navigate(R.id.categoriesFragment)
        }

    }

    private fun setupForBidRecyclerView() {


        val mAdapter = ProductsForBid(mutableListOf(), onItemClick = {
            // declare a mutable list of string
            // array of string

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

            findNavController().navigate(R.id.action_homeFragment_to_productItemFragment)

        })

        binding.itemsForBidRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun handleProductsForBid(products: List<Product>) {
        binding.itemsForBidRV.adapter?.let {
            if (it is ProductsForBid) {
                it.updateList(products)
            }
        }
    }

    private fun handleForBidLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }


    private fun goToProductByCat() {
        binding.carsImageButton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("motors")
            findNavController().navigate(action)
        }
        binding.electronicsImageBotton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("electronics")
            findNavController().navigate(action)
        }
        binding.clothingImageButton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("clothing")
            findNavController().navigate(action)
        }
        binding.realEstateImageButton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("real estate")
            findNavController().navigate(action)
        }
        binding.furnitureImageButton.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("furniture")
            findNavController().navigate(action)
        }


    }


    private fun observeRecentlyAdded() {
        observeRecentlyAddedState()
        observeRecentlyAddedProducts()
    }

    private fun observeRecentlyAddedState() {
        recentlyAddedVM.mState.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED
        ).onEach { state ->
            handleRecentlyAddedState(state)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeRecentlyAddedProducts() {

        recentlyAddedVM.products.observe(viewLifecycleOwner) { products ->
            handleRecentlyAddedProducts(products)
        }
    }


    private fun handleRecentlyAddedState(state: RecentlyAddedState) {
        when (state) {
            is RecentlyAddedState.IsLoading -> handleRecentlyAddedLoading(state.isLoading)
            is RecentlyAddedState.ShowToast -> {

            }
            is RecentlyAddedState.Init -> Unit
            else -> {

            }
        }
    }

    private fun setupRecentlyAddedRecyclerView() {
        val mAdapter = RecentlyAddedAdapter(mutableListOf(), onItemClick = {
            // declare a mutable list of string
            // array of string

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

            findNavController().navigate(R.id.action_homeFragment_to_productItemFragment)

        })
        binding.recentlyAddedRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun handleRecentlyAddedProducts(products: List<Product>) {
        binding.recentlyAddedRV.adapter?.let {
            if (it is RecentlyAddedAdapter) {
                it.updateList(products)
            }
        }
    }

    private fun handleRecentlyAddedLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }

    private fun fetchRecentlyAdded() {
        setFragmentResultListener("success_create") { _, bundle ->
            if (bundle.getBoolean("success_create")) {
                recentlyAddedVM.fetchRecentlyAdded()
            }
        }
    }
}

