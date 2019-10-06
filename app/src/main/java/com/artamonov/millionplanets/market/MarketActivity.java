package com.artamonov.millionplanets.market;

import android.os.Bundle;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.artamonov.millionplanets.R;
import com.artamonov.millionplanets.base.BaseActivity;
import com.google.android.material.tabs.TabLayout;

public class MarketActivity extends BaseActivity implements MarketYouAdapter.DialogListener {

    /**
     * The {@link PagerAdapter} that will provide fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory. If
     * this becomes too memory intensive, it may be best to switch to a
     * androidx.fragment.app.FragmentStatePagerAdapter.
     */
    private MarketPagerAdapter mSectionsPagerAdapter;

    /** The {@link ViewPager} that will host the section contents. */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market);

        /* Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new MarketPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.setPageTitles(getResources().getString(R.string.first_tab));
        mSectionsPagerAdapter.setPageTitles(getResources().getString(R.string.second_tab));
        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
        // mViewPager.addOnPageChangeListener(new
        // TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // tabLayout.addOnTabSelectedListener(new
        // TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }

    @Override
    public void onDialogCreate() {
        /*  MarketYouDialog marketYouDialog = new MarketYouDialog();
        marketYouDialog.show(getSupportFragmentManager(), "text");*/
    }
}
