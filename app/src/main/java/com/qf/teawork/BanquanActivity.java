package com.qf.teawork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BanquanActivity extends AppCompatActivity {

    private ImageView callback,home,button;
    private TextView context,more;
    private boolean isButton = false;//判断是收起状态还是下拉状态，默认为收起状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banquan);

        initView();
    }

    private void initView() {
        callback = (ImageView) findViewById(R.id.callback);
        home = (ImageView) findViewById(R.id.home);
        button = (ImageView) findViewById(R.id.button);
        context = (TextView) findViewById(R.id.context);
        more = (TextView) findViewById(R.id.more);
    }

    public void click(View view) {
        switch (view.getId()){
            case R.id.home:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);

                break;
            case R.id.callback:
                this.finish();
                break;
            case R.id.button:
                if(isButton){

                    context.setMaxLines(2);
                    button.setImageResource(R.mipmap.lv_backtobottom);
                    more.setText("更多");
                    isButton = false;

                }else{
                    context.setMaxLines(5);
                   button.setImageResource(R.mipmap.lv_backtotop);
                    more.setText("收起");
                    isButton = true;
                }

                break;
        }
    }
}
