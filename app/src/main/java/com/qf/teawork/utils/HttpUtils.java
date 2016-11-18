package com.qf.teawork.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by my on 2016/11/12.
 */

public class HttpUtils {

    public static byte[] loadByte(String path) {
        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setConnectTimeout(5000);
            if(httpURLConnection.getResponseCode() == 200){
                  is = httpURLConnection.getInputStream();
                   baos = new ByteArrayOutputStream();
                int len = 0;
                byte[] buf = new byte[1024*10];
                while((len = is.read(buf))!=-1){
                    baos.write(buf,0,len);
                }
                return baos.toByteArray();

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(baos!=null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        return null;
    }
}
