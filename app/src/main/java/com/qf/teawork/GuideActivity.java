package com.qf.teawork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.qf.teawork.adapter.MyPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    private Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);


        //隐藏状态栏，显示时间，电池电量，网络状态，通知……
        //状态栏，不属于某一个特定应用，手机的Window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        initView();

        initViewPager();
    }

    private void initViewPager() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        List<ImageView> data = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            switch (i){
                case 0:
                    imageView.setImageResource(R.mipmap.slide1);
                    break;
                case 1:
                    imageView.setImageResource(R.mipmap.slide2);
                    break;
                case 2:
                    imageView.setImageResource(R.mipmap.slide3);
                    break;
            }
            //让图片按照视图来缩放，这里是让他等于视图的大小
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            data.add(imageView);
        }

        PagerAdapter adapter = new MyPagerAdapter(data);

        viewPager.setAdapter(adapter);

        //设置监听让滑动到最后一张图片时透明的按钮出现
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                 if(position == 2){
                     go.setVisibility(View.VISIBLE);
                 }else{
                     go.setVisibility(View.GONE);
                 }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initView() {
        go = (Button) findViewById(R.id.go);
    }


    //点击跳转到主界面
    public void come(View view) {

        //存储记录，用户已经第一次使用了
        SharedPreferences sp = getSharedPreferences("appConfig",MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("isFirst",false);//用户不是第一次使用

        editor.commit();

        Intent intent = new Intent(this,MainActivity.class);
        this.startActivity(intent);
        this.finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
