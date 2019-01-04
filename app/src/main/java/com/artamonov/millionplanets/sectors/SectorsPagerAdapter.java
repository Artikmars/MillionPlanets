package com.artamonov.millionplanets.sectors;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SectorsPagerAdapter extends FragmentPagerAdapter {

    private List<String> fragmentTitlesList = new ArrayList<>();

    public SectorsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public void setPageTitles(String titles) {

        fragmentTitlesList.add(titles);
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SectorsYouFragment.newInstance();
            case 1:
                return SectorsPlanetFragment.newInstance();
            default:
                return SectorsYouFragment.newInstance();
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitlesList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentTitlesList.size();
    }
}
