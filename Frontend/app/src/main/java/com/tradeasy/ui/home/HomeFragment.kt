package com.tradeasy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tradeasy.R
import com.tradeasy.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var newRecycler: RecyclerView
    private lateinit var bestRecycler2: RecyclerView
    private lateinit var newArrayList: ArrayList<Products>
    private lateinit var binding: FragmentHomeBinding

    private lateinit var imageId: Array<Int>
    private lateinit var title: Array<String>
    private lateinit var description: Array<String>
    private lateinit var price: Array<Int>
    private lateinit var btndown : ImageView
    private lateinit var btnup : ImageView
    private lateinit var categoryLayout1: LinearLayout
    private lateinit var categoryLayout2: LinearLayout
    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        title = arrayOf("Iphone 11","Iphone 12","Iphone 13","Iphone 14")
        description = arrayOf(
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum.",
            "Le lorem ipsum est, en imprimerie, une suite de mots sans signification utilisée à titre provisoire pour calibrer une mise en page, le texte définitif venant remplacer le faux-texte dès qu'il est prêt ou que la mise en page est achevée. Généralement, on utilise un texte en faux latin, le Lorem ipsum ou Lipsum."
        )
        price = arrayOf(1600,1700,1800,1900)

        newRecycler = binding.homepageRV
        bestRecycler2 = binding.homepageRVHH
        //vertical layout manager
        newRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        newRecycler.setHasFixedSize(true)
        bestRecycler2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestRecycler2.setHasFixedSize(true)
        newArrayList = arrayListOf<Products>()

        btndown = binding.dropdownicon
        categoryLayout1 = binding.linearLayout2
        categoryLayout2 = binding.linearLayout5
        btnup = binding.dropupicon



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

        btndown.setOnClickListener {
            categoryLayout1.visibility = View.VISIBLE
            categoryLayout2.visibility = View.VISIBLE
            btndown.visibility = View.GONE
            btnup.visibility = View.VISIBLE
        }
        btnup.setOnClickListener {
            categoryLayout1.visibility = View.GONE
            categoryLayout2.visibility = View.GONE
            btndown.visibility = View.VISIBLE
            btnup.visibility = View.GONE
        }
    }

}
