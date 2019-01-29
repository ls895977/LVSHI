package com.lykj.aextreme.afinal.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 自定义主页Pager适配器
 */
public class FragPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public FragPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> fragments) {
        super(supportFragmentManager);
        this.fragments = fragments;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int arg0) {
        return fragments.get(arg0);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

}
