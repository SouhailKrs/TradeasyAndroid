package com.tradeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentHomeBinding
import com.tradeasy.utils.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
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
        bestRecycler2 = binding.homepageRVHH
        //vertical layout manager
        newRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newRecycler.setHasFixedSize(true)
        bestRecycler2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestRecycler2.setHasFixedSize(true)
        newArrayList = arrayListOf<Products>()





        getUserData()


        return binding.root
    }

    private fun getUserData() {
        for (i in imageId.indices) {
            val products = Products(title[i],description[i],price[i],imageId[i])
            newArrayList.add(products)
        }
        newRecycler.adapter = ProductsAdapter(newArrayList)
        bestRecycler2.adapter = ProductsSaleAdapter(newArrayList)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        view.rootView?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility  =
            View.VISIBLE
    }

}