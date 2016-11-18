package com.qf.teawork.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 这里是储存到磁盘的工具类
 */

public class SdCardUtils {

    //将文件存到磁盘中
    public static void saveFile(byte[] bytes,String root,String fileName){

        File file = new File(root,fileName);

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(bytes,0,bytes.length);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static byte[] getbyteFromFile(String fileName){

        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;

        try {
            Log.d("flag", "---------->fileName:" +fileName);
            fis = new FileInputStream(fileName);
             baos = new ByteArrayOutputStream();

            byte[] buf = new byte[1024*8];

            int len = 0;

            while ((len = fis.read(buf))!=-1){
                Log.d("flag", "---------->getbyteFromFile:buf " +buf.length);
                baos.write(buf,0,buf.length);
                Log.d("flag", "---------->baos.toByteArray(): " +baos.toByteArray().length);
            }
            return baos.toByteArray();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fis!=null){
                try {
                    fis.close();
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
