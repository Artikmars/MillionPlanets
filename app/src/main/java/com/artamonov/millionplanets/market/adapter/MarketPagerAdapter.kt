package com.artamonov.millionplanets.market.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.artamonov.millionplanets.market.MarketPlanetFragment
import com.artamonov.millionplanets.market.MarketYouFragment

class MarketPagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
    private val fragmentTitlesList: MutableList<String> = ArrayList()

    override fun getItemCount(): Int = fragmentTitlesList.size

    fun setPageTitles(title: String) = fragmentTitlesList.add(title)

    fun getPageTitle(position: Int) = fragmentTitlesList[position]

    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            MarketPlanetFragment.newInstance()
        } else {
            MarketYouFragment.newInstance()
        }
    }
}