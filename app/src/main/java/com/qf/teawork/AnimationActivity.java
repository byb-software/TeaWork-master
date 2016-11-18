package com.qf.teawork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationActivity extends AppCompatActivity {

    private ImageView anmia;
    private TextView firstTitle;
    private int TIME = 4;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 110://设置5秒的倒计时
                   if(TIME == 0){
                        firstTitle.setText("0秒后自动进入");
                        TIME = 4;

                       //跳转到导航界面

                       //取出sp中数据
                       SharedPreferences sp = getSharedPreferences("appConfig",MODE_PRIVATE);

                       boolean isFirst = sp.getBoolean("isFirst",true);

                       if(isFirst){
                           //Ctrl+Shift+上下键
                           AnimationActivity.this
                                   .startActivity(new Intent(AnimationActivity.this,GuideActivity.class));
                       }else {//不是第一次，用户已经导航过
                           AnimationActivity.this
                                   .startActivity(new Intent(AnimationActivity.this,MainActivity.class));
                       }
                       AnimationActivity.this.finish();//当前的Activity消亡

                }else{
                        firstTitle.setText(TIME+"秒后自动进入");
                        --TIME;
                        mHandler.sendEmptyMessageDelayed(110,1000);
                    }

            }
        }


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        //隐藏状态栏，显示时间，电池电量，网络状态，通知……
        //状态栏，不属于某一个特定应用，手机的Window
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        initView();



        //隐藏掉此页面的ActionBar，这里选择在清单文件中实现
      //  getActionBar().hide();

        ScaleAnimation scaleAnimation = new ScaleAnimation(0.9f, 1f, 0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        scaleAnimation.setDuration(3000);

        scaleAnimation.setFillAfter(true);

        //这里将跳转的实现给予倒计时来实现
        mHandler.sendEmptyMessageDelayed(110,1000);
        firstTitle.setText("5秒后自动进入");

          //将跳转设置到倒计时上面
        anmia.startAnimation(scaleAnimation);

    }

    private void initView() {
        anmia = (ImageView) findViewById(R.id.anima);
        firstTitle = (TextView) findViewById(R.id.fistTitle);

    }


    //这里是为了解决动画没有执行完毕时就退出软件还是会过一会自动进入软件的bug
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(110);

    }
}
