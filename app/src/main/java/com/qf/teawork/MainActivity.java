package com.qf.teawork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qf.teawork.adapter.MyFragmentPagerAdapter;
import com.qf.teawork.fragment.MyFragment;
import com.softpo.viewpagertransformer.RotateDownTransformer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    //ViewPager页面
    private String[] Title = new String[]{"头条","百科","资讯","经营","数据"};
    private List<Fragment> data = new ArrayList<>();


    //对侧边栏的按钮进行设置
    private EditText mExitText;
    private ImageView search,callback;
    private TextView myshoucang,lishi,banquan,yijian;



    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initView();

        initViewPager();
    }

    private void initViewPager() {

        for (int i = 0; i < 5; i++) {
            Fragment fragment = new MyFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("key",i);
            fragment.setArguments(bundle);
            data.add(fragment);
        }

        //实例化适配器
        FragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),data,Title);


        mViewPager.setAdapter(adapter);

        //设置ViewPager的翻页动画
        mViewPager.setPageTransformer(false,new RotateDownTransformer());
        //让TabLayout跟随ViewPager滑动
       mTabLayout.setupWithViewPager(mViewPager,true);
    }

    private void initView() {
        //让相应的点击事件执行mDrawerLayout.openDrawer(Gravity.RIGHT);
        //或者去执行mDrawerLayout.closeDrawer(Gravity.RIGHT);关闭
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);


        //对侧边栏的实例化
        mExitText = (EditText) findViewById(R.id.editText);
        search = (ImageView) findViewById(R.id.search);
        callback = (ImageView) findViewById(R.id.callback);
        myshoucang = (TextView) findViewById(R.id.myshoucang);
        lishi = (TextView) findViewById(R.id.lishi);
        banquan = (TextView) findViewById(R.id.banquan);
        yijian = (TextView) findViewById(R.id.yijian);


    }


    //按钮的点击事件，包括侧边栏里面
    public void click(View view) {

        switch (view.getId()){
            case R.id.drawerButton://开启侧边栏
                mDrawerLayout.openDrawer(Gravity.RIGHT);
                break;
            case R.id.search://点击搜索按钮

                String ss = mExitText.getText().toString();
                Log.d("flag", "---------->mExitText: " +ss);
                Intent intentss = new Intent(this,SearchActivity.class);

                intentss.putExtra("key",ss);

                this.startActivity(intentss);

                break;
            case R.id.myshoucang://点击看收藏
                Intent intent = new Intent(this,CollectActivity.class);
                this.startActivity(intent);
                break;
            case R.id.lishi://点击看历史
                Intent intent1 = new Intent(this,LishiActivity.class);
                this.startActivity(intent1);
                break;
            case R.id.banquan://点击看版权信息
                Intent intentbq = new Intent(this,BanquanActivity.class);
                this.startActivity(intentbq);
                break;
            case R.id.yijian://点击反馈意见
                Intent intentyj = new Intent(this,FankuiActivity.class);
                this.startActivity(intentyj);
                break;
            case R.id.callback:
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                break;
        }
    }


    //按两次返回键退出,2秒钟之内再次按下，否则就需要重新按两次
    private boolean exitCheck = false;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            exitCheck = false;
        }
    };
    @Override
    public void onBackPressed() {

        if(exitCheck){
            super.onBackPressed();
        }else{
            exitCheck = true;
            Toast.makeText(this,"再按一下返回键退出",Toast.LENGTH_SHORT).show();
            mHandler.sendEmptyMessageDelayed(0,2000);
        }


    }
}
