package com.qf.teawork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class FankuiActivity extends AppCompatActivity {

    private ImageView callback,home,submit;
    private EditText biaotiText,contextText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fankui);

        initView();
    }

    private void initView() {
        callback = (ImageView) findViewById(R.id.callback);
        home = (ImageView) findViewById(R.id.home);
        submit = (ImageView) findViewById(R.id.submit);
        biaotiText = (EditText) findViewById(R.id.biaotiText);
        contextText = (EditText) findViewById(R.id.contextText);
    }

    public void click(View view) {
        switch (view.getId()) {
            case R.id.home:
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                this.startActivity(intent);

                break;
            case R.id.callback:
                this.finish();
                break;
            case R.id.submit:
                String title = biaotiText.getText().toString();
                String context = contextText.getText().toString();
                Toast.makeText(this,"标题："+title+"   内容"+context,Toast.LENGTH_SHORT).show();
                break;
        }

    }
}
