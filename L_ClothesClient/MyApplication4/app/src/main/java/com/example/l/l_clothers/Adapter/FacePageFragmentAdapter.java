package com.example.l.l_clothers.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by L on 2017/6/6.
 */

public class FacePageFragmentAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragmentsList;

    public FacePageFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public FacePageFragmentAdapter(FragmentManager fm, ArrayList<Fragment>fragments)
    {
        super(fm);
        this.fragmentsList=fragments;
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentsList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentsList.size();//也就是说这个list是空的，就是空的这个原因
    }
    @Override
    public int getItemPosition(Object object)
    {
        return super.getItemPosition(object);
    }
}

