package com.tradeasy.ui.profile.bids

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tradeasy.databinding.FragmentBidsBinding
import com.tradeasy.ui.MainActivity

class BidsFragment : Fragment() {
    private lateinit var binding:FragmentBidsBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBidsBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity?)?.setupToolBar("Bids", false, false)
        val fragmentList = arrayListOf(
       CurrentlyBiddingFragment(),
            WonBidsFragment()
        )

        viewPager.adapter = activity?.let { PagerAdapter(it, fragmentList) }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Currently Bidding"
                1 -> tab.text = "Won Bids"
            }
        }.attach()
    }
}