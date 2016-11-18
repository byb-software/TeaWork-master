package com.qf.teawork.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> data;
    private String[] title;

    public MyFragmentPagerAdapter(FragmentManager supportFragmentManager, List<Fragment> data, String[] title) {
        super(supportFragmentManager);
        this.data = data;
        this.title = title;
    }


    @Override
    public Fragment getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    //返回TabLayout的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
