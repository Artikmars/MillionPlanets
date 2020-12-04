package com.artamonov.millionplanets.market

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.artamonov.millionplanets.R
import com.artamonov.millionplanets.base.BaseActivityWithLayout
import kotlinx.android.synthetic.main.market_activity.*

class MarketActivity : BaseActivityWithLayout(R.layout.market_activity), MarketYouAdapter.DialogListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
// Create the adapter that will return a fragment for each of the three
// primary sections of the activity.
        /**
         * The [PagerAdapter] that will provide fragments for each of the sections. We use a
         * [FragmentPagerAdapter] derivative, which will keep every loaded fragment in memory. If
         * this becomes too memory intensive, it may be best to switch to a
         * androidx.fragment.app.FragmentStatePagerAdapter.
         */
        val mSectionsPagerAdapter = MarketPagerAdapter(supportFragmentManager)
        mSectionsPagerAdapter.setPageTitles(resources.getString(R.string.first_tab))
        mSectionsPagerAdapter.setPageTitles(resources.getString(R.string.second_tab))
        // Set up the ViewPager with the sections adapter.
        /** The [ViewPager] that will host the section contents.  */
        container.adapter = mSectionsPagerAdapter
        tabs.setupWithViewPager(container)
        // mViewPager.addOnPageChangeListener(new
// TabLayout.TabLayoutOnPageChangeListener(tabLayout));
// tabLayout.addOnTabSelectedListener(new
// TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    override fun onDialogCreate(position: Int) { /*  MarketYouDialog marketYouDialog = new MarketYouDialog();
        marketYouDialog.show(getSupportFragmentManager(), "text");*/
    }
}