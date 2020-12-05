package com.artamonov.millionplanets.sectors

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.databinding.ActivitySectorsBinding
import com.google.android.material.tabs.TabLayoutMediator

class SectorsActivity : FragmentActivity() {
    /**
     * The [PagerAdapter] that will provide fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every loaded fragment in memory. If
     * this becomes too memory intensive, it may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */

    lateinit var binding: ActivitySectorsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySectorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        val mSectionsPagerAdapter = SectorsPagerAdapter(this)
        mSectionsPagerAdapter.setPageTitles(resources.getString(R.string.first_tab))
        mSectionsPagerAdapter.setPageTitles(resources.getString(R.string.second_tab))

        binding.sectorsViewPager2.adapter = mSectionsPagerAdapter
        TabLayoutMediator(binding.sectorsTabLayout, binding.sectorsViewPager2) { tab, position ->
            tab.text = mSectionsPagerAdapter.getPageTitle(position)
        }.attach()
    }
}