package com.example.aiiage.jetweather.test;


import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public  class PageAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = null;
    private Context context;

    public PageAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        //this.context = context;
        this.fragments = fragments;
    }

    @Override
    public int getCount() {
        return fragments.size();//;NUM_ITEMS;
    }

    @Override
    public Fragment getItem(int position) {
        //return ArrayListFragment.newInstance(position);
        return fragments.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}