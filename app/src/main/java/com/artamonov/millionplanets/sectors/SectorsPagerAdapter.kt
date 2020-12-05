package com.artamonov.millionplanets.sectors

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.ArrayList

class SectorsPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val fragmentTitlesList: MutableList<String> = ArrayList()

    override fun getItemCount(): Int = fragmentTitlesList.size

    fun setPageTitles(title: String) = fragmentTitlesList.add(title)

    fun getPageTitle(position: Int) = fragmentTitlesList[position]

    override fun createFragment(position: Int): Fragment {
        return if (position == 1) {
            SectorsPlanetFragment.newInstance()
        } else {
            SectorsYouFragment.newInstance()
        }
    }
}