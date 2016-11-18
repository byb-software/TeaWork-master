package com.qf.teawork.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by my on 2016/11/11.
 */
public class MyPagerAdapter extends PagerAdapter {

    private List<ImageView> data;

    public MyPagerAdapter(List<ImageView> data) {

        this.data = data;
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View ret = data.get(position);

        container.addView(ret);

        return ret;//将用过的视图返回给下面的方法
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView(data.get(position));//这里将之前的视图回收掉
    }
}
