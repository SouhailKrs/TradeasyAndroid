package com.tradeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
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
import com.tradeasy.ui.home.forBid.ProductsForBid
import com.tradeasy.utils.SharedPrefs
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
    lateinit var categoryLayout1: LinearLayout
    lateinit var categoryLayout2: LinearLayout
    private val viewModel: HomeViewModel by viewModels()
    @Inject
    lateinit var sharedPrefs: SharedPrefs
    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        fetchProductsForBid()
        setupRecyclerView()
        goToProductByCat ()
        observe()



        return binding.root
    }
private fun fetchProductsForBid() {
    setFragmentResultListener("success_create") { _, bundle ->
        if (bundle.getBoolean("success_create")) {
            viewModel.fetchProductsForBid()
        }
    }
    }
    override fun onResume() {
        super.onResume()
       // (activity as MainActivity?)?.setupToolBar("Home", false, false)
    }
//    private fun getUserData() {
//        for (i in imageId.indices) {
//            val products = Products(title[i],description[i],price[i],imageId[i])
//            newArrayList.add(products)
//        }
//        newRecycler.adapter = ProductsAdapter(newArrayList)
//
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       // (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as MainActivity?)?.setupToolBar("Home", false, )
        view.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility =
            View.VISIBLE
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


    private fun handleState(state: HomeFragmentState) {
        when (state) {
            is HomeFragmentState.IsLoading -> handleLoading(state.isLoading)
            is HomeFragmentState.ShowToast -> Toast.makeText(
                requireActivity(), state.message, Toast.LENGTH_SHORT
            ).show()
            is HomeFragmentState.Init -> Unit
            else -> {
                Toast.makeText(requireActivity(), "Unknown State", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {



        val mAdapter = ProductsForBid(mutableListOf(), onItemClick = {
           // declare a mutable list of string
          // array of string

            val imagesArray = Array(it.image!!.size) { i -> it.image[i] }



            val action = HomeFragmentDirections.actionHomeFragmentToProductItemFragment(

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


        binding.itemsForBidRV.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun handleProducts(products: List<Product>) {
        binding.itemsForBidRV.adapter?.let {
            if (it is ProductsForBid) {
                it.updateList(products)
            }
        }
    }

    fun handleLoading(isLoading: Boolean) {
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }


    private fun goToProductByCat (){
        binding.carsImageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("cars")
            findNavController().navigate(action)
        }
        binding.electronicsImageBotton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("electronics")
            findNavController().navigate(action)
        }
        binding.clothingImageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("clothing")
            findNavController().navigate(action)
        }
        binding.realEstateImageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("real estate")
            findNavController().navigate(action)
        }
        binding.groceriesImageButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsByCategoryFragment("groceries")
            findNavController().navigate(action)
        }




    }
}

