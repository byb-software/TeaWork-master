package com.qf.teawork;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.qf.teawork.adapter.MyBaseAdapter;
import com.qf.teawork.entyties.Tea;
import com.qf.teawork.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.qf.teawork.utils.Uri.SEARCH_URL;

public class SearchActivity extends AppCompatActivity {

    private ListView mListView;
    private TextView title,empty;
    private ImageView callback,home;

    //网址
    private String mPath = null;

    //数据源
    private List<Tea> data = new ArrayList<>();

    //适配器
    private BaseAdapter adapter = null;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case 1:

                    byte[] bytes = (byte[]) msg.obj;

                    //解析
                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));
                         JSONArray jsonArray = jsonObject.optJSONArray("data");
                         data.addAll(JSON.parseArray(jsonArray.toString(),Tea.class));

                           title.setText(titleData);
                            adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };
    private String titleData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();

        titleData = intent.getStringExtra("key");

        Log.d("flag", "---------->key: " +titleData);
        mPath = SEARCH_URL + titleData;

        initView();
        initData();
        initListView();

    }

    private void initListView() {
        adapter = new MyBaseAdapter(data,this);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(empty);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent1 = new Intent(SearchActivity.this,ContextActivity.class);
                intent1.putExtra("activity","search");
                intent1.putExtra("id",data.get(position).getId());
                SearchActivity.this.startActivity(intent1);
            }
        });
    }

    private void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {

               byte[] bytes = HttpUtils.loadByte(mPath);
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

        mListView = (ListView) findViewById(R.id.listView);
        title = (TextView) findViewById(R.id.title);
        empty = (TextView) findViewById(R.id.empty);
        callback = (ImageView) findViewById(R.id.callback);
        home = (ImageView) findViewById(R.id.home);

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
        }
    }
}
