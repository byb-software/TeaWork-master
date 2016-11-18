package com.qf.teawork.fragment.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by my on 2016/11/14.
 */
public class MyViewPagerAdapter extends PagerAdapter {

    private List<ImageView> imgData;

    public MyViewPagerAdapter(List<ImageView> imgData) {
        this.imgData = imgData;
    }

    @Override
    public int getCount() {
//        Log.d("flag", "---------->getCount: size" +imgData.size());
     return imgData!=null?imgData.size():0;//写成无限循环
      //  return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {


           View v=imgData.get(position);
          ViewGroup parent = (ViewGroup) v.getParent();
        //Log.i("ViewPaperAdapter", parent.toString());
        if (parent != null) {
            parent.removeAllViews();
        }
        container.addView(imgData.get(position));
        return imgData.get(position);


       /* container.addView(imgData.get(position));

        return imgData.get(position);*/
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);

        container.removeView(imgData.get(position));
    }
}
