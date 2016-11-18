package com.qf.teawork;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.qf.teawork.entyties.Content;
import com.qf.teawork.utils.HttpUtils;
import com.qf.teawork.utils.MySQLite;
import com.qf.teawork.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.qf.teawork.utils.Uri.CONTENT_URL;

public class ContextActivity extends AppCompatActivity {

    private String path = null;

    private TextView content_title,content_time,content_laiyuan;

    private ImageView callback,share,collect;

    private WebView webView;

    private Content mContent;

    private LinearLayout button;


    //声明数据库的使用
    private MySQLite dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;



    //通过handler来将网络上获取的数据解析并绑定到控件上面
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    byte[] bytes = (byte[]) msg.obj;
                 //   Log.d("flag", "---------->handleMessage: 第二个页面的handler取到的数据" +bytes.toString());

                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                     //   Log.d("flag", "---------->jsonObject: " +jsonObject);
                        JSONObject object = jsonObject.optJSONObject("data");
                     //   Log.d("flag", "---------->object: " +object);
                        mContent =  JSON.parseObject(object.toString(), Content.class);

                      //  Log.d("flag", "---------->handleMessage: " +content.toString());

                        content_title.setText(mContent.getTitle());
                        content_time.setText(mContent.getCreate_time());
                        content_laiyuan.setText(mContent.getSource());

                        webView.loadDataWithBaseURL(null, mContent.getWap_content(), "text/html", "utf-8", null);

                     SeaveData();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

    //当点击这个页面就说明可以被作为历史记录来进行添加

    private void SeaveData() {

        //将这条数据添加到数据库中

        //数据库不允许插入空数据
        //如果想要插入空数据，需要对参数二指定一个字段
        //参数给null就可以了

        //可以将数据放到values
        ContentValues values = new ContentValues();
        cursor = db.query("lishi", new String[]{"_id", "title", "laiyuan","name","time","id"},
                "id=?", new String[]{mContent.getId()}, null, null, null);
        if(!cursor.moveToNext()){
            //将浏览过的历史存储到数据库
            values.put("title", mContent.getTitle());
            values.put("laiyuan", mContent.getSource());
            values.put("name", mContent.getAuthor());
            values.put("time", mContent.getCreate_time());
            values.put("id", mContent.getId());
            //存到历史的表单中
            db.insert("lishi", null, values);
        }

    }


    private LinearLayout mLinearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_context);

        Intent intent = getIntent();

        initView();

        //当没有网络的时候显示没有数据的提示
        if(NetworkUtils.isConnected(this)){

            //这里的dbHelper代表的就是确定的数据库的工具类
            dbHelper = new MySQLite(this);

            db = dbHelper.getReadableDatabase();


            if(intent.getStringExtra("activity").equals("shoucang")){
                initData(intent.getStringExtra("id"));
                button.setVisibility(View.GONE);
            }else if(intent.getStringExtra("activity").equals("lishi")){
                initData(intent.getStringExtra("id"));
                button.setVisibility(View.VISIBLE);

            }else if(intent.getStringExtra("activity").equals("context")){
                initData(intent.getStringExtra("id"));
                button.setVisibility(View.VISIBLE);

            }else if(intent.getStringExtra("activity").equals("search")) {
                initData(intent.getStringExtra("id"));
                button.setVisibility(View.VISIBLE);


            }
        }else{

            TextView mTextView = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout
                    .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER;
            params.weight = 3;

            mTextView.setGravity(Gravity.CENTER);
            mTextView.setText("没有网络数据");
            mTextView.setTextSize(20);
            mTextView.setLayoutParams(params);
            mLinearLayout.addView(mTextView);
        }




    }




    private void initData(final String mId) {
        new Thread(new Runnable() {
            @Override
            public void run() {

               byte[] bytes = HttpUtils.loadByte(CONTENT_URL +mId);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(new String(bytes));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            //    Log.d("flag", "---------->jsonObject1: " +jsonObject);
            //    Log.d("flag", "---------->bytes: " +bytes.toString());
                Message message = Message.obtain();//从消息池中拿减少内存损耗 节省内存空间
                //将需要传递到主线程的数据放到Message对象的obj属性中,以便于传递到主线程
                message.obj = bytes;
                //Message对象中的what属性是为了区别信息种类,方便主线程根据这些类别做出相应的操作
                message.what = 1;
                //handler对象携带Message中的数据返回主线程
                mHandler.sendMessage(message);

            }
        }).start();
    }

    private void initView() {
        content_title = (TextView) findViewById(R.id.content_title);
        content_time = (TextView) findViewById(R.id.content_time);
        content_laiyuan = (TextView) findViewById(R.id.content_laiyuan);

        webView = (WebView) findViewById(R.id.webView);

        callback = (ImageView) findViewById(R.id.callback);
        share = (ImageView) findViewById(R.id.share);
        collect = (ImageView) findViewById(R.id.collect);

        button = (LinearLayout) findViewById(R.id.button);


        mLinearLayout = (LinearLayout) findViewById(R.id.activity_context);

    }


    //三个按钮的点击事件
    public void click(View view) {
        switch (view.getId()){
            case R.id.callback://返回键
                this.finish();
                break;
            case R.id.share://分享键

                break;
            case R.id.collect://收藏键

                if(NetworkUtils.isConnected(this)){//如果有网络的时候就可以进行收藏

                    //将这条数据添加到数据库中


                    //数据库不允许插入空数据
                    //如果想要插入空数据，需要对参数二指定一个字段
                    //参数给null就可以了

                    //可以将数据放到values
                    ContentValues values = new ContentValues();

                    //查询收藏数据库中是否有符合条件的Id
                    Cursor cursor = db.query("shoucang", new String[]{"_id", "title", "laiyuan","name","time","id"},
                            "id=?", new String[]{mContent.getId()}, null, null, null);

                    long row = 0;
                    if(!cursor.moveToNext()) {

                        values.put("title", mContent.getTitle());
                        values.put("laiyuan", mContent.getSource());
                        values.put("name", mContent.getAuthor());
                        values.put("time", mContent.getCreate_time());
                        values.put("id", mContent.getId());
                        //添加到数据库
                        row = db.insert("shoucang", null, values);

                        if(row != -1){
                            Toast.makeText(this,"已经收藏成功！",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(this,"未能收藏成功，请重新尝试或联系客服",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(this,"您已经收藏过此文章！",Toast.LENGTH_SHORT).show();
                    }


                    db.close();
                }else{//如果没有网络时就不能进行收藏
                    Toast.makeText(this,"无网络不可以收藏",Toast.LENGTH_SHORT).show();

                }


                break;
        }
    }
}
