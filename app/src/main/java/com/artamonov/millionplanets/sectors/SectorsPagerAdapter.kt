package com.artamonov.millionplanets.sectors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class SectorsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val fragmentTitlesList: MutableList<String> = ArrayList()
    fun setPageTitles(titles: String) {
        fragmentTitlesList.add(titles)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> SectorsPlanetFragment.newInstance()
            else -> SectorsYouFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitlesList[position]
    }

    override fun getCount(): Int {
        return fragmentTitlesList.size
    }
}