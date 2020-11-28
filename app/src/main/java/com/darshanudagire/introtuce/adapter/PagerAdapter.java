package com.darshanudagire.introtuce.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.darshanudagire.introtuce.Enroll;
import com.darshanudagire.introtuce.Users;
import com.google.android.material.tabs.TabLayout;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int noOfTabs;

    public PagerAdapter(FragmentManager fragmentManager,int noOfTabs)
    {
        super(fragmentManager);
        this.noOfTabs = noOfTabs;

    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                Users users = new Users();
                return users;
            case 1:
                Enroll enroll = new Enroll();
                return enroll;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
