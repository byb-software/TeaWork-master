package com.qf.teawork.asynctask;


import android.os.AsyncTask;
import android.util.Log;

import com.qf.teawork.callback.BitmapCallback;
import com.qf.teawork.utils.HttpUtils;

/**
 * Created by my on 2016/11/12.
 */

public class ImageAsyncTask extends AsyncTask<String,Void,byte[]> {

    private BitmapCallback bitmapCallback;

    public ImageAsyncTask(BitmapCallback bitmapCallback) {
        super();
        this.bitmapCallback = bitmapCallback;
    }

    @Override
    protected byte[] doInBackground(String... params) {

        byte[] data = HttpUtils.loadByte(params[0]);


        return data;
    }

    @Override
    protected void onPostExecute(byte[] bitmap) {
        super.onPostExecute(bitmap);
        if (bitmap != null) {
            bitmapCallback.callbackBitmap(bitmap);
        }else {
            Log.d("flag", "----------------->onPostExecute: 图片的异步任务失败");
        }

    }
}
