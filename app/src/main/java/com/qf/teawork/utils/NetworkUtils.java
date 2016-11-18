package com.qf.teawork.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by my on 2016/11/16.
 */

public class NetworkUtils {

    public static boolean isConnected(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


        //获取当前活跃网络状态
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if(activeNetworkInfo == null){
            return false;
        }switch (activeNetworkInfo.getType()){
            case ConnectivityManager.TYPE_WIFI:

                return true;
            case ConnectivityManager.TYPE_MOBILE:

                return true;
        }

        return false;
    }
}
