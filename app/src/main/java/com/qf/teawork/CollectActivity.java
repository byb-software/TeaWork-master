package com.qf.teawork;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qf.teawork.utils.MySQLite;

import static android.view.animation.Animation.RELATIVE_TO_SELF;
import static com.qf.teawork.R.id.listView;

public class CollectActivity extends AppCompatActivity {

    //声明数据库的使用
    private MySQLite dbHelper;
    private SQLiteDatabase db;

    //控件
    private ListView mListView;
    private ImageView callback;
    private ImageView home;
    private TextView empty;

    //数据源
    private Cursor cursor;


    //适配器
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);

        //这里的dbHelper代表的就是确定的数据库的工具类
        dbHelper = new MySQLite(this);

        db = dbHelper.getReadableDatabase();


        initView();

        initData();

        initListView();

    }

    private void initListView() {
        // TODO Auto-generated method stub

        //参数四new String[]{必须是数据库中字段}
        //参数五，数据库中数据取出来放大哪个控件中显示
        adapter = new SimpleCursorAdapter(this, R.layout.shoucang,
                cursor, new String[]{"title","laiyuan","name","time"},
                new int[]{R.id.title,R.id.laiyuan,R.id.name,R.id.time},
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);//没有作用

        mListView.setAdapter(adapter);

        mListView.setEmptyView(empty);



        //长按删除功能
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CollectActivity.this);
                builder.setIcon(R.mipmap.ic_logo);
                builder.setTitle("收藏");
                builder.setMessage("您真的要删除这条吗");
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        final TranslateAnimation translate = new TranslateAnimation(
                                RELATIVE_TO_SELF, 0,
                                RELATIVE_TO_SELF, -1,//移动自身的宽度，参数二view
                                RELATIVE_TO_SELF, 0,
                                RELATIVE_TO_SELF, 0);

                        translate.setDuration(2000);

                        view.startAnimation(translate);


                        //设置动画的监听
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {//动画结束时
                                //删除点击的这个条目
                                String is = cursor.getString(cursor.getColumnIndex("id"));

                                int delete = db.delete("shoucang", "id = ?", new String[]{is});
                                Log.d("flag", "---------->删除的个数: " +delete);
                                //重新进行查询

                                initData();

                                //改变适配器中的cursor
                                adapter.changeCursor(cursor);

                              //  adapter.notifyDataSetChanged();

                                int count = mListView.getChildCount();

                                AnimationSet set = new AnimationSet(true);
                                //渐变
                                AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
                                alphaAnimation.setDuration(1000);

                                //平移，让剩下的条目向上平移
                                TranslateAnimation translateAnimation = new TranslateAnimation(RELATIVE_TO_SELF, 0,
                                        RELATIVE_TO_SELF, 0,//移动自身的宽度，参数二view
                                        RELATIVE_TO_SELF, 1,
                                        RELATIVE_TO_SELF, 0);
                                translateAnimation.setDuration(1000);
                                set.addAnimation(alphaAnimation);
                                set.addAnimation(translateAnimation);

                                int currentTop = view.getTop();
                                for (int i = 0; i < count; i++) {
                                    View itemView = mListView.getChildAt(i);

                                    //条件成立就说明这个条目是要删除条目的下面或自身
                                    //不成立就说明这个条目是要删除条目的上面条目
                                    if (itemView.getTop() >= currentTop)
                                        itemView.startAnimation(set);

                                }


                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                    }
                });

                builder.create().show();
                return true;
            }
        });





        //点击条目跳转到另一个页面
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                cursor.moveToPosition(position);
                String ids = cursor.getString(cursor.getColumnIndex("id"));
                Log.d("flag", "---------->onItemClick: ids" +ids);
                Intent intent = new Intent(CollectActivity.this,ContextActivity.class);
                  intent.putExtra("activity","shoucang");
                  intent.putExtra("id",ids);
                CollectActivity.this.startActivity(intent);
            }
        });

    }

    private void initData() {


            //查询数据库
            //查询的时候需要_id
            cursor = db.query("shoucang", new String[]{"_id", "title", "laiyuan","name","time","id"},
                    null, null, null, null, null);


    }

    private void initView() {
        mListView = (ListView) findViewById(listView);
        callback = (ImageView) findViewById(R.id.callback);
        home = (ImageView) findViewById(R.id.home);
        empty = (TextView) findViewById(R.id.empty);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        cursor.close();
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
