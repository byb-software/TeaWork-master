package com.qf.teawork.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.qf.teawork.ContextActivity;
import com.qf.teawork.MainActivity;
import com.qf.teawork.R;
import com.qf.teawork.adapter.MyBaseAdapter;
import com.qf.teawork.asynctask.ImageAsyncTask;
import com.qf.teawork.callback.BitmapCallback;
import com.qf.teawork.entyties.HeaderView;
import com.qf.teawork.entyties.Tea;
import com.qf.teawork.fragment.adapter.MyViewPagerAdapter;
import com.qf.teawork.utils.HttpUtils;
import com.qf.teawork.utils.MyLruCache;
import com.qf.teawork.utils.MySQLite;
import com.qf.teawork.utils.NetworkUtils;
import com.qf.teawork.utils.SdCardUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.animation.Animation.RELATIVE_TO_SELF;
import static com.qf.teawork.R.id.header;
import static com.qf.teawork.R.id.listView;
import static com.qf.teawork.utils.Uri.BASE_URL;
import static com.qf.teawork.utils.Uri.CONSULT_TYPE;
import static com.qf.teawork.utils.Uri.CYCLOPEDIA_TYPE;
import static com.qf.teawork.utils.Uri.DATA_TYPE;
import static com.qf.teawork.utils.Uri.HEADERIMAGE_URL;
import static com.qf.teawork.utils.Uri.HEADLINE_TYPE;
import static com.qf.teawork.utils.Uri.HEADLINE_URL;
import static com.qf.teawork.utils.Uri.OPERATE_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    private TextView empty, viewTitle;
    private View view1, view2, view3;

    //添加具有下拉功能的ListView
    private PullToRefreshListView mPullToRefreshListView;

    private int lastview = 1;

    //ListView里面的数据源
    private List<Tea> data = new ArrayList<>();
    //头布局的数据源
    private List<HeaderView> headerData = new ArrayList<>();
    //头布局中的图片数据源
    private List<ImageView> imgData = new ArrayList<>();


    private BaseAdapter adapter = null;
    private PagerAdapter mPagerAdapter = null;
    private LoadingLayoutProxy mLoadingLayoutProxy;

    private int page = 1;

    private int currentPosition = 0;

    private MyLruCache mMyLruCache;

    private  ImageView backTop;

    private Handler mHandler = new Handler() {

        private byte[] mBytes;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what > 9) {//110说明进来的数据是头布局
                switch (msg.what) {
                    case 10://进行ViewPager切换
                        if (this.hasMessages(10)) {
                            //移出多个Message，保证只有一个
                            this.removeMessages(10);
                        }
                        currentPosition++;
                        if (currentPosition == 3) {
                            currentPosition = 0;
                        }
                        // TODO 实现ViewPager的自动切换，有问题
                        //    Log.d("flag", "---------->currentPosition: " +currentPosition);
                        mViewPager.setCurrentItem(currentPosition);
                        this.sendEmptyMessageDelayed(10, 3000);

                        break;
                    case 11:
                        if (this.hasMessages(10)) {
                            //移出了Message，自动的切换就会停止
                            this.removeMessages(10);
                        }
                        break;
                    case 12:
                        //手滑动的时候，页码变，需要对页码重新赋值
                        currentPosition = msg.arg1;
                        this.sendEmptyMessageDelayed(10, 3000);
                        break;

                    case 110:

                        mBytes = (byte[]) msg.obj;

                        //   Log.d("flag", "---------->handleMessage: mBytes" +mBytes.length);

                        JSONObject jsonObject = null;

                        try {
                            Log.d("flag", "---------->mBytes: " +new String(mBytes));

                            //将信息存储在磁盘里
                            String root = getContext().getExternalCacheDir().getAbsolutePath();
                            String fileName = "viewpager";

                            SdCardUtils.saveFile(mBytes,root,fileName);

                            //解析
                            jsonObject = new JSONObject(new String(mBytes));
                            JSONArray jsonArray = jsonObject.optJSONArray("data");
                            headerData = JSON.parseArray(jsonArray.toString(), HeaderView.class);
                            //  Log.d("flag", "---------->handleMessage: headerData" +headerData);

                            //取出图片放进图片的集合中
                            for (int i = 0; i < 3; i++) {
                                final ImageView imageView = new ImageView(getContext());

                                final String image = headerData.get(i).getImage();
                                Bitmap bitmap = getBitmap(image);

                                if(bitmap!=null){//说明不是第一次，同时也获取到了数据
                                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                    imageView.setImageBitmap(bitmap);
                                    imgData.add(imageView);

                                    mPagerAdapter.notifyDataSetChanged();
                                }else{//说明是第一次这里从网上获取图片

                                    new ImageAsyncTask(new BitmapCallback() {
                                        @Override
                                        public void callbackBitmap(byte[] bitmap) {
                                            // TODO Auto-generated method stub
                                            if (bitmap != null) {
                                                Bitmap bitmap1 = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
                                                // Log.d("flag", "---------->callbackBitmap: bitmap1" +bitmap1);
                                                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                                imageView.setImageBitmap(bitmap1);
                                                imgData.add(imageView);
//                                    Log.d("flag", "---------->callbackBitmap: imgData" +imgData.size());
                                                //  Log.d("flag", "---------->callbackBitmap: " + mPagerAdapter);

                                                //存起来
                                                //存到内存中,第一级缓存
                                                mMyLruCache.put(image.substring(image.lastIndexOf("/")+1),bitmap);

                                                //存入磁盘中，第二级缓存
                                                String root = getContext().getExternalCacheDir().getAbsolutePath();
                                                SdCardUtils.saveFile(bitmap,root,image.substring(image.lastIndexOf("/")+1));

                                                mPagerAdapter.notifyDataSetChanged();
                                            }
                                        }

                                    }).execute(headerData.get(i).getImage());

                                }
                            }

                            //   Log.d("flag", "---------->imgData1: " + imgData.size());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 100:
                        if(mPullToRefreshListView.isRefreshing()){
                            mPullToRefreshListView.onRefreshComplete();
                        }
                        break;
                }

            } else {//这里说明进来的ListView数据

                switch (msg.what) {
                    case 0:
                        mBytes = (byte[]) msg.obj;
                        break;
                    case 1:
                        mBytes = (byte[]) msg.obj;
                        break;
                    case 2:
                        mBytes = (byte[]) msg.obj;
                        break;
                    case 3:
                        mBytes = (byte[]) msg.obj;
                        break;
                    case 4:
                        mBytes = (byte[]) msg.obj;
                        break;
                }

                JSONObject jsonObject = null;
                try {

                    //保存数据到sd卡
                    String root = getContext().getExternalCacheDir().getAbsolutePath();
                    String fileName = "tea";
                    Log.d("flag", "----------------->handleMessage: " +fileName);
                    SdCardUtils.saveFile(mBytes,root,fileName);


                    //解析
                    jsonObject = new JSONObject(new String(mBytes));
                    JSONArray jsonArray = jsonObject.optJSONArray("data");
                    List<Tea> teas = JSON.parseArray(jsonArray.toString(), Tea.class);
                    // Log.d("flag", "---------->handleMessage: " +data.toString());
                    data.addAll(teas);
                    adapter.notifyDataSetChanged();
                    if (mPullToRefreshListView.isRefreshing()) {//当刷新完成后，正在刷新的行为停止掉
                        mPullToRefreshListView.onRefreshComplete();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };
    private ViewPager mViewPager;
    private ListView mListView;

    //声明数据库的使用
    private MySQLite dbHelper;
    private SQLiteDatabase db;
    private Cursor cursor;


    public MyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        //确定内存分配，这里给整个内存的八分之一
        mMyLruCache = new MyLruCache(maxSize);


        // TODO 设置头布局的三级缓存机制
        if(NetworkUtils.isConnected(getContext())){//有网时从网络上获取数据
            //设置头布局的数据源
            new Thread(new Runnable() {
                @Override
                public void run() {

                    byte[] bytes = HttpUtils.loadByte(HEADERIMAGE_URL);
                    // Log.d("flag", "---------->run: ViewPager数据" +bytes.length);
                    Message message = Message.obtain();//从消息池中拿减少内存损耗 节省内存空间
                    //将需要传递到主线程的数据放到Message对象的obj属性中,以便于传递到主线程
                    message.obj = bytes;
                    //Message对象中的what属性是为了区别信息种类,方便主线程根据这些类别做出相应的操作
                    message.what = 110;
                    //handler对象携带Message中的数据返回主线程
                    mHandler.sendMessage(message);

                }
            }).start();
        }else{//没有网络时从磁盘中获取数据

            String root = getContext().getExternalCacheDir().getAbsolutePath();
            String fileName = root + File.separator + "viewpager";
            byte[] bytes = SdCardUtils.getbyteFromFile(fileName);
            if(bytes!=null){//从磁盘中获取
                Message message = Message.obtain();//从消息池中拿减少内存损耗 节省内存空间
                //将需要传递到主线程的数据放到Message对象的obj属性中,以便于传递到主线程
                message.obj = bytes;
                //Message对象中的what属性是为了区别信息种类,方便主线程根据这些类别做出相应的操作
                message.what = 110;
                //handler对象携带Message中的数据返回主线程
                mHandler.sendMessage(message);
            }
        }



/**
 * 这里ListView的适配器一定要写在onCreate方法里，因为这里的方法程序开启后只会执行一次。
 * 如果把初始化适配器放在onCreateView里面，就会因为ViewPager的滑动而不断新建，
 * 但是之前的数据会保留，所以，适配器中的数据源会一直重复往上加
 */
        //设置适配器
        mPagerAdapter = new MyViewPagerAdapter(imgData);

        //这里的dbHelper代表的就是确定的数据库的工具类
        dbHelper = new MySQLite(getContext());

        db = dbHelper.getReadableDatabase();
    }




    //这里是根据图片的地址来取出缓存中数据
    private Bitmap getBitmap(String imgPath) {

        String path = imgPath.substring(imgPath.lastIndexOf("/")+1);
        // Log.d("flag", "---------->getBitmap:path " +path);
        //取出内存中的图片字节数组
        byte[] bytes = mMyLruCache.get(path);
        //将图片的字节数组组合成一张图片
        // Log.d("flag", "---------->getBitmap: " +bytes);


        if(bytes!=null){//说明缓存中有图片数据
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            return bitmap;

        }else{//说明缓存中没有图片数据
            //再从二级的磁盘中获取图片数据
            String root = getContext().getExternalCacheDir().getAbsolutePath();
            String fileName = root+ File.separator+path;
            Log.d("flag", "---------->fileName: " +fileName);
            byte[] bytes1 = SdCardUtils.getbyteFromFile(fileName);
            if(bytes1!=null){
                Bitmap bitmapsd = BitmapFactory.decodeByteArray(bytes1,0,bytes1.length);
                //这里将磁盘中的数据保存到缓存中
                mMyLruCache.put(path,bytes1);

                return bitmapsd;
            }

            return null;
        }

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        //  Log.d("flag", "---------->onCreateView: 12" );
        View ret = inflater.inflate(R.layout.fragment_my, container, false);

        initView(ret);


        Bundle arguments = getArguments();
        if (arguments != null) {
            int key = (int) arguments.get("key");
            //  Log.d("flag", "---------->initListView(key): " +key);
            initPullToRefreshListView(key);
        }

        //设置下拉刷新的头布局
        initLoadingLayout();

        return ret;
    }


    //设置PullToRefreshListView下拉刷新的头布局
    private void initLoadingLayout() {

        mLoadingLayoutProxy = (LoadingLayoutProxy) mPullToRefreshListView.getLoadingLayoutProxy();

        mLoadingLayoutProxy.setPullLabel("下拉刷新");

        mLoadingLayoutProxy.setReleaseLabel("释放更新");

        mLoadingLayoutProxy.setRefreshingLabel("拼命加载中……");

        //显示刷新时间
//        TextUtils.isEmpty()
        String time = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(),
                DateUtils.FORMAT_ABBREV_TIME | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE);


        mLoadingLayoutProxy.setLastUpdatedLabel(time);
        //该方法就是设置加载，动画，有时候，代码中设置，不起作用（PullToRefresh拉动模式：BOTH时，不起作用）
        //建议在布局中，使用自定义属性进行设置
//        mLoadingLayoutProxy.setLoadingDrawable();


    }


    private void initPullToRefreshListView(final int key) {

        mListView = mPullToRefreshListView.getRefreshableView();

        //添加尾布局
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        TextView footer = new TextView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,35*getResources().getDisplayMetrics().densityDpi/160);
        footer.setText("点击加载更多");
        footer.setGravity(Gravity.CENTER_HORIZONTAL);
        footer.setLayoutParams(params);
        relativeLayout.addView(footer);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkUtils.isConnected(getContext())){
                    page++;
                    initData(HEADLINE_URL + HEADLINE_TYPE + page, 0);
                }else{
                    Toast.makeText(getContext(),"没有网络连接",Toast.LENGTH_SHORT).show();
                }

            }
        });
        mListView.addFooterView(relativeLayout);

        if (key == 0) {
            initData(HEADLINE_URL + HEADLINE_TYPE + page, 0);

            adapter = new MyBaseAdapter(data, getContext());


            View headerView = LayoutInflater.from(getContext()).inflate(R.layout.list_header, mListView, false);
            mListView.addHeaderView(headerView);

            initViewPager(headerView);

            mPullToRefreshListView.setAdapter(adapter);


            mPullToRefreshListView.setEmptyView(empty);

            mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                @Override//向下拉刷新
                public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                    if(NetworkUtils.isConnected(getContext())){
                        data.clear();
                        initData(HEADLINE_URL + HEADLINE_TYPE + 1, 0);

                    }else{
                        mHandler.sendEmptyMessageDelayed(100,2000);
                        Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                    }


                }

                @Override//向上拉刷新
                public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                    if(NetworkUtils.isConnected(getContext())){
                        page++;
                        initData(HEADLINE_URL + HEADLINE_TYPE + page, 0);
                    }else{
                        mHandler.sendEmptyMessageDelayed(100,2000);
                        Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            //设置刷新模式
            mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            switch (key) {
                case 1:
                    initData(BASE_URL + CYCLOPEDIA_TYPE + page, 1);
                    adapter = new MyBaseAdapter(data, getContext());

                    mPullToRefreshListView.setAdapter(adapter);
                    mPullToRefreshListView.setEmptyView(empty);

                    mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override//向下拉刷新
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                initData(BASE_URL + CYCLOPEDIA_TYPE + page, 1);
                                adapter.notifyDataSetChanged();
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }


                        }

                        @Override//向上拉刷新
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                page++;
                                initData(BASE_URL + CYCLOPEDIA_TYPE + page, 1);
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    //设置刷新模式
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                    break;
                case 2:
                    initData(BASE_URL + CONSULT_TYPE + page, 2);
                    adapter = new MyBaseAdapter(data, getContext());
                    mPullToRefreshListView.setAdapter(adapter);
                    mPullToRefreshListView.setEmptyView(empty);

                    mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override//向下拉刷新
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                initData(BASE_URL + CONSULT_TYPE + page, 2);
                                adapter.notifyDataSetChanged();
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override//向上拉刷新
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                page++;
                                initData(BASE_URL + CONSULT_TYPE + page, 2);
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //设置刷新模式
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                    break;
                case 3:
                    initData(BASE_URL + CONSULT_TYPE + page, 2);
                    adapter = new MyBaseAdapter(data, getContext());
                    mPullToRefreshListView.setAdapter(adapter);
                    mPullToRefreshListView.setEmptyView(empty);

                    mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override//向下拉刷新
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                initData(BASE_URL + OPERATE_TYPE + page, 3);
                                adapter.notifyDataSetChanged();
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override//向上拉刷新
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                page++;
                                initData(BASE_URL + OPERATE_TYPE + page, 3);
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//设置刷新模式
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                    break;
                case 4:

                    initData(BASE_URL + CONSULT_TYPE + page, 2);
                    adapter = new MyBaseAdapter(data, getContext());
                    mPullToRefreshListView.setAdapter(adapter);
                    mPullToRefreshListView.setEmptyView(empty);

                    mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
                        @Override//向下拉刷新
                        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

                            if(NetworkUtils.isConnected(getContext())){
                                initData(BASE_URL + DATA_TYPE + page, 4);
                                adapter.notifyDataSetChanged();
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override//向上拉刷新
                        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                            if(NetworkUtils.isConnected(getContext())){
                                page++;
                                initData(BASE_URL + DATA_TYPE + page, 4);
                            }else{
                                mHandler.sendEmptyMessageDelayed(100,2000);
                                Toast.makeText(getContext(),"请检查您的网络链接",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
//设置刷新模式
                    mPullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
                    break;
            }

        }


        //   Log.d("flag", "---------->setOnItemLongClickListener: 是否有执行" +mListView);
//长按删除功能
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                //设置
                builder.setIcon(R.mipmap.icon_dialog);

                builder.setTitle("提示");

                builder.setMessage("亲啊，你真的要删除吗");

                builder.setNegativeButton("取消", null);

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       // int width = getContext().getResources().getDisplayMetrics().widthPixels / 3;

                        final TranslateAnimation translate = new TranslateAnimation(
                                RELATIVE_TO_SELF, 0,
                                RELATIVE_TO_SELF, -1,//移动自身的宽度，参数二view
                                RELATIVE_TO_SELF, 0,
                                RELATIVE_TO_SELF, 0);

                        translate.setDuration(2000);

                        view.startAnimation(translate);

                        //动画的监听
                        translate.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {//启动

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {//结束

                                //将数据删除，并且更新适配器
                                if (key == 0) {
                                    data.remove(position - 2);
                                } else {
                                    data.remove(position - 1);
                                }

                              //  Log.d("flag", "---------->onAnimationEnd: " + data.toString());

                                //刷新适配器
                                adapter.notifyDataSetChanged();


                                //得到总条目的数量
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




                //目的
                builder.create().show();
                return true;
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Intent intent = new Intent(getContext(), ContextActivity.class);
                String dataId = null;
                if (key == 0) {
                    dataId = data.get(position - 2).getId();
                } else {
                    dataId = data.get(position - 1).getId();
                    }

                intent.putExtra("id", dataId);
                intent.putExtra("activity", "context");
                MyFragment.this.startActivity(intent);
                // Log.d("flag", "---------->onItemClick: " +data.get(position).getId());


                }




        });


    }

    private void initViewPager(View headerView) {
        // TODO 这里对头布局中的图片切换进行设置
        mViewPager = (ViewPager) headerView.findViewById(header);
        viewTitle = (TextView) headerView.findViewById(R.id.viewTitle);
        viewTitle.setText("茶百科androidV1.2新功能简介");

        view1 = headerView.findViewById(R.id.view1);
        view2 = headerView.findViewById(R.id.view2);
        view3 = headerView.findViewById(R.id.view3);
        view1.setEnabled(true);
        view2.setEnabled(false);
        view3.setEnabled(false);

        mViewPager.setAdapter(mPagerAdapter);

        //发送信息进行自动切换
        mHandler.sendEmptyMessageDelayed(10, 3000);

        //设置ViewPager的滑动监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //实现ViewPager的自动切换
                mHandler.sendMessage(Message.obtain(mHandler, 12, position, 0));

                switch (position) {
                    case 0:
                        view1.setEnabled(false);
                        viewTitle.setText(headerData.get(0).getTitle());
                        view2.setEnabled(true);

                        view3.setEnabled(true);


                        break;
                    case 1:
                        view2.setEnabled(false);
                        //    Log.d("flag", "---------->onPageSelected: " +headerData.get(1).getTitle());
                        viewTitle.setText(headerData.get(1).getTitle());


                        view1.setEnabled(true);

                        view3.setEnabled(true);


                        break;
                    case 2:
                        view3.setEnabled(false);
                        viewTitle.setText(headerData.get(2).getTitle());
                        view1.setEnabled(true);
                        view2.setEnabled(true);

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING://手正在拖拽
                        mHandler.sendEmptyMessage(11);
                        break;
                }
            }
        });


        //设置头布局的ViewPager与外层Main中的ViewPager里面的滑动冲突
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ((MainActivity) getContext()).getViewPager().requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });


    }


    private void initData(final String path, final int i) {

        //添加三级缓存，当没有网络连接时，通过三级缓存取数据
        if(NetworkUtils.isConnected(getContext())){//如果有网的话进行联网获取数据

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = HttpUtils.loadByte(path);
                Message message = Message.obtain();//从消息池中拿减少内存损耗 节省内存空间
                //将需要传递到主线程的数据放到Message对象的obj属性中,以便于传递到主线程
                message.obj = bytes;
                //Message对象中的what属性是为了区别信息种类,方便主线程根据这些类别做出相应的操作
                message.what = i;
                //handler对象携带Message中的数据返回主线程
                mHandler.sendMessage(message);
            }

        }).start();

        }else{//没有网络的话就从缓存中取出数据
            //从磁盘中获取文字等信息

            String root = getContext().getExternalCacheDir().getAbsolutePath();
            String fileName = root + File.separator+"tea";
            byte[] bytes = SdCardUtils.getbyteFromFile(fileName);
            if(bytes!= null){
                Message message = Message.obtain();//从消息池中拿减少内存损耗 节省内存空间
                //将需要传递到主线程的数据放到Message对象的obj属性中,以便于传递到主线程
                message.obj = bytes;
                //Message对象中的what属性是为了区别信息种类,方便主线程根据这些类别做出相应的操作
                message.what = i;
                //handler对象携带Message中的数据返回主线程
                mHandler.sendMessage(message);
            }

        }
    }

    private void initView(View ret) {
        mPullToRefreshListView = (PullToRefreshListView) ret.findViewById(listView);
        empty = (TextView) ret.findViewById(R.id.empty);
        backTop = (ImageView) ret.findViewById(R.id.backTop);
        backTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.setSelection(0);
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
