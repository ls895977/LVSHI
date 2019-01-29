package com.lykj.aextreme.afinal.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.lykj.aextreme.afinal.utils.Debug;

import java.util.List;

/**
 * List<View>类型
 */
public class ViewPagerAdapter extends PagerAdapter {

    // 界面列表
    private List<View> views;

    public ViewPagerAdapter(List<View> views) {
        this.views = views;
    }

    // 获得当前界面数
    @Override
    public int getCount() {
        return views == null ? 0 : views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Debug.e(getClass(), " position:" + position);
        // container.removeAllViews();
        container.addView(views.get(position));
        return views.get(position);
    }

    // 销毁arg1位置的界面
    @Override
    public void destroyItem(View v, int position, Object obj) {
        ((ViewPager) v).removeView(views.get(position));
    }

    // 判断是否由对象生成界面
    @Override
    public boolean isViewFromObject(View v, Object obj) {
        return (v == obj);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(ViewGroup container) {

    }

    @Override
    public void finishUpdate(View arg0) {

    }

    public void refresh(List<View> views) {
        this.views = views;
        this.notifyDataSetChanged();
    }

}
