package com.example.mobileapplicationproject.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.FragmentEstabCountHistory;
import com.example.mobileapplicationproject.FragmentEstabDetailsHistory;
import com.example.mobileapplicationproject.FragmentUserEstabHistory;
import com.example.mobileapplicationproject.FragmentUserTravelHistory;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    DataHolder dh = new DataHolder();

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
            if(dh.getType().equals("Establishment"))
            {
                FragmentEstabDetailsHistory fragtrav = new FragmentEstabDetailsHistory();
                return fragtrav;
            }
            else
            {
                FragmentUserTravelHistory fragdetails = new FragmentUserTravelHistory();
                return fragdetails;
            }


        }
        else if(position==1)
        {
            if(dh.getType().equals("Establishment"))
            {
                FragmentEstabCountHistory fragcount = new FragmentEstabCountHistory();
                return fragcount;
            }
            else
            {
                FragmentUserEstabHistory fragestab = new FragmentUserEstabHistory();
                return fragestab;
            }
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