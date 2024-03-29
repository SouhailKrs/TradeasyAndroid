package com.tradeasy.ui.profile.bids

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fa:FragmentActivity,private val fragments:ArrayList<Fragment>) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int =fragments.size

    override fun createFragment(position: Int): Fragment =fragments[position]

}