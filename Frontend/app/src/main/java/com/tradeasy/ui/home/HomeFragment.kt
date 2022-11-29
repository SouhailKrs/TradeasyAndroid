package com.tradeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentHomeBinding
import com.tradeasy.domain.model.Product
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var newRecycler: RecyclerView
    private lateinit var bestRecycler2: RecyclerView
    private lateinit var newArrayList: ArrayList<Products>
    private lateinit var binding: FragmentHomeBinding
    lateinit var imageId: Array<Int>
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


        println("token " + sharedPrefs.getToken())
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val toolbar: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbar_title)
        toolbar.text = "Tradeasy"
        val toolbarTxt: TextView = requireActivity().findViewById(com.tradeasy.R.id.toolbarRightText)
        toolbarTxt.visibility = View.GONE
        imageId = arrayOf(R.drawable.iphone,R.drawable.iphone,R.drawable.iphone,R.drawable.iphone,R.drawable.iphone,R.drawable.iphone)
        title = arrayOf("Iphone 6","Iphone 8","Iphone X","Iphone 11","Iphone 12","Iphone 13","Iphone 14")
        description = arrayOf(
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",

            )
        price = arrayOf(600,800,1600,1700,1800,1900)

        newRecycler = binding.homepageRV
        //vertical layout manager
        newRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newRecycler.setHasFixedSize(true)

        newArrayList = arrayListOf<Products>()
        setupRecyclerView()

        println("token home: " + sharedPrefs.getToken())


        observe()

        getUserData()
        setFragmentResultListener("success_create"){ requestKey, bundle ->
            if(bundle.getBoolean("success_create")){
                viewModel.fetchProductsForBid()
            }
        }

        return binding.root
    }

    private fun getUserData() {
        for (i in imageId.indices) {
            val products = Products(title[i],description[i],price[i],imageId[i])
            newArrayList.add(products)
        }
        newRecycler.adapter = ProductsAdapter(newArrayList)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        view.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility  =
            View.VISIBLE
    }
    private fun observe(){
        observeState()
        observeProducts()
    }
    private fun observeState(){
        viewModel.mState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle,  Lifecycle.State.STARTED)
            .onEach { state ->
                handleState(state)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
    private fun observeProducts(){
        viewModel.mProducts
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { products ->
                handleProducts(products)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }


    private fun handleState(state: HomeFragmentState){
        when(state){
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

    private fun setupRecyclerView(){
        val mAdapter = ProductsForBid(mutableListOf(), onItemClick = {
            val action = HomeFragmentDirections.actionHomeFragmentToBidFragment(it.productId!!,
                it.bidEndDate!!.toString(), it.price!!, it.forBid!!
            )

            findNavController().navigate(action)

        })


        binding.homepageRVHH.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
    private fun handleLoading(isLoading: Boolean){
//        if(isLoading){
//            binding.loadingProgressBar.visible()
//        }else{
//            binding.loadingProgressBar.gone()
//        }
    }
    private fun handleProducts(products: List<Product>){
        binding.homepageRVHH.adapter?.let {
            if(it is ProductsForBid){
                it.updateList(products)
            }
        }
    }
}