package com.artamonov.millionplanets.market

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class MarketPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val fragmentTitlesList: MutableList<String> = ArrayList()
    fun setPageTitles(titles: String) {
        fragmentTitlesList.add(titles)
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 1) {
            MarketPlanetFragment.newInstance()
        } else MarketYouFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitlesList[position]
    }

    override fun getCount(): Int {
        return fragmentTitlesList.size
    }
}