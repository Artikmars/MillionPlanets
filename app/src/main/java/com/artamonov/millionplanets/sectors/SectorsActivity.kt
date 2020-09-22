package com.artamonov.millionplanets.sectors

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivity
import com.google.android.material.tabs.TabLayout

class SectorsActivity : BaseActivity(R.layout.sectors) {
    /**
     * The [PagerAdapter] that will provide fragments for each of the sections. We use a
     * [FragmentPagerAdapter] derivative, which will keep every loaded fragment in memory. If
     * this becomes too memory intensive, it may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private var mSectionsPagerAdapter: SectorsPagerAdapter? = null

    /** The [ViewPager] that will host the section contents.  */
    private var mViewPager: ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sectors)

        //        Toolbar toolbar = findViewById(R.id.toolbar);
        //        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectorsPagerAdapter(supportFragmentManager)
        mSectionsPagerAdapter?.setPageTitles(resources.getString(R.string.first_tab))
        mSectionsPagerAdapter?.setPageTitles(resources.getString(R.string.second_tab))
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.sectors_container)
        mViewPager?.adapter = mSectionsPagerAdapter
        val tabLayout = findViewById<TabLayout>(R.id.sectors_tabs)
        tabLayout.setupWithViewPager(mViewPager)
        // mViewPager.addOnPageChangeListener(new
        // TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tabLayout.addOnTabSelectedListener(new
        // TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    fun onSectionsAction(view: View?) {}
}