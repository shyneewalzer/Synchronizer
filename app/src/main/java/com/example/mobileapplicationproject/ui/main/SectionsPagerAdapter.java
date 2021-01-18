package com.example.mobileapplicationproject.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mobileapplicationproject.FragmentEstabHistory;
import com.example.mobileapplicationproject.FragmentTravelHistory;
import com.example.mobileapplicationproject.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {


    public static String[] TAB_TITLES = new String[]{};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm, String ipttitle1, String ipttitle2) {
        super(fm);
        mContext = context;
        TAB_TITLES = new String[]{ipttitle1, ipttitle2};
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        if(position==0)
        {
            FragmentTravelHistory fragtrav = new FragmentTravelHistory();
            return fragtrav;
        }
        else if(position==1)
        {
            FragmentEstabHistory fragestab = new FragmentEstabHistory();
            return fragestab;
        }
        else
        {
            return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
//        return mContext.getResources().getString(TAB_TITLES[position]);
        return TAB_TITLES[position];
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}