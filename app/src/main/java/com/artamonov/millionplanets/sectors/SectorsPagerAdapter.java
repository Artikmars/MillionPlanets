package com.artamonov.millionplanets.sectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

public class SectorsPagerAdapter extends FragmentPagerAdapter {

    private final List<String> fragmentTitlesList = new ArrayList<>();

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
