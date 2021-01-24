package com.example.mobileapplicationproject.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mobileapplicationproject.DataController.DataHolder;
import com.example.mobileapplicationproject.FragmentPasswordUDE;
import com.example.mobileapplicationproject.FragmentProfileEstab;
import com.example.mobileapplicationproject.FragmentProfileUD;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SPASecond extends FragmentPagerAdapter {

    DataHolder dh = new DataHolder();

    public static String[] TAB_TITLES = new String[]{};
    private final Context mContext;

    public SPASecond(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        TAB_TITLES = new String[]{"Profile", "Account"};
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        if(position==0)
        {
            if (dh.getType().equals("Establishment"))
            {
                FragmentProfileEstab fragestprof = new FragmentProfileEstab();
                return fragestprof;
            }
            else
            {
                FragmentProfileUD fragprof = new FragmentProfileUD();
                return fragprof;
            }
        }
        else if(position==1)
        {

            FragmentPasswordUDE fragpass = new FragmentPasswordUDE();
            return fragpass;

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