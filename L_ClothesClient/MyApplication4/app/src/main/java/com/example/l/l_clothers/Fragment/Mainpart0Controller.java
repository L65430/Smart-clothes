package com.example.l.l_clothers.Fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;

import java.util.ArrayList;

/**
 * Created by L on 2017/6/5.
 */

public class Mainpart0Controller {
    private int containerId;
    private FragmentManager fm;
    private ArrayList<Fragment> fragments;
    private static Mainpart0Controller controller;

    public static Mainpart0Controller getInstance(Fragment parentFragment,int containerId){
        if(controller==null){
            controller=new Mainpart0Controller(parentFragment,containerId);
        }
        return controller;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private Mainpart0Controller(Fragment fragment, int containerId){
        this.containerId=containerId;
        fm=fragment.getChildFragmentManager();
        initFragment();
    }

    private void initFragment(){
        fragments=new ArrayList<Fragment>();
        fragments.add(new MainPart0talk());
        fragments.add(new MainPart0friend());
        FragmentTransaction ft=fm.beginTransaction();
        for(Fragment fragment:fragments){
            ft.add(containerId,fragment);
        }
        ft.commit();
    }

    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction ft = fm.beginTransaction();
        ft.show(fragment);
        ft.commit();
    }

    public void hideFragments() {
        FragmentTransaction ft = fm.beginTransaction();
        for(Fragment fragment : fragments) {
            if(fragment != null) {
                ft.hide(fragment);
            }
        }
        ft.commit();
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

}
