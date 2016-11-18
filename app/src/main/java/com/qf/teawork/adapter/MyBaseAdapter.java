package com.qf.teawork.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.qf.teawork.R;
import com.qf.teawork.asynctask.ImageAsyncTask;
import com.qf.teawork.callback.BitmapCallback;
import com.qf.teawork.entyties.Tea;
import com.qf.teawork.utils.MyLruCache;
import com.qf.teawork.utils.SdCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by my on 2016/11/12.
 */
public class MyBaseAdapter extends BaseAdapter {

    private List<Tea> data;
    private Context context;

    //这里将图片储存到缓存中
    private MyLruCache mMyLruCache;

    public MyBaseAdapter(List<Tea> data, Context context) {
        this.data = data;
        this.context = context;

        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        //确定内存分配，这里给整个内存的八分之一
        mMyLruCache = new MyLruCache(maxSize);
    }

    @Override
    public int getCount() {
        return data!=null?data.size():0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View ret = null;
        ViewHolder holder = null;

        if(convertView!=null){//说明可以复用
            ret = convertView;
            holder = (ViewHolder) ret.getTag();
        }else{

            ret = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

            holder = new ViewHolder();

            holder.title = (TextView) ret.findViewById(R.id.title);
            holder.laiyuan = (TextView) ret.findViewById(R.id.laiyuan);
            holder.name = (TextView) ret.findViewById(R.id.name);
            holder.time = (TextView) ret.findViewById(R.id.time);
            holder.image = (ImageView) ret.findViewById(R.id.image);

            ret.setTag(holder);

        }

        //赋值
        holder.title.setText(data.get(position).getTitle());
        holder.laiyuan.setText(data.get(position).getSource());
        holder.name.setText(data.get(position).getNickname());
        holder.time.setText(data.get(position).getCreate_time());

        //获取网络图片
//设置图片
        final String imgPath = data.get(position).getWap_thumb();

        //局部变量接收holder
        final ViewHolder finalHolder = holder;

        //设置图片的三级缓存，这里将图片的字节形式存到缓存中
    //   Log.d("flag", "---------->imgPath: " +imgPath);
        Bitmap bitmap = getBitmap(imgPath);
        if(bitmap!=null){//说明不是第一次，已经取得了数据

            finalHolder.image.setScaleType(ImageView.ScaleType.FIT_XY);
            finalHolder.image.setImageBitmap(bitmap);

        }else{//说明是第一次，所以必须从网络上下载

            //通过SetTag,赋值之前打一个标签
            holder.image.setTag(imgPath);

            new ImageAsyncTask(new BitmapCallback() {

                @Override
                public void callbackBitmap(byte[] bitmap) {
                    // TODO Auto-generated method stub
                    Object tag = finalHolder.image.getTag();
                    if(bitmap!=null&& imgPath.equals((String)tag)){
                        Bitmap bitmap1 = BitmapFactory.decodeByteArray(bitmap,0,bitmap.length);
                        finalHolder.image.setScaleType(ImageView.ScaleType.FIT_XY);
                        finalHolder.image.setImageBitmap(bitmap1);

                        //存起来
                        //存到内存中,第一级缓存
                        mMyLruCache.put(imgPath.substring(imgPath.lastIndexOf("/")+1),bitmap);

                        //存入磁盘中，第二级缓存
                        String root = context.getExternalCacheDir().getAbsolutePath();
                        SdCardUtils.saveFile(bitmap,root,imgPath.substring(imgPath.lastIndexOf("/")+1));
                    }

                }
            }).execute(imgPath);

        }


        return ret;
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
            String root = context.getExternalCacheDir().getAbsolutePath();
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

    public static class ViewHolder{
        private TextView title,laiyuan,name,time;
        private ImageView image;
    }
}
